package com.example.template.multiloader;

import dev.architectury.injectables.annotations.ExpectPlatform;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Supplier;


public enum Loader {

    // Forge and NeoForge won't be used at the same time due to how stonecutter handles conflicting versions
    FORGE, FABRIC, NEOFORGE;

    public static final Loader CURRENT = getCurrent();

    public boolean isCurrent() {
        return this == CURRENT;
    }

    public void runIfCurrent(Supplier<Runnable> run) {
        if (isCurrent())
            run.get().run();
    }

    @ApiStatus.Internal
    @ExpectPlatform
    public static Loader getCurrent() {
        throw new AssertionError();
    }
}
