package org.brigadepixel.towers;

import org.brigadepixel.core.Game;

import java.util.ArrayList;
import java.util.List;

public class TowerController {

    private final Game game;
    private final List<Tower> towers = new ArrayList<>();

    public TowerController(Game game) {
        this.game = game;
    }

    public void newTower(Tower tower) {
        towers.add(tower);
        Tower.setGame(game);
    }

    public void update(double delta) {
        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).update(delta, game.getEnemies());
        }
    }

    public List<Tower> getTowers() {
        return towers;
    }
}
