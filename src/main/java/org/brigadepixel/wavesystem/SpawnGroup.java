package org.brigadepixel.wavesystem;

import org.brigadepixel.enemies.Enemy;

import java.util.function.Supplier;

public class SpawnGroup {

    private Supplier<Enemy> enemyFactory;
    private int baseCount;
    private double baseInterval;

    public SpawnGroup(Supplier<Enemy> enemyFactory, int baseCount, double baseInterval) {
        this.enemyFactory = enemyFactory;
        this.baseCount = baseCount;
        this.baseInterval = baseInterval;
    }

    public Enemy createEnemy() {
        return enemyFactory.get();
    }

    public int getBaseCount() { return baseCount; }
    public double getBaseInterval() { return baseInterval; }
}
