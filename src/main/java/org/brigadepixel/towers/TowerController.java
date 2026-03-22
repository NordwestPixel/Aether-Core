package org.brigadepixel.towers;

import org.brigadepixel.core.Game;
import org.brigadepixel.util.OutlineUtil;
import org.brigadepixel.util.ToolTipUtil;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class TowerController implements MouseListener, MouseMotionListener {

    private final Game game;
    private Dimension dim;

    private final List<Tower> towers = new ArrayList<>();
    private Tower selectedTower = null;
    private static final int infoWidth = 300;
    private boolean open = false;
    private boolean isOpen = false;
    private double animTimer = 0;
    private static final int animInterval = 1;
    private int animX = infoWidth;

    private static int mouseX = 0, mouseY = 0;

    public TowerController(Game game) {
        this.game = game;
        this.dim = game.getDimensions();
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

        animTimer += delta;
        if (animTimer >= animInterval) {
            animTimer = 0;
            if (!isOpen && open && animX >= 0) {
                animX -= 10;
                if (animX <= 0) {
                    isOpen = true;
                }
            }
            if (isOpen && !open && animX <= 300) {
                animX += 10;
                if (animX >= 300) {
                    isOpen = false;
                }
            }
        }
    }

    public void render(Graphics2D g2) {
        if (!isOpen && !open) return;
        g2.setColor(new Color(0x7496599F, true));
        int x = dim.width - infoWidth + animX;
        g2.fillRoundRect(x, 0, infoWidth, dim.height, 24, 24);

        if (selectedTower == null) return;
        BufferedImage img = OutlineUtil.createOutline(selectedTower.getImg(), new Color(0xFFFFFFFF, true));
        g2.drawImage(img, (int) selectedTower.getPos().getX() - img.getWidth() / 2, (int) selectedTower.getPos().getY() - img.getHeight() / 2, null);

        ToolTipUtil.Tooltip tooltip = TowerTooltip.build(selectedTower);
        tooltip.render(g2, x, 0, infoWidth);
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
        boolean selTower = false;
        for (Tower tower : towers) {
            if (tower.bounds().contains(e.getPoint())) {
                selectedTower = tower;
                selectedTower.setSelected(true);
                selTower = true;
                isOpen = false;
                open = true;
                break;
            }
        }
        if (!selTower) {
            selectedTower = null;
            isOpen = true;
            open = false;
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
        mouseX = e.getX(); mouseY = e.getY();
    }
}
