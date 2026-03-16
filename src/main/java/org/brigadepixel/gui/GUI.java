package org.brigadepixel.gui;

import org.brigadepixel.core.Game;
import org.brigadepixel.wavesystem.WaveController;
import org.brigadepixel.towers.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GUI implements MouseListener, MouseMotionListener, MouseWheelListener {

    private static Game game;
    private static WaveController waveController;
    private static TowerController towerController;

    private static final Font fontName = new Font("Cooper Black", Font.PLAIN, 30);
    private static final Font fontInfo = new Font("Cooper Black", Font.PLAIN, 20);

    private static BufferedImage shop = null;
    private static BufferedImage openShop = null;
    private static RoundRectangle2D shopBounds;
    private static final Rectangle2D closeShop = new Rectangle2D.Double(1122,710,21,21);
    private static boolean close = false;
    private static boolean open = true;
    private static boolean closed = false;
    private static double shopY = 680;
    private static double openShopY = 1300;
    private static double shopTimer = 0;
    private static final double updateShop = 1;

    private static final RoundRectangle2D waveBtn = new RoundRectangle2D.Double(860, 1020, 210, 45, 90,90);

    private static List<TowerPrototype> towers = null;
    private static int shopTowerX = 0;
    private static String towerName = "";
    private static String towerInfo = "";

    private static boolean buyTower = false;
    private static TowerPrototype prototype = null;
    private static int mouseX, mouseY;

    public GUI(Game game, TowerController towerController) {
        this.game = game;
        this.towerController = towerController;
        shopTowerX = (int) game.getDimensions().getWidth() / 2 - 140;
        waveController = game.getWaveController();
        try {
            shop = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/gui/buy.png"));
            openShop = ImageIO.read(new File("src/main/java/org/brigadepixel/data/img/gui/openBuy.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //load towers in shop
        ShopBootstrap();
        towers = TowerRegistry.getPrototypes();
        for (TowerPrototype t : towers) {
            t.setShopX(shopTowerX);
            shopTowerX += t.getImg().getWidth();
        }

        shopBounds = new RoundRectangle2D.Double(game.getDimensions().getWidth() / 2 - (double) shop.getWidth() / 2, shopY, shop.getWidth(), shop.getHeight(), 48, 48);
    }

    public final void ShopBootstrap() {
        final Class<? extends Tower>[] TOWER_CLASSES = new Class[] {
                Slinger.class,
                Canon.class,
                Ballista.class,
                Crystallizer.class,
                Howitzer.class
        };

        for (Class<? extends Tower> cls : TOWER_CLASSES) {
            try {
                Class.forName(cls.getName(), true, cls.getClassLoader());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void update(double delta) {
        if (!close && !open) return;
        shopTimer += delta;

        if (!closed && close && shopTimer >= updateShop) {
            shopTimer = 0;
            if (shopY <= game.getDimensions().getHeight()) {
                shopY += 20;
            } else {
                if (openShopY >= game.getDimensions().getHeight() - openShop.getHeight()) {
                    openShopY -= 20;
                } else {
                    closed = true;
                    close = false;
                    open = false;
                }
            }
        }

        if (closed && open && shopTimer >= updateShop) {
            shopTimer = 0;
            if (openShopY <= 1300) {
                openShopY += 20;
            } else {
                if (shopY >= 700) {
                    shopY -= 20;
                } else {
                    closed = false;
                    close = false;
                    open = true;
                }
            }
        }

        shopBounds.setRoundRect(game.getDimensions().getWidth() / 2 - (double) shop.getWidth() / 2, shopY, shop.getWidth(), shop.getHeight(), 48, 48);
    }

    public void render(Graphics2D g2) {
        if (!closed || open) {
            g2.drawImage(shop, (int) game.getDimensions().getWidth() / 2 - shop.getWidth() / 2, (int) shopY, null);
        }
        if (closed || close) {
            g2.drawImage(openShop, (int) game.getDimensions().getWidth() / 2 - openShop.getWidth() / 2, (int) openShopY, null);
        }
        if (towers.isEmpty()) return;
        for (TowerPrototype t : towers) {
            g2.drawImage(t.getImg(), t.getShopX(), (int) shopY + 210, null);
        }
        g2.setFont(fontName);
        g2.setColor(Color.WHITE);
        g2.drawString(towerName, (int) shopBounds.getX() + 53, (int) shopY + 97);
        g2.setFont(fontInfo);
        String[] lines = towerInfo.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();
            g2.drawString(line, (int) shopBounds.getX() + 53, (int) shopY + 142 + i * 28);
        }
        if (prototype != null) {
            g2.drawImage(prototype.getImg(), mouseX - prototype.getImg().getWidth() / 2, mouseY - prototype.getImg().getHeight() / 2, null);
            g2.drawOval(mouseX - prototype.getRange() / 2, mouseY - prototype.getRange() / 2, prototype.getRange(), prototype.getRange());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println(e.getPoint());
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        int key = e.getButton();
        if (key == 1 && waveBtn.contains(e.getPoint()) && !close && !closed) {
            close = waveController.startNextWave();
        }
        if (key == 1 && closeShop.contains(e.getPoint())) {
            close = true;
        }
        if (key == 3 && buyTower) {
            prototype = null;
            buyTower = false;
            open = true;
            closed = true;
        }
        if (key == 1 && buyTower) {
            Tower tower = prototype.createAt(mouseX, mouseY);
            towerController.newTower(tower);
            game.getPlayer().setMoney(-prototype.getCost());
            if (prototype.getCost() >= game.getPlayer().getMoney()) {
                prototype = null;
                buyTower = false;
            }
            open = true;
            closed = true;
        }
        Rectangle2D openShopBounds = new Rectangle2D.Double(game.getDimensions().getWidth() / 2 - (double) openShop.getWidth() / 2, (int) openShopY, openShop.getWidth(), openShop.getHeight());
        if (key == 1 && openShopBounds.contains(e.getPoint())) {
            open = true;
        }
        if (key != 1 || buyTower) return;
        for (TowerPrototype t : towers) {
            Rectangle2D bound =  new Rectangle2D.Double(t.getShopX(), (int) shopY + 210, t.getImg().getWidth(), t.getImg().getHeight());
            if (bound.contains(e.getPoint()) && game.getPlayer().getMoney() >= t.getCost()) {
                buyTower = true;
                prototype = t;
                close = true;
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
        mouseX = e.getX(); mouseY = e.getY();
        towerName = ""; towerInfo = "";
        if (!shopBounds.contains(e.getPoint())) return;
        for (TowerPrototype t : towers) {
            Rectangle2D rec = new Rectangle2D.Double(t.getShopX(), (int) shopY + 210, t.getImg().getWidth(), t.getImg().getHeight());
            if (rec.contains(e.getPoint())) {
                towerName = t.getDisplayName();
                towerInfo = "Cost: " + t.getCost() + ", Dmg: " + t.getDamage() + "\n Att Speed: " + t.getAttSpeed() + ", Range: " + t.getRange();
            }
        }
    }
    public void setShopOpen(boolean b) {
        open = b;
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (!shopBounds.contains(e.getPoint())) return;
        int move = e.getUnitsToScroll() * -10;
        if (towers.getLast().getShopX() + towers.getLast().getImg().getWidth() + move <= shopBounds.getX() + shopBounds.getWidth() - (double) towers.getLast().getImg().getWidth() / 2 || towers.getFirst().getShopX() + move >= shopBounds.getX() + (double) towers.getFirst().getImg().getWidth() / 2 + 20) return;
        for (TowerPrototype t : towers) {
            t.setShopX(t.getShopX() + move);
        }
    }
}
