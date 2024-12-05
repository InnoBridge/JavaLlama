package io.github.innobridge.llama.client;

/*--------------------------------------------------------------------------
 *  Copyright 2007 Taro L. Saito
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *--------------------------------------------------------------------------*/

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

/**
 * Set the system properties, io.github.innobridge.llama.lib.path,
 * io.github.innobridge.llama.lib.name, appropriately so that the
 * library can find *.dll, *.dylib and *.so files, according to the current OS
 * (win, linux, mac).
 *
 * <p>
 * The library files are automatically extracted from this project's package
 * (JAR).
 *
 * <p>
 * usage: call {@link #initialize()} before using the library.
 *
 * @author leo
 */
@SuppressWarnings("UseOfSystemOutOrSystemErr")
class LlamaLoader {

  private static boolean extracted = false;

  /**
   * Loads the llama and jllama shared libraries
   */
  static synchronized void initialize() throws UnsatisfiedLinkError {
    // only cleanup before the first extract
    if (!extracted) {
      cleanup();
    }
    if ("Mac".equals(OSInfo.getOSName())) {
      String nativeDirName = getNativeResourcePath();
      String tempFolder = getTempDir().getAbsolutePath();
      System.out.println(nativeDirName);
      Path metalFilePath = extractFile(nativeDirName, "ggml-metal.metal", tempFolder, false);
      if (metalFilePath == null) {
        System.err.println("'ggml-metal.metal' not found");
      }
    }
    loadNativeLibrary("ggml");
    loadNativeLibrary("llama");
    loadNativeLibrary("jllama");
    extracted = true;
  }

  /**
   * Deleted old native libraries e.g. on Windows the DLL file is not removed on
   * VM-Exit (bug #80)
   */
  private static void cleanup() {
    try (Stream<Path> dirList = Files.list(getTempDir().toPath())) {
      dirList.filter(LlamaLoader::shouldCleanPath).forEach(LlamaLoader::cleanPath);
    } catch (IOException e) {
      System.err.println("Failed to open directory: " + e.getMessage());
    }
  }

  private static boolean shouldCleanPath(Path path) {
    String fileName = path.getFileName().toString();
    return fileName.startsWith("jllama") || fileName.startsWith("llama");
  }

  private static void cleanPath(Path path) {
    try {
      Files.delete(path);
    } catch (Exception e) {
      System.err.println("Failed to delete old native lib: " + e.getMessage());
    }
  }

  private static void loadNativeLibrary(String name) {
    List<String> triedPaths = new LinkedList<>();
    String nativeLibName = System.mapLibraryName(name);

    // 1. First try loading from explicitly configured path (for CUDA libraries)
    String nativeLibPath = System.getProperty("io.github.innobridge.llama.lib.path");
    System.out.println("Configured library path: " + nativeLibPath);
    
    if (nativeLibPath != null) {
      Path path = Paths.get(nativeLibPath, nativeLibName);
      System.out.println("Attempting to load " + nativeLibName + " from: " + path);
      if (Files.exists(path)) {
        System.out.println("Found library at: " + path);
        if (loadNativeLibrary(path)) {
          System.out.println("Successfully loaded library from: " + path);
          return;
        } else {
          System.out.println("Failed to load library from: " + path);
        }
      } else {
        System.out.println("Library not found at: " + path);
      }
      triedPaths.add(nativeLibPath);
    }

    // 2. Try loading from java.library.path
    String javaLibraryPath = System.getProperty("java.library.path", "");
    System.out.println("Checking java.library.path locations: " + javaLibraryPath);
    for (String ldPath : javaLibraryPath.split(File.pathSeparator)) {
      if (ldPath.isEmpty()) {
        continue;
      }
      Path path = Paths.get(ldPath, nativeLibName);
      System.out.println("Attempting to load from: " + path);
      if (loadNativeLibrary(path)) {
        System.out.println("Successfully loaded library from: " + path);
        return;
      }
      triedPaths.add(ldPath);
    }

    // 3. Try Android-specific loading if on Android
    if (OSInfo.isAndroid()) {
      try {
        System.out.println("Attempting Android-specific loading for: " + name);
        System.loadLibrary(name);
        System.out.println("Successfully loaded library via Android path");
        return;
      } catch (UnsatisfiedLinkError e) {
        System.out.println("Failed to load via Android path: " + e.getMessage());
        triedPaths.add("Directly from .apk/lib");
      }
    }

    // 4. Last resort: try loading from JAR resources
    nativeLibPath = getNativeResourcePath();
    System.out.println("Attempting to load from JAR resources: " + nativeLibPath);
    if (hasNativeLib(nativeLibPath, nativeLibName)) {
      String tempFolder = getTempDir().getAbsolutePath();
      System.out.println("Extracting to temp folder: " + tempFolder);
      if (extractAndLoadLibraryFile(nativeLibPath, nativeLibName, tempFolder)) {
        System.out.println("Successfully loaded library from JAR resources");
        return;
      }
      triedPaths.add(nativeLibPath);
    } else {
      System.out.println("Library not found in JAR resources: " + nativeLibPath + "/" + nativeLibName);
    }

    throw new UnsatisfiedLinkError(
        String.format(
            "No native library found for os.name=%s, os.arch=%s, paths=[%s]",
            OSInfo.getOSName(),
            OSInfo.getArchName(),
            String.join(File.pathSeparator, triedPaths)));
  }

