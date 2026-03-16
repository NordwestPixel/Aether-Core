package org.brigadepixel.towers;

import org.brigadepixel.gui.TowerPrototype;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Crystallizer extends Tower{

    private static BufferedImage img = null;
    private static final int cost = 150;
    private static final int damage = 12;
    private static final int range = 320;
    private static final double attSpeed = 2.5;
    private static final int maxTargets = 1;

    static {
        try {
            img = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/towers/crystallizer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //register
        TowerPrototype prototype = new TowerPrototype(
                "crystallizer",
                "Crystallizer Spire",
                img,
                cost,
                damage,
                range,
                attSpeed,
                maxTargets,
                Crystallizer::new
        );
        TowerRegistry.register(prototype);
    }

    public Crystallizer(int x, int y) {
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
