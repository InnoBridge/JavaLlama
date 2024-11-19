package io.github.innobridge.llama.util;

public class OSInfo {
    public enum OS {
        WINDOWS,
        LINUX,
        MAC,
        UNKNOWN
    }

    public enum Arch {
        X86_64,
        AARCH64,
        UNKNOWN
    }

    private static OS os = null;
    private static Arch arch = null;

    public static OS getOS() {
        if (os == null) {
            String osName = System.getProperty("os.name").toLowerCase();
            if (osName.contains("win")) {
                os = OS.WINDOWS;
            } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
                os = OS.LINUX;
            } else if (osName.contains("mac")) {
                os = OS.MAC;
            } else {
                os = OS.UNKNOWN;
            }
        }
        return os;
    }

    public static Arch getArch() {
        if (arch == null) {
            String archName = System.getProperty("os.arch").toLowerCase();
            if (archName.contains("amd64") || archName.contains("x86_64")) {
                arch = Arch.X86_64;
            } else if (archName.contains("aarch64") || archName.contains("arm64")) {
                arch = Arch.AARCH64;
            } else {
                arch = Arch.UNKNOWN;
            }
        }
        return arch;
    }

    public static String getNativeLibraryPath() {
        OS os = getOS();
        Arch arch = getArch();
        
        String prefix = "/native/";
        String suffix;
        
        switch (os) {
            case WINDOWS:
                suffix = ".dll";
                break;
            case MAC:
                suffix = ".dylib";
                break;
            case LINUX:
            default:
                suffix = ".so";
                break;
        }

        String osArchPath = switch (os) {
            case WINDOWS -> "windows";
            case LINUX -> "linux";
            case MAC -> "osx";
            default -> throw new UnsupportedOperationException("Unsupported operating system");
        } + "-" + switch (arch) {
            case X86_64 -> "x86_64";
            case AARCH64 -> "aarch64";
            default -> throw new UnsupportedOperationException("Unsupported architecture");
        };

        return prefix + osArchPath + "/libllama" + suffix;
    }

    public static boolean isSupported() {
        try {
            getOS();
            getArch();
            getNativeLibraryPath();
            return true;
        } catch (UnsupportedOperationException e) {
            return false;
        }
    }
}
