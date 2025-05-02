package org.lilbrocodes.automodpack.modpack;

import org.lilbrocodes.automodpack.wildcards.WildCards;

import java.nio.file.Path;

public class LoadedModpack {
    public final Modpack modpack;
    public final Path generatedPath;
    public WildCards wildCards;

    public LoadedModpack(Modpack modpack, Path generatedPath) {
        this.modpack = modpack;
        this.generatedPath = generatedPath;
    }
}
