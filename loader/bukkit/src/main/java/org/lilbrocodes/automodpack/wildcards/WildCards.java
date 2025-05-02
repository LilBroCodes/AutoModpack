package org.lilbrocodes.automodpack.wildcards;

import org.lilbrocodes.automodpack.AutoModpack;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

import static org.lilbrocodes.automodpack.AutoModpack.LOGGER;
import static org.lilbrocodes.automodpack.AutoModpack.isDebug;

/**
 * Original code by @Skidamek, but it <br>
 * wasn't working properly, so I rewrote it fully.
 */
public class WildCards {

    public final Map<String, Path> wildcardMatches = new HashMap<>();

    public WildCards(List<String> wildcardsList, Set<Path> directoriesToSearch) {
        if (directoriesToSearch.isEmpty()) return;

        wildcardsList = new ArrayList<>(wildcardsList);

        wildcardsList.sort(Comparator.comparing(s -> s.startsWith("!")));

        if (isDebug()) LOGGER.info("Wildcards to process: {}", wildcardsList);

        List<String> finalWildcardsList = wildcardsList;
        directoriesToSearch.forEach(directory -> {
            try {
                searchDirectory(directory, finalWildcardsList);
            } catch (IOException e) {
                if (isDebug())
                    LOGGER.info("Failed to scan directory \"{}\" with error: \"{}\"", directory.toString(), e.toString());
            }
        });
    }

    private void searchDirectory(Path directory, List<String> wildcards) throws IOException {
        List<Path> possibleFiles = Files.walk(directory)
                .filter(Files::isRegularFile)
                .toList();
        if (isDebug()) LOGGER.info("Scanning directory: {} with {} possible files.", directory, possibleFiles.size());

        for (Path path : possibleFiles) {
            for (String wildcard : wildcards) {
                boolean isNegation = wildcard.startsWith("!");
                String cleanWildcard = isNegation ? wildcard.substring(1) : wildcard;

                if (wildcardMatchesFile(path, directory, cleanWildcard)) {
                    if (isNegation) {
                        wildcardMatches.remove(path.toString());
                        if (isDebug()) LOGGER.info("File {} excluded by negation wildcard: {}", path, wildcard);
                    } else {
                        wildcardMatches.put(path.toString(), path);
                        if (isDebug()) LOGGER.info("File {} matched with wildcard: {}", path, wildcard);
                    }
                }
            }
        }
    }

    private boolean wildcardMatchesFile(Path file, Path basePath, String wildcard) {
        String regex = wildcardToRegex(wildcard);
        String pathToCheck = fullPathToCheckable(file, basePath).replace(File.separator, "/");
        boolean result = Pattern.matches(regex, pathToCheck);
        if (isDebug() && AutoModpack.VERBOSE_DEBUG) LOGGER.info("Testing file {} against wildcard {} (regex: {}): {}", file, wildcard, regex, result);
        return result;
    }

    private String fullPathToCheckable(Path file, Path basePath) {
        return file.toString().replace(basePath.toString(), "");
    }

    private String wildcardToRegex(String wildcard) {
        wildcard = wildcard.replace(File.separator, "/");
        String regex = wildcard
                .replace(".", "\\.")
                .replace("**", ".'|ß'?")
                .replace("*", "[^/]'|ß'")
                .replace("?", ".")
                .replace("'|ß'", "*");
        if (isDebug() && AutoModpack.VERBOSE_DEBUG) LOGGER.info("Converted wildcard {} to regex: {}", wildcard, regex);
        return "^" + regex + "$";
    }
}