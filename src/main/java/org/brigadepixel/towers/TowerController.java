package org.brigadepixel.towers;

import org.brigadepixel.core.Game;
import org.brigadepixel.util.OutlineUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TowerController implements MouseListener, MouseMotionListener {

    private final Game game;
    private final List<Tower> towers = new ArrayList<>();

    private Tower selectedTower = null;

    public TowerController(Game game) {
        this.game = game;
    }

    public void newTower(Tower tower) {
        towers.add(tower);
        Tower.setGame(game);

        towers.sort((a, b) ->
                Double.compare(b.bounds().getY(), a.bounds().getY())
        );
    }

    public void update(double delta) {
        for (int i = 0; i < towers.size(); i++) {
            towers.get(i).update(delta, game.getEnemies());
        }
    }

    public void render(Graphics2D g2) {
        if (selectedTower != null) {
            BufferedImage img = OutlineUtil.createOutline(selectedTower.getImg(), new Color(0xFFFFFFFF, true));
            g2.setColor(Color.white);
            g2.drawImage(img, (int) selectedTower.getPos().getX() - img.getWidth() / 2, (int) selectedTower.getPos().getY() - img.getHeight() / 2, null);
        }
    }

    public List<Tower> getTowers() {
        return towers;
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() != MouseEvent.BUTTON1 && e.getButton() != MouseEvent.BUTTON3) return;
        if (selectedTower != null) selectedTower.setSelected(false);
        selectedTower = null;
        for (Tower tower : towers) {
            if (tower.bounds().contains(e.getPoint())) {
                selectedTower = tower;
                selectedTower.setSelected(true);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}
