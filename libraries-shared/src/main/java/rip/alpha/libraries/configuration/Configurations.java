package rip.alpha.libraries.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import rip.alpha.libraries.json.GsonProvider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Configurations {

    private static final Map<Class<? extends Configuration>, File> FILE_REGISTRY = new HashMap<>();

    public static <T extends Configuration> T computeIfAbsent(T defaultInstance) {
        registerConfiguration(defaultInstance);
        return (T) loadConfiguration(defaultInstance.getClass());
    }

    public static <T extends Configuration> void registerConfiguration(T defaultInstance) {
        if (FILE_REGISTRY.containsKey(defaultInstance.getClass())){
            return;
        }

        // Find directory and cache it in our registry
        File file = defaultInstance.getFileLocation();
        FILE_REGISTRY.put(defaultInstance.getClass(), file);

        // Check if the file exists, if not create the file and write it.
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
                writeConfiguration(file, defaultInstance);
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static <T extends Configuration> T loadConfiguration(Class<T> clazz) {
        File file = FILE_REGISTRY.get(clazz);

        if (file == null) {
            throw new IllegalArgumentException("Unregistered config class: " + clazz);
        }

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String jsonData = new String(bytes, StandardCharsets.UTF_8);
            T instance = GsonProvider.fromJson(jsonData, clazz);
            writeConfiguration(file, instance); // so we update the config if there are updated values
            return instance;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T extends Configuration> void writeConfiguration(File file, T instance) throws IOException {
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(GsonProvider.toJsonPretty(instance));
        fileWriter.close();
    }
}