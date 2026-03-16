package org.brigadepixel.towers;

import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Tower {

    private static Game game;

    private final int id;
    private static int idCounter = 0;

    private int x, y;
    private final BufferedImage img;
    private final int cost;
    private int damage;
    private Ellipse2D range;
    private double attSpeed;
    private int maxTargets;

    private double attDelta = 0;
    private boolean attacking;
    private final List<Enemy> targets = new ArrayList<>();

    public Tower (int x, int y, BufferedImage img, int cost, int damage, int range, double attSpeed, int maxTargets) {
        this.x = x; this.y = y;
        this.img = img;
        this.cost = cost;
        this.damage = damage;
        this.range = new Ellipse2D.Double(x - (double)range / 2, y - (double)range / 2, range, range);
        this.attSpeed = 1 / attSpeed;
        this.maxTargets = maxTargets;
        this.id = idCounter++;
    }

    public void render(Graphics2D g2) {
        g2.drawImage(img,x - img.getWidth() / 2,y - img.getHeight() / 2,null);
        g2.setColor(Color.white);
        g2.draw(range);
    }

    public void update(double delta, List<Enemy> enemies) {
        updateProjectile(delta);

        if (targets.isEmpty()) attacking = false;
        for (int i = 0; i < targets.size(); i++) {
            Enemy e = targets.get(i);
            if (!range.intersects(e.getShape())) {
                targets.remove(e);
            }
        }

        for (int i = 0; i < targets.size(); i++) {
            Enemy t = targets.get(i);
            if (t.isDead()) {
                targets.remove(t);
            }
        }

        for (int i = 0; i < enemies.size(); i++) {
            Enemy e = enemies.get(i);
            if (e.isDead()) {
                enemies.remove(e);
                game.getPlayer().setMoney(e.getBounty());
            }
        }

        if (enemies.isEmpty()) { clearProjectiles(); return; }
        for (Enemy e : enemies) {
            if (range.intersects(e.getShape()) && targets.size() < maxTargets && !targets.contains(e)) {
                targets.add(e);
                attacking = true;
            }
        }

        if (!attacking || targets.isEmpty()) return;
        attDelta += delta / 60;
        if (attDelta >= attSpeed) {
            attDelta -= attSpeed;
            attack();
        }
    }

    public static void setGame(Game game) {
        Tower.game = game;
    }

    protected static Game getGame() {
        return game;
    }

    protected Point getPos() {
        return new Point(x,y);
    }

    protected List<Enemy> getTargets() {
        return targets;
    }

    public abstract void updateProjectile(double delta);

    public abstract void attack();

    public abstract void renderProjectiles(Graphics2D g2);

    public abstract void clearProjectiles();
}
