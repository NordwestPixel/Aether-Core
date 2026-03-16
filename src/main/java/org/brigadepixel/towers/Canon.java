package org.brigadepixel.towers;

import org.brigadepixel.core.Game;
import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.gui.TowerPrototype;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Canon extends Tower{

    private static BufferedImage img = null;
    private static final int cost = 150;
    private static final int damage = 4;
    private static final int range = 300;
    private static final double attSpeed = 8;
    private static final int maxTargets = 1;

    private static Game game;
    private List<Enemy> targets = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();

    private final List<Projectile> projectiles = new ArrayList<>();
    private double proTimer = 0;
    private static final double proInterval = 1;

    static {
        try {
            img = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/towers/canon.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register
        TowerPrototype prototype = new TowerPrototype(
                "canon",
                "Gatling Canon",
                img,
                cost,
                damage,
                range,
                attSpeed,
                maxTargets,
                Canon::new
        );
        TowerRegistry.register(prototype);
    }

    public Canon(int x, int y) {
        super(x, y, img, cost, damage, range, attSpeed, maxTargets);
    }

    @Override
    public void updateProjectile(double delta) {
        if (game == null) game = getGame();
        if (projectiles.isEmpty()) return;

        proTimer += delta;
        if (proTimer >= proInterval) {
            proTimer -= proInterval;
            for (Projectile p : projectiles) {
                p.move();
            }
        }

        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            enemies = game.getEnemies();
            for (int a = 0; a < enemies.size(); a++) {
                Enemy e = enemies.get(a);
                if (p.getShape().intersects(e.getShape())) {
                    projectiles.remove(p);
                    e.setHealth(damage);
                    if (e.isDead()) {
                        game.getPlayer().setMoney(e.getBounty());
                        targets.remove(e);
                        enemies.remove(e);
                    }
                    break;
                }
            }

            Point pos = p.getPos();
            if (pos.x < 0 || pos.x > 1920 || pos.y < 0 || pos.y > 1080) {
                projectiles.remove(p);
            }
        }
    }

    @Override
    public void attack() {
        targets = getTargets();
        for (Iterator<Enemy> itE = targets.iterator(); itE.hasNext();) {
            Enemy target = itE.next();
            projectiles.add(new Projectile(target, getPos().x, getPos().y));
        }
    }

    @Override
    public void renderProjectiles(Graphics2D g2) {
        if (projectiles.isEmpty()) return;
        for (int i = 0; i < projectiles.size(); i++) {
            Projectile p = projectiles.get(i);
            if (p == null) return;
            g2.setColor(p.getProColor());
            g2.fill(p.getShape());
        }
    }

    @Override
    public void clearProjectiles() {
        if (projectiles.isEmpty()) return;
        projectiles.clear();
    }

    private static class Projectile {

        private double x, y;
        private final double velX, velY;
        private Ellipse2D shape;
        private static final double speed = 25;
        private static final Color proColor = new Color(0x7D628F);

        public Projectile(Enemy target, int startX, int startY) {
            this.x = startX; this.y = startY;
            shape = new Ellipse2D.Double(x - 6, y - 6, 12,12);

            double targetX = target.getShape().getCenterX();
            double targetY = target.getShape().getCenterY();

            double diffX = targetX - startX;
            double diffY = targetY - startY;

            double distance = Math.sqrt(diffX * diffX + diffY * diffY);

            if (distance != 0) {
                this.velX = (diffX / distance) * speed;
                this.velY = (diffY / distance) * speed;
            } else {
                this.velX = 0;
                this.velY = 0;
            }
        }

        public void move() {
            x += velX;
            y += velY;

            shape = new Ellipse2D.Double(x - 6, y - 6, 12, 12);
        }

        public Ellipse2D getShape() {
            return shape;
        }

        public Color getProColor() {
            return proColor;
        }

        public Point getPos() {
            return new Point((int) x, (int) y);
        }
    }
}