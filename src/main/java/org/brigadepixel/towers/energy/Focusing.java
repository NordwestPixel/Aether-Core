package org.brigadepixel.towers.energy;

import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.towers.TowerPrototype;
import org.brigadepixel.towers.Tower;
import org.brigadepixel.towers.TowerRegistry;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Focusing extends Tower {

    private static final String id = "focusing";
    private static final String displayName = "Prismatic Focusing Tower";
    private static BufferedImage img = null;
    private static final int cost = 100;
    private static final int damage = 1;
    private static final int range = 400;
    private static final double attSpeed = 100;
    private static final int maxTargets = 3;

    private static Game game;
    private List<Enemy> targets = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    static {
        try {
            img = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/towers/focusing.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register
        TowerPrototype prototype = new TowerPrototype(
                id,
                displayName,
                img,
                cost,
                damage,
                range,
                attSpeed,
                maxTargets,
                Focusing::new
        );
        TowerRegistry.register(prototype);
    }

    public Focusing(int x, int y) {
        super(displayName, x, y, img, cost, damage, range, attSpeed, maxTargets);
    }

    @Override
    public void updateProjectile(double delta) {
        if (game == null) game = getGame();
        if (projectiles.isEmpty()) return;

        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            Enemy target = p.getTarget();

            if (target == null || !targets.contains(target)) {
                it.remove();
                continue;
            }

            p.move();
            p.update(delta);
        }
    }

    @Override
    public void attack() {
        targets = getTargets();
        if (targets == null || targets.isEmpty()) return;

        for (Enemy target : targets) {
            if (target == null) continue;

            if (projectiles.size() >= maxTargets) break;
            if (hasProjectileForTarget(target)) continue;

            projectiles.add(new Projectile(target, getPos().getX(), getPos().getY() - 30));
        }
    }

    private boolean hasProjectileForTarget(Enemy target) {
        for (Projectile p : projectiles) {
            if (p != null && p.getTarget() == target) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void renderProjectiles(Graphics2D g2) {
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            g2.setColor(new Color(229, 190, 255, 45));
            g2.setStroke(new BasicStroke(p.getThickness() + 3));
            g2.drawLine((int) p.getP1().getX(), (int) p.getP1().getY(), (int) p.getP2().getX(), (int) p.getP2().getY());

            g2.setColor(new Color(0xBD84E7));
            g2.setStroke(new BasicStroke(p.getThickness()));
            g2.drawLine((int) p.getP1().getX(), (int) p.getP1().getY(), (int) p.getP2().getX(), (int) p.getP2().getY());
        }

        g2.setStroke(new BasicStroke(1f));
    }

    @Override
    public void clearProjectiles() {
        projectiles.clear();
    }

    private class Projectile {

        private Point2D p1, p2;
        private Enemy target;

        private double current = damage;
        private static final int MAX_DAMAGE = 10;
        private static final double RAMP_PER_TICK = 0.5;

        private double damageTimer = 0;
        private static final double DAMAGE_INTERVAL = 6;

        private float thickness = 1f;

        public Projectile(Enemy target, double startX, double startY) {
            this.target = target;
            p1 = new Point2D.Double(startX, startY);
            p2 = new Point2D.Double(target.getShape().getCenterX(), target.getShape().getCenterY());
        }

        public void update(double delta) {
            damageTimer += delta;
            if (damageTimer >= DAMAGE_INTERVAL) {
                damageTimer = 0;
                current += RAMP_PER_TICK;
                current = Double.min(current, MAX_DAMAGE);

                thickness = (float) Math.sqrt(current);
                applyDamage(target, (int) current);
            }
        }

        public void move() {
            p2.setLocation(target.getShape().getCenterX(), target.getShape().getCenterY());
        }

        public Point2D getP1() {
            return p1;
        }

        public Point2D getP2() {
            return p2;
        }

        public Enemy getTarget() {
            return target;
        }

        public float getThickness() {
            return thickness;
        }
    }
}
