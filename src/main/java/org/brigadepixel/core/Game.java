package org.brigadepixel.core;

import org.brigadepixel.enemies.Enemy;
import org.brigadepixel.gui.GUI;
import org.brigadepixel.player.Player;
import org.brigadepixel.towers.Tower;
import org.brigadepixel.towers.TowerController;
import org.brigadepixel.wavesystem.WaveController;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class Game implements Runnable {

    private final JFrame window;
    private final JPanel gamePanel;

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;

    private final Heart heart;
    private final Player player;
    private final GUI gui;

    private Thread gameThread;
    private boolean running = false;
    private final int FPS  = 60;

    private final TowerController towerController;
    private final WaveController waveController;

    Game() {
        window = new JFrame("Tech vs Nihilidae's");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setExtendedState(Frame.MAXIMIZED_BOTH);

        towerController = new TowerController(this);
        waveController = new WaveController(this);

        heart = new Heart();
        player = new Player(805,730, this);
        gui = new GUI(this, towerController);

        gamePanel = new GamePanel(this);
        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null);

        gamePanel.addKeyListener(player);

        gamePanel.addMouseListener(gui);
        gamePanel.addMouseMotionListener(gui);
        gamePanel.addMouseWheelListener(gui);

        gamePanel.addMouseListener(towerController);
        gamePanel.addMouseMotionListener(towerController);

        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        window.setVisible(true);
    }

    public synchronized void start() {
        if (running) return;
        running = true;
        gameThread = new Thread(this, "Game Thread");
        gameThread.start();
    }

    public synchronized void stop() {
        if (!running) return;
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double nsPerUpdate = 1_000_000_000.0 / FPS;
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerUpdate;
            lastTime = now;

            while (delta >= 1) {
                update(delta);
                delta--;
            }
        }
    }

    private void update(double delta) {
        player.move();
        waveController.update(delta);
        towerController.update(delta);
        gui.update(delta);
    }

    public Player getPlayer() {
        return player;
    }

    public GUI getGui() {
        return gui;
    }

    public Dimension getDimensions() {
        return new Dimension(WIDTH, HEIGHT);
    }

    public List<Enemy> getEnemies() {
        return waveController.getEnemies();
    }

    public List<Tower> getTowers() {
        return towerController.getTowers();
    }

    public WaveController getWaveController() {
        return waveController;
    }

    public TowerController getTowerController() { return towerController; }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
