package rip.alpha.libraries.model;

import org.redisson.api.MapOptions;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import rip.alpha.libraries.mongo.TypedMongoStorage;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class LocalDataDomain<K, V> {

    private final DomainContext<K, V> domainContext;
    private final Map<K, V> internalMap = new ConcurrentHashMap<>();
    private final RMapCache<K, V> redisAccess;
    private final TypedMongoStorage<K, V> mongoStorage;

    protected LocalDataDomain(DomainContext<K, V> domainContext) {
        this.domainContext = domainContext;
        this.mongoStorage = domainContext.createMapper();

        RedissonClient redissonClient = domainContext.getRedissonClient();
        if (redissonClient != null) {
            MapOptions<K, V> options = MapOptions.<K, V>defaults()
                    .loader(this.mongoStorage)
                    .writer(this.mongoStorage)
                    .writeMode(MapOptions.WriteMode.WRITE_THROUGH);
            this.redisAccess = redissonClient.getMapCache(domainContext.getNamespace(), options);
        } else {
            this.redisAccess = null;
        }
    }

    public List<V> getAllLoadedValues() {
        return List.copyOf(this.internalMap.values());
    }

    public List<V> getLocalTop(Comparator<V> comparator, int limit) {
        return this.internalMap.values().stream().sorted(comparator).limit(limit).toList();
    }

    public Optional<V> findInLocalCache(Predicate<V> predicate) {
        return this.internalMap.values().stream().filter(predicate).findAny();
    }

    public void loadAllValuesIntoLocalCache() {
        this.mongoStorage.forEach(v -> {
            K key = this.domainContext.getKeyFunction().apply(v);
            if (key != null) {
                this.internalMap.put(key, v);
            }
        });
    }

    public List<K> getAllLoadedKeys() {
        return List.copyOf(this.internalMap.keySet());
    }

    public void forEach(BiConsumer<K, V> consumer) {
        this.internalMap.forEach(consumer);
    }

    public boolean isDataLoaded(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        return this.internalMap.containsKey(key);
    }

    public boolean exists(K key) {
        if (key == null) {
            return false;
        }
        if (this.isDataLoaded(key)) {
            return true;
        }
        if (this.redisAccess != null && this.redisAccess.containsKey(key)) {
            return true;
        }
        return this.mongoStorage.contains(key);
    }

    public CompletableFuture<Boolean> existsAsync(K key) {
        return CompletableFuture.supplyAsync(() -> this.exists(key));
    }

    public V loadAndGetSync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        this.loadDataSync(key);
        return this.getData(key);
    }

    public CompletableFuture<V> loadAndGetAsync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        return CompletableFuture.supplyAsync(() -> this.loadAndGetSync(key));
    }

    public void loadDataSync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }

        if (this.internalMap.containsKey(key)) {
            throw new IllegalStateException("Tried to load data for already loaded key.");
        }

        V value;

        if (this.redisAccess == null) {
            value = this.mongoStorage.load(key);
            if (value == null) {
                value = this.domainContext.getCreator().apply(key);
                this.mongoStorage.persist(value);
            }
        } else {
            value = this.redisAccess.get(key);
            if (value == null) {
                value = this.domainContext.getCreator().apply(key);
                this.redisAccess.fastPut(key, value);
            }
        }
        if (value == null) {
            throw new IllegalStateException("Null insertion in local domain is not permitted.");
        }

        this.internalMap.put(key, value);
    }

    public CompletableFuture<Void> loadDataAsync(K key) {
        return CompletableFuture.runAsync(() -> this.loadDataSync(key));
    }

    public void deleteDataGloballySync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }

        this.internalMap.remove(key);

        if (this.redisAccess != null) {
            this.redisAccess.remove(key);
        }

        this.mongoStorage.delete(key);
    }

    public CompletableFuture<Void> deleteDataGloballyAsync(K key) {
        return CompletableFuture.runAsync(() -> this.deleteDataGloballySync(key));
    }

    public void unloadDataSync(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }

        if (!(this.internalMap.containsKey(key))) {
            throw new IllegalStateException("Tried to unload data for absent key.");
        }

        V value = this.internalMap.remove(key);

        if (this.redisAccess == null) {
            this.mongoStorage.persist(value);
        } else {
            this.redisAccess.fastPut(key, value, 15, TimeUnit.MINUTES);
        }
    }

    public CompletableFuture<Void> unloadDataAsync(K key) {
        return CompletableFuture.runAsync(() -> this.unloadDataSync(key));
    }

    public V getData(K key) {
        if (key == null) {
            throw new IllegalArgumentException("Key cant be null.");
        }
        return Optional.ofNullable(this.internalMap.get(key)).orElseThrow(LocalDataDomain::unloadedState);
    }

    // This might cause concurrency problems...
    public CompletableFuture<Void> applyToDataAsync(K key, Consumer<V> consumer) {
        if (this.isDataLoaded(key)) {
            consumer.accept(this.getData(key));
            return CompletableFuture.completedFuture(null);
        } else {
            return this.loadAndGetAsync(key).thenAccept(consumer).thenRun(() -> this.unloadDataSync(key));
        }
    }

    private static IllegalStateException unloadedState() {
        return new IllegalStateException("Tried to get data for unloaded key.");
    }

    public void saveCacheSync() {
        if (this.redisAccess == null) {
            this.internalMap.values().forEach(this.mongoStorage::persist);
        } else {
            this.internalMap.forEach(this.redisAccess::fastPut);
        }
    }

    public CompletableFuture<Void> saveCacheAsync() {
        return CompletableFuture.runAsync(this::saveCacheSync);
    }
}
