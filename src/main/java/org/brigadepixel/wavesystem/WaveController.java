package org.brigadepixel.wavesystem;

import com.google.gson.Gson;
import org.brigadepixel.config.GroupConfig;
import org.brigadepixel.config.LevelConfig;
import org.brigadepixel.config.WaveConfig;
import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.enemies.EnemyRegistry;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;

public class WaveController {

    private final Game game;

    private final List<Enemy> enemies = new ArrayList<>();

    private int wave = 0;
    private boolean waveOver = true;
    private LevelConfig levelConfig;

    private Queue<SpawnGroup> currentWaveQueue;

    private SpawnGroup currentGroup;
    private int enemiesSpawnedInGroup = 0;
    private int targetEnemyCount = 0;
    private double currentSpawnInterval = 0;
    private double spawnTimer = 0;

    private String sortMec = "";

    public WaveController(Game game) {
        this.game = game;
        loadJsonConfig("src/main/java/org/brigadepixel/wavesystem/waves.json");
    }

    public void update(double delta) {
        if (enemies.isEmpty() && waveOver && wave > 0) {
            game.getGui().setShopOpen(true);
            return;
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).move();
            if (enemies.get(i).getReachedEnd()) {
                enemies.remove(i);
            }
        }

        if (currentGroup == null) return;
        spawnTimer += delta;

        if (spawnTimer >= currentSpawnInterval) {
            spawnTimer = 0;

            Enemy newEnemy = currentGroup.createEnemy();
            enemies.add(newEnemy);
            sort();

            enemiesSpawnedInGroup++;

            if (enemiesSpawnedInGroup >= targetEnemyCount) {
                loadNextGroup();
            }
        }
    }

    public void sort() {
        switch (sortMec) {
            case "health": enemies.sort((a, b) ->
                    Double.compare(b.getMaxHealth(), a.getMaxHealth())
            );
            default: enemies.sort((a, b) ->
                    Double.compare(b.getPathPos(), a.getPathPos())
            );
        }
    }

    public boolean startNextWave() {
        if (!enemies.isEmpty()) return false;

        this.wave++;
        waveOver = false;

        if (levelConfig == null) return false;

        if (this.wave > levelConfig.waves.size()) {
            return false;
        }

        WaveConfig currentWaveConfig = levelConfig.waves.get(this.wave - 1);

        this.currentWaveQueue = new LinkedList<>();
        for (GroupConfig gc : currentWaveConfig.groups) {
            Supplier<Enemy> factory = EnemyRegistry.getFactory(gc.enemyType);
            this.currentWaveQueue.add(new SpawnGroup(factory, gc.count, gc.interval));
        }

        loadNextGroup();

        return true;
    }

    private void loadNextGroup() {
        if (currentWaveQueue == null || currentWaveQueue.isEmpty()) {
            waveOver = true;
            currentGroup = null;
            return;
        }

        currentGroup = currentWaveQueue.poll();
        enemiesSpawnedInGroup = 0;

        assert currentGroup != null;
        targetEnemyCount = currentGroup.getBaseCount();
        currentSpawnInterval = currentGroup.getBaseInterval();
    }

    public void loadJsonConfig(String filepath) {
        try (FileReader reader = new FileReader(filepath)) {
            Gson gson = new Gson();
            this.levelConfig = gson.fromJson(reader, LevelConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }
}