  /**
   * Loads native library using the given path and name of the library
   *
   * @param path path of the native library
   * @return true for successfully loading, otherwise false
   */
  private static boolean loadNativeLibrary(Path path) {
    if (!Files.exists(path)) {
      return false;
    }
    String absolutePath = path.toAbsolutePath().toString();
    try {
      System.load(absolutePath);
      return true;
    } catch (UnsatisfiedLinkError e) {
      System.err.println(e.getMessage());
      System.err.println("Failed to load native library: " + absolutePath + ". osinfo: "
          + OSInfo.getNativeLibFolderPathForCurrentOS());
      return false;
    }
  }

  @Nullable
  private static Path extractFile(String sourceDirectory, String fileName, String targetDirectory, boolean addUuid) {
    String nativeLibraryFilePath = sourceDirectory + "/" + fileName;
    System.out.println("nativeLibraryFilePath '" + nativeLibraryFilePath);
    Path extractedFilePath = Paths.get(targetDirectory, fileName);

    try {
      // Extract a native library file into the target directory
      try (InputStream reader = LlamaLoader.class.getResourceAsStream(nativeLibraryFilePath)) {
        if (reader == null) {
          return null;
        }
        Files.copy(reader, extractedFilePath, StandardCopyOption.REPLACE_EXISTING);
      } finally {
        // Delete the extracted lib file on JVM exit.
        extractedFilePath.toFile().deleteOnExit();
      }

      // Set executable (x) flag to enable Java to load the native library
      extractedFilePath.toFile().setReadable(true);
      extractedFilePath.toFile().setWritable(true, true);
      extractedFilePath.toFile().setExecutable(true);

      // Check whether the contents are properly copied from the resource folder
      try (InputStream nativeIn = LlamaLoader.class.getResourceAsStream(nativeLibraryFilePath);
          InputStream extractedLibIn = Files.newInputStream(extractedFilePath)) {
        if (!contentsEquals(nativeIn, extractedLibIn)) {
          throw new RuntimeException(String.format("Failed to write a native library file at %s", extractedFilePath));
        }
      }

      System.out.println("Extracted '" + fileName + "' to '" + extractedFilePath + "'");
      return extractedFilePath;
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return null;
    }
  }

  /**
   * Extracts and loads the specified library file to the target folder
   *
   * @param libFolderForCurrentOS Library path.
   * @param libraryFileName       Library name.
   * @param targetFolder          Target folder.
   * @return whether the library was successfully loaded
   */
  private static boolean extractAndLoadLibraryFile(String libFolderForCurrentOS, String libraryFileName,
      String targetFolder) {
    Path path = extractFile(libFolderForCurrentOS, libraryFileName, targetFolder, true);
    if (path == null) {
      return false;
    }
    return loadNativeLibrary(path);
  }

  private static boolean contentsEquals(InputStream in1, InputStream in2) throws IOException {
    if (!(in1 instanceof BufferedInputStream)) {
      in1 = new BufferedInputStream(in1);
    }
    if (!(in2 instanceof BufferedInputStream)) {
      in2 = new BufferedInputStream(in2);
    }

    int ch = in1.read();
    while (ch != -1) {
      int ch2 = in2.read();
      if (ch != ch2) {
        return false;
      }
      ch = in1.read();
    }
    int ch2 = in2.read();
    return ch2 == -1;
  }

  private static File getTempDir() {
    return new File(System.getProperty("io.github.innobridge.llama.tmpdir", System.getProperty("java.io.tmpdir")));
  }

  private static String getNativeResourcePath() {
    String packagePath = LlamaLoader.class.getPackage().getName().replace(".", "/");
    return String.format("/%s/%s", packagePath, OSInfo.getNativeLibFolderPathForCurrentOS());
  }

  private static boolean hasNativeLib(String path, String libraryName) {
    return LlamaLoader.class.getResource(path + "/" + libraryName) != null;
  }
}
