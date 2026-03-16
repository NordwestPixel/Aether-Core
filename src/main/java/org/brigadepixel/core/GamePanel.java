package org.brigadepixel.core;

import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.towers.Tower;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GamePanel extends JPanel {

    private final Game game;
    private static Dimension dim;
    private static Image back = null;

    public GamePanel(Game game) {
        this.game = game;
        dim = game.getDimensions();
        setPreferredSize(dim);
        try {
            back = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/level/background.png"));
            back = back.getScaledInstance(dim.width,dim.height,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(back, 0, 0, null);

        //game.getPath().render(g2);

        List<Enemy> e = game.getEnemies();
        for (int i = e.size()-1; i >= 0; i--) {
            if (e.get(i).getReachedEnd() || e.get(i) == null) return;
            e.get(i).render(g2);
        }

        List<Tower> towers = game.getTowers();
        for (int i = 0; i < towers.size(); i++) {
            if (towers.get(i) == null) return;
            Tower t = towers.get(i);
            t.render(g2);
            t.renderProjectiles(g2);
        }

        game.getPlayer().render(g2);
        game.getGui().render(g2);

        revalidate();
        repaint();
    }
}
