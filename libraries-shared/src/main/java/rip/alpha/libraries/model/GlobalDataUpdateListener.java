package rip.alpha.libraries.model;

import org.redisson.api.listener.MessageListener;

public class GlobalDataUpdateListener<K, V> implements MessageListener<GlobalDataUpdateEvent<K, V>> {

    @Override
    public void onMessage(CharSequence channel, GlobalDataUpdateEvent<K, V> msg) {
        ContextKey<K, V> key = msg.contextKey();
        GlobalDataDomain<K, V> domain = DataManager.getInstance().getGlobalDomainByKey(key);
        if (domain != null && domain.isLocallyCached(msg.keyWrapper().key())) {
            domain.triggerLocalCacheRenew(msg.keyWrapper().key());
        }
    }

}
