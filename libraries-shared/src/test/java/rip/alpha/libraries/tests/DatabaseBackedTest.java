package rip.alpha.libraries.tests;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;
import rip.alpha.libraries.redis.GsonCodec;

public class DatabaseBackedTest {

    @Container
    protected static GenericContainer<?> redis;
    @Container
    protected static MongoDBContainer mongo;
    protected static MongoDatabase mongoDatabase;
    protected static RedissonClient redissonClient;

    @BeforeAll
    public static void setUp() {
        System.out.println("Setting up backing Databases");
        mongo = new MongoDBContainer(DockerImageName.parse("mongo")).withExposedPorts(27017);
        mongo.start();
        redis = new GenericContainer<>(DockerImageName.parse("redis")).withExposedPorts(6379);
        redis.start();

        Config redissonConfig = new Config();
        redissonConfig.setTransportMode(TransportMode.NIO);
        redissonConfig.setCodec(new GsonCodec());
        redissonConfig.useSingleServer().setAddress("redis://%s:%d".formatted(redis.getHost(), redis.getFirstMappedPort()));
        redissonClient = Redisson.create(redissonConfig);
        MongoClient mongoClient = new MongoClient(mongo.getHost(), mongo.getFirstMappedPort());
        mongoDatabase = mongoClient.getDatabase("test-data");
        System.out.println("Finished setting up backing Databases");
    }

    @AfterAll
    public static void tearDown() {
        mongo.stop();
        redis.stop();
    }

}