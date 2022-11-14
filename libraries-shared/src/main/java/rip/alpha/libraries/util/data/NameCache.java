package rip.alpha.libraries.util.data;

import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.model.DataManager;
import rip.alpha.libraries.model.DomainContext;
import rip.alpha.libraries.model.GlobalDataDomain;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class NameCache {

    @Getter
    private static final NameCache instance = new NameCache();

    private final GlobalDataDomain<String, NameIDEntry> nameCache;
    private final GlobalDataDomain<UUID, NameIDEntry> uuidCache;

    private NameCache() {
        MongoDatabase database = Libraries.getMongoClient().getDatabase("global-caches");
        DomainContext<String, NameIDEntry> nameContext = DomainContext.<String, NameIDEntry>builder()
                .keyFunction(NameIDEntry::getName)
                .keyClass(String.class)
                .namespace("playerName-to-id")
                .valueClass(NameIDEntry.class)
                .creator(NameCache::fetchForName)
                .mongoDatabase(database)
                .redissonClient(Libraries.getRedissonClient())
                .build();
        DomainContext<UUID, NameIDEntry> uuidContext = DomainContext.<UUID, NameIDEntry>builder()
                .keyFunction(NameIDEntry::getUuid)
                .keyClass(UUID.class)
                .namespace("id-to-playerName")
                .valueClass(NameIDEntry.class)
                .creator(NameCache::fetchForID)
                .mongoDatabase(database)
                .redissonClient(Libraries.getRedissonClient())
                .build();
        this.nameCache = DataManager.getInstance().getOrCreateGlobalDomain(nameContext);
        this.uuidCache = DataManager.getInstance().getOrCreateGlobalDomain(uuidContext);
    }

    public String getName(UUID playerID) {
        if (!this.uuidCache.isLocallyCached(playerID)) {
            //this.uuidCache.enableLocalCacheFor(playerID);
            return null; //dont cache because cache fetches...
        }
        return this.uuidCache.getCachedValue(playerID).getName();
    }

    public UUID getID(String playerName) {
        playerName = playerName.toLowerCase();
        if (!this.nameCache.isLocallyCached(playerName)) {
            //this.nameCache.enableLocalCacheFor(playerName);
            return null; //dont cache because cache fetches...
        }
        return this.nameCache.getCachedValue(playerName).getUuid();
    }

    public CompletableFuture<String> getNameAsync(UUID playerID) {
        return CompletableFuture.supplyAsync(() -> {
            if (!this.uuidCache.isLocallyCached(playerID)) {
                this.uuidCache.enableLocalCacheFor(playerID);
            }
            return this.getName(playerID);
        });
    }

    public CompletableFuture<UUID> getIDAsync(String playerName) {
        String finalPlayerName = playerName.toLowerCase();
        return CompletableFuture.supplyAsync(() -> {
            if (!this.nameCache.isLocallyCached(finalPlayerName)) {
                this.nameCache.enableLocalCacheFor(finalPlayerName);
            }
            return this.getID(finalPlayerName);
        });
    }

    public void updateEntry(UUID playerID, String playerName) {
        String lowerCaseName = playerName.toLowerCase();
        if (!this.nameCache.isLocallyCached(lowerCaseName)) {
            this.nameCache.enableLocalCacheFor(lowerCaseName);
        }
        this.nameCache.applyToData(lowerCaseName, data -> data.setUuid(playerID));
        if (!this.uuidCache.isLocallyCached(playerID)) {
            this.uuidCache.enableLocalCacheFor(playerID);
        }
        this.uuidCache.applyToData(playerID, data -> data.setName(playerName));
    }

    private static NameIDEntry fetchForName(String name) {
        return new NameIDEntry(name.toLowerCase(), MojangRequest.fetchID(name));
    }

    private static NameIDEntry fetchForID(UUID playerID) {
        return new NameIDEntry(MojangRequest.fetchName(playerID), playerID);
    }

}
