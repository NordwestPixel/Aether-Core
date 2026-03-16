package org.brigadepixel.enemies;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class EnemyRegistry {
    private static final Map<String, Supplier<Enemy>> REGISTRY = new HashMap<>();

    public EnemyRegistry() {
    }

    static {
        REGISTRY.put("Spork", Spork::new);
        REGISTRY.put("Grunt", Grunt::new);
    }

    public static Supplier<Enemy> getFactory(String type) {
        Supplier<Enemy> factory = REGISTRY.get(type);
        if (factory == null) throw new IllegalArgumentException("Unknown enemy: " + type);
        return factory;
    }
}