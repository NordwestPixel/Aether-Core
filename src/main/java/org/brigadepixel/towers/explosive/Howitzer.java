package org.brigadepixel.towers.explosive;

import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.gui.TowerPrototype;
import org.brigadepixel.towers.Tower;
import org.brigadepixel.towers.TowerRegistry;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Howitzer extends Tower {

    private static BufferedImage img = null;

    private static final int cost = 260;
    private static final int damage = 60;
    private static final int range = 600;
    private static final double attSpeed = 0.5;
    private static final int maxTargets = 1;

    private static Game game;

    private List<Enemy> targets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();

    static {
        try {
            img = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/towers/howitzer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        TowerPrototype prototype = new TowerPrototype(
                "howitzer",
                "Howitzer",
                img,
                cost,
                damage,
                range,
                attSpeed,
                maxTargets,
                Howitzer::new
        );
        TowerRegistry.register(prototype);
    }

    public Howitzer(int x, int y) {
        super(x, y, img, cost, damage, range, attSpeed, maxTargets);
    }

    @Override
    public void updateProjectile(double delta) {
        if (game == null) game = getGame();
        if (projectiles.isEmpty()) return;

        Iterator<Projectile> it = projectiles.iterator();
        while (it.hasNext()) {
            Projectile p = it.next();
            p.move(delta);

            if (p.hasExploded()) {
                enemies = game.getEnemies();
                p.explode(enemies, damage);
                it.remove();
            }
        }
    }

    @Override
    public void attack() {
        targets = getTargets();
        if (targets == null || targets.isEmpty()) return;
        for (Enemy target : targets) {
            projectiles.add(new Projectile(target, getPos().x, getPos().y));
        }
    }

    @Override
    public void renderProjectiles(Graphics2D g2) {
        if (projectiles.isEmpty()) return;

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if (p == null) continue;

            g2.setColor(p.getShadowColor());
            g2.fill(p.getShadowShape());

            g2.setColor(p.getProColor());
            g2.fill(p.getShellShape());
        }
    }

    @Override
    public void clearProjectiles() {
        projectiles.clear();
    }

    private class Projectile {
        private double x, y;
        private double z;
        private double velX, velY;
        private double velZ;

        private final double targetX;
        private final double targetY;

        private boolean exploded = false;

        private final double flightTime;
        private final double gravity;

        private static final double HORIZONTAL_SPEED = 10; // pixels/sec
        private static final double MIN_FLIGHT_TIME = 50;   // keeps it feeling like artillery
        private static final double MAX_FLIGHT_TIME = 90;

        private static final double SPLASH_RADIUS = 90.0;

        private static final double BASE_SIZE = 36.0;
        private static final Color proColor = new Color(0x7D628F);
        private static final Color shadowColor = new Color(0x2B1E33);

        public Projectile(Enemy target, int startX, int startY) {
            this.x = startX;
            this.y = startY;
            this.z = 0;

            this.targetX = target.getShape().getCenterX();
            this.targetY = target.getShape().getCenterY();

            double dx = targetX - startX;
            double dy = targetY - startY;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Make the shell land at the target point with a strong arc.
            double computedFlightTime = distance / HORIZONTAL_SPEED;
            this.flightTime = clamp(computedFlightTime, MIN_FLIGHT_TIME, MAX_FLIGHT_TIME);

            this.velX = dx / flightTime;
            this.velY = dy / flightTime;

            // Ballistic arc: z(t) = velZ*t - 0.5*g*t^2
            // To land back at z=0 exactly at flightTime:
            // velZ = 0.5 * g * flightTime
            this.gravity = 2.0 * 900.0 / (flightTime * flightTime);
            this.velZ = 0.5 * gravity * flightTime;
        }

        public void move(double delta) {
            if (exploded) return;

            x += velX * delta;
            y += velY * delta;

            z += velZ * delta;
            velZ -= gravity * delta;

            // When the shell reaches the ground plane, detonate.
            if (z <= 0) {
                z = 0;
                exploded = true;
            }
        }

        public void explode(List<Enemy> enemies, int baseDamage) {
            if (enemies == null || enemies.isEmpty()) return;

            for (Enemy e : enemies) {
                double ex = e.getShape().getCenterX();
                double ey = e.getShape().getCenterY();

                double dx = ex - targetX;
                double dy = ey - targetY;
                double dist = Math.sqrt(dx * dx + dy * dy);

                if (dist <= SPLASH_RADIUS) {
                    // Linear falloff: full damage at center, reduced at edge.
                    double falloff = 1.0 - (dist / SPLASH_RADIUS);
                    double scaledDamage = Math.max(0.35, falloff);

                    int finalDamage = Math.max(1, (int) Math.round(baseDamage * scaledDamage));
                    e.setHealth(finalDamage);
                }
            }
        }

        public boolean hasExploded() {
            return exploded;
        }

        public Ellipse2D getShellShape() {
            // Shell is smaller as it flies higher.
            double heightFactor = 1.0 - clamp(z / (flightTime * flightTime * gravity / 8.0), 0.0, 1.0);
            double size = BASE_SIZE * (0.65 + 0.35 * heightFactor);

            return new Ellipse2D.Double(x - size / 2.0, y - size / 2.0, size, size);
        }

        public Ellipse2D getShadowShape() {
            // Ground shadow stays on the map plane.
            double shadowSize = 10.0;
            return new Ellipse2D.Double(x - shadowSize / 2.0, y - shadowSize / 2.0, shadowSize, shadowSize);
        }

        public Color getProColor() {
            return proColor;
        }

        public Color getShadowColor() {
            return shadowColor;
        }

        private double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}