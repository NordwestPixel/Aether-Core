package org.brigadepixel.player;

import org.brigadepixel.core.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Player implements KeyListener {

    private BufferedImage sprite;
    private int x, y;
    private int targetX, targetY;
    private int moveW, moveS, moveA, moveD;
    private int speed = 3;
    private Game game;
    private int money = 1000;

    public Player(int x, int y, Game game) {
        this.x = x; this.y = y;
        this.game = game;
        try {
            sprite = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/player/sprite.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void move() {
        targetY = y + (moveW + moveS) * speed;
        targetX = x + (moveA + moveD) * speed;

        x = targetX;
        y = targetY;
    }

    public void render(Graphics2D g2) {
        //0g2.drawImage(sprite, x, y, null);
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if  (key == KeyEvent.VK_W) {
            moveW = -1;
        } else if (key == KeyEvent.VK_S) {
            moveS = 1;
        } else if  (key == KeyEvent.VK_A) {
            moveA = -1;
        } else if (key == KeyEvent.VK_D) {
            moveD = 1;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getExtendedKeyCode();
        if  (key == KeyEvent.VK_W) {
            moveW = 0;
        } else if (key == KeyEvent.VK_S) {
            moveS = 0;
        } else if  (key == KeyEvent.VK_A) {
            moveA = 0;
        } else if (key == KeyEvent.VK_D) {
            moveD = 0;
        }
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int delta) {
        this.money = money + delta;
    }
}
