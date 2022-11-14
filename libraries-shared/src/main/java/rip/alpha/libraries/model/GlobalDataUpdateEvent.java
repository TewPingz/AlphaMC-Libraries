package rip.alpha.libraries.model;

import rip.alpha.bridge.BridgeEvent;
import rip.alpha.libraries.json.GsonProvider;

public record GlobalDataUpdateEvent<K, V>(ContextKey<K, V> contextKey,
                                          KeyWrapper<K> keyWrapper) implements BridgeEvent {

    public GlobalDataUpdateEvent(ContextKey<K, V> contextKey, K key) {
        this(contextKey, new KeyWrapper<>(key, contextKey.keyClass));
    }

    public static class KeyWrapper<K> {
        private final String serializedKey;
        private final Class<K> keyClass;

        public KeyWrapper(K key, Class<K> keyClass) {
            this.serializedKey = GsonProvider.toJson(key);
            this.keyClass = keyClass;
        }

        public K key() {
            return GsonProvider.fromJson(this.serializedKey, this.keyClass);
        }
    }

}
