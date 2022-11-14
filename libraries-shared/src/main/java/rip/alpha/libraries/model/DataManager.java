package rip.alpha.libraries.model;

import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DataManager {

    @Getter
    private static final DataManager instance = new DataManager();

    private final Map<ContextKey<?, ?>, LocalDataDomain<?, ?>> localDataDomainMap = new ConcurrentHashMap<>();
    private final Map<ContextKey<?, ?>, GlobalDataDomain<?, ?>> globalDataDomainMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, GlobalDataPocket<?>> globalDataPocketMap = new ConcurrentHashMap<>();

    public <T> GlobalDataPocket<T> getOrCreateDataPocket(Class<T> tClass) {
        return (GlobalDataPocket<T>) this.globalDataPocketMap.computeIfAbsent(tClass, GlobalDataPocket::new);
    }

    public <K, V> LocalDataDomain<K, V> getOrCreateLocalDomain(DomainContext<K, V> key) {
        return (LocalDataDomain<K, V>) this.localDataDomainMap.computeIfAbsent(key, (k) -> new LocalDataDomain<>(key));
    }

    public <K, V> GlobalDataDomain<K, V> getOrCreateGlobalDomain(DomainContext<K, V> key) {
        return (GlobalDataDomain<K, V>) this.globalDataDomainMap.computeIfAbsent(key, (k) -> new GlobalDataDomain<>(key));
    }

    protected <K, V> GlobalDataDomain<K, V> getGlobalDomainByKey(ContextKey<K, V> key) {
        return (GlobalDataDomain<K, V>) this.globalDataDomainMap.get(key);
    }
}