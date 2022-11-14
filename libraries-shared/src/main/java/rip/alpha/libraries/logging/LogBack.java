package rip.alpha.libraries.logging;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Date;
import java.util.Deque;

public class LogBack {

    private static final DateFormat timeFormat = DateFormat.getTimeInstance();
    private static final File logFile = new File("coreLogs",
            Date.from(Instant.now()).toString().replace(" ", "-").replace(":", "-"));
    private static final Deque<String> bacLogQueue = new ArrayDeque<>();

    protected static void append(String log) {
        Date now = Date.from(Instant.now());
        String timed = "[" + timeFormat.format(now) + "] " + log;
        bacLogQueue.add(timed);
    }

    protected static void flush() {
        StringBuilder batchBuilder = new StringBuilder();
        while (!bacLogQueue.isEmpty()) {
            batchBuilder.append(bacLogQueue.poll()).append("\n");
        }
        try {
            if (!logFile.exists()) {
                logFile.getParentFile().mkdirs();
                logFile.createNewFile();
            }
            Files.writeString(logFile.toPath(), batchBuilder.toString(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}