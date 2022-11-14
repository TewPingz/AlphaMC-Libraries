package rip.alpha.libraries.model;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.redisson.api.RedissonClient;
import rip.alpha.libraries.mongo.TypedMongoStorage;

import java.util.function.Function;

@Getter
public class DomainContext<K, V> extends ContextKey<K, V> {

    public static <K, V> DomainContext.DomainContextBuilder<K, V> builder() {
        return new DomainContextBuilder<>();
    }

    private final RedissonClient redissonClient;
    private final Function<K, V> creator;
    private final Function<V, K> keyFunction;
    private final MongoDatabase mongoDatabase;

    public DomainContext(
            String namespace,
            Class<K> keyClass,
            Class<V> valueClass,
            RedissonClient redissonClient,
            Function<K, V> creator,
            Function<V, K> keyFunction,
            MongoDatabase mongoDatabase) {
        super(namespace, keyClass, valueClass);
        this.redissonClient = redissonClient;
        this.creator = creator;
        this.keyFunction = keyFunction;
        this.mongoDatabase = mongoDatabase;
    }

    public MongoCollection<V> getBackingCollection() {
        return this.mongoDatabase.getCollection(this.namespace, this.valueClass);
    }

    public TypedMongoStorage<K, V> createMapper() {
        return new TypedMongoStorage<>(this.getBackingCollection(), this.valueClass, this.keyFunction);
    }

    public ContextKey<K, V> asKey() {
        return new ContextKey<>(this.namespace, this.keyClass, this.valueClass);
    }

    public static class DomainContextBuilder<K, V> {

        private String namespace;
        private Class<K> keyClass;
        private Class<V> valueClass;
        private RedissonClient redissonClient;
        private Function<K, V> creator;
        private Function<V, K> keyFunction;
        private MongoDatabase mongoDatabase;

        public DomainContextBuilder<K, V> namespace(String namespace) {
            this.namespace = namespace;
            return this;
        }

        public DomainContextBuilder<K, V> keyClass(Class<K> keyClass) {
            this.keyClass = keyClass;
            return this;
        }

        public DomainContextBuilder<K, V> valueClass(Class<V> valueClass) {
            this.valueClass = valueClass;
            return this;
        }

        public DomainContextBuilder<K, V> redissonClient(RedissonClient redissonClient) {
            this.redissonClient = redissonClient;
            return this;
        }

        public DomainContextBuilder<K, V> creator(Function<K, V> creator) {
            this.creator = creator;
            return this;
        }

        public DomainContextBuilder<K, V> keyFunction(Function<V, K> keyFunction) {
            this.keyFunction = keyFunction;
            return this;
        }

        public DomainContextBuilder<K, V> mongoDatabase(MongoDatabase mongoDatabase) {
            this.mongoDatabase = mongoDatabase;
            return this;
        }

        public DomainContext<K, V> build() {
            return new DomainContext<>(this.namespace, this.keyClass, this.valueClass, this.redissonClient, this.creator, this.keyFunction, this.mongoDatabase);
        }
    }
}
