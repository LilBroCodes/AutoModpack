package org.lilbrocodes.automodpack.utils;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PathUtil {
    public static List<String> cleanPaths(List<String> files, String basePath) {
        List<String> out = new ArrayList<>();
        files.forEach(file -> out.add(file.replace(basePath, "")));
        return out;
    }

    @Nullable
    public static String deepestFolder(String path) {
        String normalizedPath = path.replace("\\", "/");
        String[] parts = normalizedPath.split("/");
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isEmpty()) {
                return parts[i];
            }
        }
        return null;
    }
}
