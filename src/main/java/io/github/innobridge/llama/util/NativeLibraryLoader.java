package io.github.innobridge.llama.util;

import io.github.innobridge.llama.client.OSInfo;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class NativeLibraryLoader {
    private static boolean loaded = false;
    private static final String NATIVE_FOLDER = "/native";

    public static synchronized void loadNativeLibrary() {
        if (loaded) {
            return;
        }

        String libraryPath = NATIVE_FOLDER + "/" + OSInfo.getNativeLibFolderPathForCurrentOS() + "/" + System.mapLibraryName("llama");
        
        try {
            Path tempDir = Files.createTempDirectory("llama-native-");
            Path tempLib = tempDir.resolve(Path.of(libraryPath).getFileName());
            
            // Copy library from resources to temp directory
            try (InputStream in = NativeLibraryLoader.class.getResourceAsStream(libraryPath)) {
                if (in == null) {
                    throw new RuntimeException("Native library not found: " + libraryPath + 
                        ". This platform (" + OSInfo.getOSName() + "/" + OSInfo.getArchName() + ") may not be supported.");
                }
                Files.copy(in, tempLib, StandardCopyOption.REPLACE_EXISTING);
            }

            // Load the library
            System.load(tempLib.toAbsolutePath().toString());
            loaded = true;

            // Clean up the temp file on JVM exit
            tempLib.toFile().deleteOnExit();
            tempDir.toFile().deleteOnExit();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load native library", e);
        }
    }
}
