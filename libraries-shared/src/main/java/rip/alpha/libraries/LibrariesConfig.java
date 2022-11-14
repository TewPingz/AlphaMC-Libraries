package rip.alpha.libraries;

import lombok.Getter;
import lombok.Setter;
import org.redisson.config.Config;
import org.redisson.config.TransportMode;
import rip.alpha.libraries.configuration.Configuration;
import rip.alpha.libraries.configuration.Configurations;

import java.io.File;

@Setter
@Getter
public class LibrariesConfig implements Configuration {

    @Getter
    private static final LibrariesConfig instance = Configurations.computeIfAbsent(new LibrariesConfig());

    private String serverName = "Alpha";
    private String serverIP = "alpha.rip";
    private RedissonEntries redissonEntries = new RedissonEntries();
    private long timerTaskTickMillis = 500;
    private MongoDBEntries mongoDBEntries = new MongoDBEntries();

    private LibrariesConfig() {

    }

    @Override
    public File getFileLocation() {
        return new File("/home/configuration/", "libraries.json");
    }

    @Getter
    @Setter
    public static class RedissonEntries {
        private String address = "redis://127.0.0.1:6379";
        private TransportMode transportMode = TransportMode.NIO;
        private int redissonNettyThreadCount = 32;
        private int redissonThreadCount = 16;

        public void applyTo(Config redissonConfig) {
            redissonConfig.useSingleServer().setAddress(this.address);
            redissonConfig.setTransportMode(this.transportMode);
            redissonConfig.setThreads(this.redissonNettyThreadCount);
            redissonConfig.setNettyThreads(this.redissonNettyThreadCount);
        }
    }

    @Getter
    @Setter
    public static class MongoDBEntries {
        private String address = "localhost:27017";
    }

}
