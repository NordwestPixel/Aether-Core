package org.brigadepixel.enemies;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Grunt extends Enemy {

    private static BufferedImage sprite;
    private static BufferedImage spriteDmgOne;
    private static BufferedImage spriteDmgTwo;
    private static int health = 80;
    private static final int damage = 2;
    private static final int speed = 1;
    private static final int bounty = 10;

    static {
        try {
            sprite = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/grunt.png"));
            spriteDmgOne = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/grunt.png"));
            spriteDmgTwo = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/enemies/skirmishers/shards/grunt.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Grunt() {
        super(health,damage,speed,sprite,spriteDmgOne,spriteDmgTwo,bounty);
    }
}