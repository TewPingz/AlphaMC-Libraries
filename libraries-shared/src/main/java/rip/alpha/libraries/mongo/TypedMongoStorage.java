package rip.alpha.libraries.mongo;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.redisson.api.map.MapLoader;
import org.redisson.api.map.MapWriter;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public class TypedMongoStorage<K, V> implements MapLoader<K, V>, MapWriter<K, V> {

    private final MongoCollection<V> mongoCollection;
    private final Function<V, K> keyFunction;

    public TypedMongoStorage(MongoCollection<V> mongoCollection, Class<V> valueClass, Function<V, K> keyFunction) {
        this.keyFunction = keyFunction;
        CodecRegistry codecRegistry = CodecRegistries.fromRegistries(
                CodecRegistries.fromCodecs(new TypedMongoCodec<>(keyFunction, valueClass)),
                MongoClientSettings.getDefaultCodecRegistry());
        this.mongoCollection = mongoCollection.withCodecRegistry(codecRegistry);
    }

    public UpdateResult write(K key, V value) {
        Bson filter = Filters.eq("_id", key.toString());
        ReplaceOptions options = new ReplaceOptions().upsert(true);
        return this.mongoCollection.replaceOne(filter, value, options);
    }

    public UpdateResult persist(V value) {
        return this.write(this.keyFunction.apply(value), value);
    }

    public DeleteResult delete(K key) {
        return this.mongoCollection.deleteOne(Filters.eq("_id", key.toString()));
    }

    @Override
    public V load(K key) {
        Bson filter = Filters.eq(key.toString());
        return this.mongoCollection.find(filter).first();
    }

    @Override
    public Set<K> loadAllKeys() {
        Set<K> keys = new HashSet<>();
        this.forEach(v -> {
            if (v != null) {
                K k = this.keyFunction.apply(v);
                if (k != null) {
                    keys.add(k);
                }
            }
        });
        return keys;
    }

    public void forEach(Consumer<V> consumer) {
        this.mongoCollection.find().forEach(consumer);
    }

    @Override
    public void write(Map<K, V> map) {
        map.forEach(this::write);
    }

    @Override
    public void delete(Collection<K> keys) {
        keys.forEach(this::delete);
    }

    public boolean contains(K key) {
        return this.load(key) != null;
    }

    public <E> List<V> findAll(String property, E value) {
        return this.findAll(Filters.eq(property, value));
    }

    public List<V> findAll(Bson filter) {
        List<V> elements = new ArrayList<>();
        this.mongoCollection.find(filter).into(elements);
        return elements;
    }

    public <E> DeleteResult deleteAll(String property, E value) {
        return this.deleteAll(Filters.eq(property, value));
    }

    public DeleteResult deleteAll(Bson filter) {
        return this.mongoCollection.deleteMany(filter);
    }

}
