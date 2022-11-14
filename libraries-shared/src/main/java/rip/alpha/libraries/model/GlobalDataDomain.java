package rip.alpha.libraries.model;

import it.unimi.dsi.fastutil.Pair;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.redisson.api.MapOptions;
import org.redisson.api.RLock;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.mongo.TypedMongoStorage;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.*;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class GlobalDataDomain<K, V> {

    private static final String LOCK_PREFIX = "LOCK_";

    private final DomainContext<K, V> domainContext;
    private final RMapCache<K, V> redisAccess;
    private final TypedMongoStorage<K, V> mongoStorage;
    private final RedissonClient redissonClient;
    private final Map<K, V> localCache = new ConcurrentHashMap<>();

    protected GlobalDataDomain(DomainContext<K, V> domainContext) {
        this.domainContext = domainContext;
        this.redissonClient = domainContext.getRedissonClient();
        this.mongoStorage = domainContext.createMapper();
        MapOptions<K, V> options = MapOptions.<K, V>defaults()
                .loader(this.mongoStorage)
                .writer(this.mongoStorage)
                .writeMode(MapOptions.WriteMode.WRITE_THROUGH);
        this.redisAccess = domainContext.getRedissonClient().getMapCache(domainContext.getNamespace(), options);
        Libraries.getBridge().registerListener(domainContext.namespace, GlobalDataUpdateEvent.class, new GlobalDataUpdateListener<>());
    }

    public CompletableFuture<Void> enableLocalCacheAsyncFor(K key) {
        return CompletableFuture.runAsync(() -> this.enableLocalCacheFor(key));
    }

    public void enableLocalCacheFor(K key) {
        if (this.isLocallyCached(key)) {
            throw new IllegalStateException("Cant enable cache twice: " + key);
        }
        this.localCache.put(key, this.getOrCreateRealTimeData(key));
    }

    public void disableLocalCacheFor(K key) {
        this.localCache.remove(key);
    }

    public V getCachedValue(K key) {
        if (!this.isLocallyCached(key)) {
            throw new IllegalStateException("Tried to get cached value for uncached key: " + key);
        }
        return this.localCache.get(key);
    }

    public void triggerLocalCacheRenew(K key) {
        if (!this.isLocallyCached(key)) {
            throw new IllegalStateException("Tried to renew cached value for uncached key: " + key);
        }
        this.localCache.put(key, this.getOrCreateRealTimeData(key));
    }

    public boolean isLocallyCached(K key) {
        return this.localCache.containsKey(key);
    }

    public V getOrCreateRealTimeData(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        AtomicReference<V> atomic = new AtomicReference<>();
        this.withRLock(key, () -> {
            V value = this.redisAccess.get(key);
            if (value == null) {
                value = this.domainContext.getCreator().apply(key);
                this.redisAccess.fastPut(key, value, 30, TimeUnit.MINUTES);
            }
            atomic.set(value);
        });
        return atomic.get();
    }

    public CompletableFuture<Void> forEachAsync(BiConsumer<K, V> consumer) {
        return CompletableFuture.runAsync(() -> this.forEach(consumer));
    }

    public void forEach(BiConsumer<K, V> consumer) {
        for (K key : this.fetchAllKeys()) {
            this.applyToData(key, data -> consumer.accept(key, data));
        }
    }

    public Set<K> fetchAllKeys() {
        return this.mongoStorage.loadAllKeys();
    }

    public boolean exists(K key) {
        return key != null && (this.redisAccess.containsKey(key) || this.mongoStorage.contains(key));
    }

    public CompletableFuture<Boolean> existsAsync(K key) {
        return CompletableFuture.supplyAsync(() -> this.exists(key));
    }

    public CompletableFuture<V> getOrCreateRealTimeDataAsync(K key) {
        return CompletableFuture.supplyAsync(() -> this.getOrCreateRealTimeData(key));
    }

    public void batchDeleteDataGloballySync(Collection<K> keys) {
        this.withAllLocs(keys, () -> keys.forEach(key -> {
            if (key == null) {
                throw new IllegalArgumentException("Key cant be null.");
            }
            this.redisAccess.remove(key);
            this.mongoStorage.delete(key);
        }));
        for (K key : keys) {
            if (this.isLocallyCached(key)) {
                this.disableLocalCacheFor(key);
                this.enableLocalCacheFor(key);
            }
        }
    }

    public CompletableFuture<Void> batchDeleteDataGloballyAsync(Collection<K> keys) {
        return CompletableFuture.runAsync(() -> this.batchDeleteDataGloballySync(keys));
    }

    public void deleteDataGloballySync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        this.withRLock(key, () -> {
            this.redisAccess.remove(key);
            this.mongoStorage.delete(key);
        });
        if (this.isLocallyCached(key)) {
            this.disableLocalCacheFor(key);
            this.enableLocalCacheFor(key);
        }
    }

    public CompletableFuture<Void> deleteDataGloballyAsync(K key) {
        return CompletableFuture.runAsync(() -> this.deleteDataGloballySync(key));
    }

    public void applyToBoth(K keyOne, K keyTwo, BiConsumer<V, V> action) {
        if (keyOne == null || keyTwo == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        this.withAllLocs(List.of(keyOne, keyTwo), () -> {
            V valueOne = this.redisAccess.getOrDefault(keyOne, this.domainContext.getCreator().apply(keyOne));
            V valueTwo = this.redisAccess.getOrDefault(keyTwo, this.domainContext.getCreator().apply(keyTwo));
            action.accept(valueOne, valueTwo);
            this.redisAccess.fastPut(keyOne, valueOne, 30, TimeUnit.MINUTES);
            this.redisAccess.fastPut(keyTwo, valueTwo, 30, TimeUnit.MINUTES);
        });
        Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), keyOne));
        Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), keyTwo));
    }

    public void applyToAll(Collection<K> keys, Consumer<V> action) {
        this.withAllLocs(keys, () -> keys.stream()
                .filter(Objects::nonNull)
                .map(key -> Pair.of(key, this.redisAccess.getOrDefault(key, this.domainContext.getCreator().apply(key))))
                .peek(pair -> action.accept(pair.value()))
                .forEach(pair -> this.redisAccess.fastPut(pair.key(), pair.value(), 30, TimeUnit.MINUTES)));
    }

    public void applyToData(K key, Consumer<V> action) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        this.withRLock(key, () -> {
            V value = this.redisAccess.getOrDefault(key, this.domainContext.getCreator().apply(key));
            action.accept(value);
            this.redisAccess.fastPut(key, value, 30, TimeUnit.MINUTES);
        });
        Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), key));
    }

    public CompletableFuture<Void> applyToDataAsync(K key, Consumer<V> action) {
        return CompletableFuture.runAsync(() -> this.applyToData(key, action));
    }

    public <T> T npCompute(Collection<K> keys, Function<Collection<V>, T> computation) {
        return this.withAllLocksCallback(keys, () -> {
            Map<K, V> map = new HashMap<>();
            for (K key : keys) {
                map.put(key, this.redisAccess.getOrDefault(key, this.domainContext.getCreator().apply(key)));
            }
            T computed = computation.apply(map.values());
            map.forEach((key, value) -> this.redisAccess.fastPut(key, value, 30, TimeUnit.MINUTES));
            CompletableFuture.runAsync(() -> {
                for (K key : map.keySet()) {
                    Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), key));
                }
            });
            return computed;
        });
    }

    public <T> CompletableFuture<T> biComputeAsync(K keyOne, K keyTwo, BiFunction<V, V, T> computation) {
        return CompletableFuture.supplyAsync(() -> this.biCompute(keyOne, keyTwo, computation));
    }

    public <T> T biCompute(K keyOne, K keyTwo, BiFunction<V, V, T> computation) {
        if (keyOne == null || keyTwo == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        return this.withAllLocksCallback(List.of(keyOne, keyTwo), () -> {
            V valueOne = this.redisAccess.getOrDefault(keyOne, this.domainContext.getCreator().apply(keyOne));
            V valueTwo = this.redisAccess.getOrDefault(keyTwo, this.domainContext.getCreator().apply(keyTwo));
            T computed = computation.apply(valueOne, valueTwo);
            this.redisAccess.fastPut(keyOne, valueOne, 30, TimeUnit.MINUTES);
            this.redisAccess.fastPut(keyTwo, valueTwo, 30, TimeUnit.MINUTES);
            CompletableFuture.runAsync(() -> {
                Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), keyOne));
                Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), keyTwo));
            });
            return computed;
        });
    }

    public <T> CompletableFuture<T> computeAsync(K key, Function<V, T> computation) {
        return CompletableFuture.supplyAsync(() -> this.compute(key, computation));
    }

    public <T> T compute(K key, Function<V, T> computation) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        return this.withRLockCallback(key, () -> {
            V value = this.redisAccess.getOrDefault(key, this.domainContext.getCreator().apply(key));
            T computed = computation.apply(value);
            this.redisAccess.fastPut(key, value, 30, TimeUnit.MINUTES);
            Libraries.getBridge().callEvent(this.domainContext.namespace, new GlobalDataUpdateEvent<>(this.domainContext.asKey(), key));
            return computed;
        });
    }

    private <T> T withRLockCallback(K key, Supplier<T> supplier) {
        RLock lock = this.domainContext.getRedissonClient().getLock(LOCK_PREFIX + this.domainContext.namespace + key);
        T value = null;
        try {
            lock.lock(30, TimeUnit.SECONDS);
            value = supplier.get();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
        return value;
    }

    private <T> T withAllLocksCallback(Collection<K> keys, Supplier<T> supplier) {
        Collection<RLock> locks = keys.stream().map(key -> this.redissonClient.getLock(LOCK_PREFIX + this.domainContext.namespace + key)).toList();
        T value = null;
        try {
            locks.forEach(RLock::lock);
            value = supplier.get();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            locks.forEach(RLock::unlock);
        }
        return value;
    }

    private void withRLock(K key, Runnable runnable) {
        RLock lock = this.domainContext.getRedissonClient().getLock(LOCK_PREFIX + this.domainContext.namespace + key);
        try {
            lock.lock(30, TimeUnit.SECONDS);
            runnable.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    private void withAllLocs(Collection<K> keys, Runnable runnable) {
        Collection<RLock> locks = keys.stream().map(key -> this.redissonClient.getLock(LOCK_PREFIX + this.domainContext.namespace + key)).toList();
        try {
            locks.forEach(RLock::lock);
            runnable.run();
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            locks.forEach(RLock::unlock);
        }
    }
}
