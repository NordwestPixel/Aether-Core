package org.brigadepixel.towers;

import java.util.*;

public final class TowerRegistry {
    private static final List<TowerPrototype> prototypes = new ArrayList<>();

    public static void register(TowerPrototype p) {
        prototypes.add(p);
    }

    public static List<TowerPrototype> getPrototypes() {
        return Collections.unmodifiableList(prototypes);
    }
}
