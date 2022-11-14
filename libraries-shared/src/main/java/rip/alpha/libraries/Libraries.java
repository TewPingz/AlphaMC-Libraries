package rip.alpha.libraries;

import com.google.gson.InstanceCreator;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import lombok.Getter;
import marcono1234.gson.recordadapter.RecordTypeAdapterFactory;
import org.bson.codecs.configuration.CodecRegistries;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import rip.alpha.bridge.Bridge;
import rip.alpha.libraries.json.ClassAdapter;
import rip.alpha.libraries.json.ColorAdapter;
import rip.alpha.libraries.json.GsonProvider;
import rip.alpha.libraries.json.map.AllowNullMapTypeAdapterFactory;
import rip.alpha.libraries.redis.GsonCodec;

import java.awt.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.EnumMap;

public class Libraries {
    @Getter
    private static final Libraries instance = new Libraries();

    private boolean enabled = false;
    private RedissonClient redissonClient;
    private MongoClient mongoClient;
    private Bridge bridge;

    public void registerSerializers() {
        GsonProvider.registerTypeAdapterFactory(RecordTypeAdapterFactory.builder()
                .allowMissingComponentValues()
                .allowDuplicateComponentValues()
                .create());
        GsonProvider.registerTypeAdapter(Class.class, new ClassAdapter());
        GsonProvider.registerTypeHierarchyAdapter(Color.class, new ColorAdapter());

        GsonProvider.registerTypeAdapter(EnumMap.class, (InstanceCreator<Object>) type -> {
            Type[] types = (((ParameterizedType) type).getActualTypeArguments());
            return new EnumMap((Class<?>) types[0]);
        });
        GsonProvider.registerTypeAdapterFactory(new AllowNullMapTypeAdapterFactory(GsonProvider.createConstructor(), true));
    }

    public void enable() {
        if (this.enabled) {
            throw new IllegalStateException("Libraries is already enabled");
        }
        Bridge.setGsonSupplier(GsonProvider::getGson);
        this.registerSerializers();
        Config redissonConfig = new Config();
        redissonConfig.setCodec(new GsonCodec());
        LibrariesConfig.getInstance().getRedissonEntries().applyTo(redissonConfig);
        this.redissonClient = Redisson.create(redissonConfig);
        this.bridge = new Bridge("alpha-libraries-bridge", this.redissonClient);
        LibrariesConfig.MongoDBEntries entries = LibrariesConfig.getInstance().getMongoDBEntries();
        MongoClientOptions options = MongoClientOptions.builder()
                .codecRegistry(CodecRegistries.fromRegistries(MongoClient.getDefaultCodecRegistry()))
                .build();
        this.mongoClient = new MongoClient(entries.getAddress(), options);
        this.enabled = true;
    }

    public void disable() {
        if (!this.enabled) {
            throw new IllegalStateException("Libraries is not enabled");
        }

        this.redissonClient.shutdown();
        this.mongoClient.close();
        this.enabled = false;
    }

    public static Bridge getBridge() {
        return instance.bridge;
    }

    public static RedissonClient getRedissonClient() {
        return instance.redissonClient;
    }

    public static MongoClient getMongoClient() {
        return instance.mongoClient;
    }

    public static LibrariesConfig getConfig() {
        return LibrariesConfig.getInstance();
    }
}
