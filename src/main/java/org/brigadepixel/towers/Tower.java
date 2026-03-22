package org.brigadepixel.towers;

import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.util.NumberFormatUtil;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Tower {

    private static Game game;

    private final int id;
    private static int idCounter = 0;
    private final String name;

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
    private int totalDmg = 0;

    private boolean selected = false;

    public Tower (String name, int x, int y, BufferedImage img, int cost, int damage, int range, double attSpeed, int maxTargets) {
        this.name = name;
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
        if (!selected) return;
        g2.setColor(new Color(0xBFFFFFFF, true));
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

    protected void applyDamage(Enemy e, int dmg) {
        e.setHealth(dmg);
        totalDmg += dmg;
    }

    public static void setGame(Game game) {
        Tower.game = game;
    }
    public void setSelected(boolean b) {
        selected = b;
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
    public Rectangle2D bounds() {
        return new Rectangle2D.Double(x - (double) img.getWidth() / 2, y - (double) img.getHeight() / 2, img.getWidth(), img.getHeight());
    }
    public BufferedImage getImg() {
        return img;
    }

    public String getName() { return name; }
    public int getTotalDmg() { return totalDmg; }
    public int getDamage() { return damage; }
    public double getRange() { return range.getWidth(); }
    public double getAttSpeed() { return attSpeed; }
    public int getMaxTargets() { return  maxTargets; }

    @Override
    public String toString() {
        return "ID: " + id + "; NAME: " + name + "; TOTAL-DMG: " + totalDmg + "; DMG: " + damage + "; RANGE: " + range.getWidth() + "; ATT-SPEED: " +
                NumberFormatUtil.round2(attSpeed) + "; MAX-TARGETS: " + maxTargets;
    }

    public abstract void updateProjectile(double delta);

    public abstract void attack();

    public abstract void renderProjectiles(Graphics2D g2);

    public abstract void clearProjectiles();
}
