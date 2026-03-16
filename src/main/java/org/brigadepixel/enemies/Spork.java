package org.brigadepixel.enemies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Spork extends Enemy {

    private static BufferedImage sprite;
    private static BufferedImage spriteDmgOne;
    private static BufferedImage spriteDmgTwo;
    private static int health = 30;
    private static final int damage = 1;
    private static final int speed = 2;
    private static final int bounty = 5;

    static {
        try {
            sprite = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/spork.png"));
            spriteDmgOne = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/sporkDmgOne.png"));
            spriteDmgTwo = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/sporkDmgTwo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Spork() {
        super(health,damage,speed,sprite,spriteDmgOne,spriteDmgTwo,bounty);
    }
}