package org.brigadepixel.towers;

import org.brigadepixel.gui.TowerPrototype;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Howitzer extends Tower{

    private static BufferedImage img = null;
    private static final int cost = 220;
    private static final int damage = 60;
    private static final int range = 600;
    private static final double attSpeed = 0.3;
    private static final int maxTargets = 9;

    static {
        try {
            img = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/towers/howitzer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register
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

    }

    @Override
    public void attack() {

    }

    @Override
    public void renderProjectiles(Graphics2D g2) {

    }

    @Override
    public void clearProjectiles() {

    }
}
