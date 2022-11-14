package rip.alpha.libraries.logging;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AlphaLoggerFactory {

    @Getter
    @Setter
    private static LogLevel currentLogLevel = LogLevel.BASIC;
    private static final ScheduledExecutorService logFlushExecutor = Executors.newSingleThreadScheduledExecutor();

    static {
        logFlushExecutor.scheduleAtFixedRate(LogBack::flush, 5, 5, TimeUnit.SECONDS);
    }

    public static AlphaLogger createLogger(String name) {
        return new AlphaLogger(name);
    }

    protected static void log(String prefix, Object message, LogLevel logLevel) {
        String log = "[" + prefix + "] " + message;
        if (currentLogLevel.ordinal() >= logLevel.ordinal()) {
            logToConsole(log);
        }
        logToBacklog(log);
    }

    protected static void warn(String prefix, String message, LogLevel logLevel) {
        String log = "[" + prefix + "] " + message;
        if (currentLogLevel.ordinal() >= logLevel.ordinal()) {
            logToConsole("§e" + log);
        }
        logToBacklog(log);
    }

    protected static void severe(String prefix, String message, LogLevel logLevel) {
        String log = "[" + prefix + "] " + message;
        if (currentLogLevel.ordinal() >= logLevel.ordinal()) {
            logToConsole("§c" + log);
        }
        logToBacklog(log);
    }

    private static void logToConsole(String log) {
        System.out.println(log);
    }

    private static void logToBacklog(String log) {
        logFlushExecutor.execute(() -> LogBack.append(log));
    }


}