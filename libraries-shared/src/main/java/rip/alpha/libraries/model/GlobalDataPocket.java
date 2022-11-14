package rip.alpha.libraries.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import rip.alpha.libraries.Libraries;
import rip.alpha.libraries.mongo.TypedMongoStorage;

import java.util.List;
import java.util.UUID;

public class GlobalDataPocket<T> {

    private final TypedMongoStorage<UUID, T> internalMongoStorage;

    public GlobalDataPocket(Class<T> tClass) {
        MongoDatabase dataBase = Libraries.getMongoClient().getDatabase("DataPockets");
        MongoCollection<T> collection = dataBase.getCollection(tClass.getSimpleName(), tClass);
        this.internalMongoStorage = new TypedMongoStorage<>(collection, tClass, value -> {
            throw new UnsupportedOperationException();
        });
    }

    public void add(T element) {
        this.internalMongoStorage.write(UUID.randomUUID(), element);
    }

    public <E> List<T> loadAll(String fieldName, E value) {
        return this.internalMongoStorage.findAll(fieldName, value);
    }

}
