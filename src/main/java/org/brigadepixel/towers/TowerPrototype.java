package org.brigadepixel.towers;

import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.function.BiFunction;

public final class TowerPrototype {
    private final String id;
    private final String displayName;
    private int cost;
    private final int damage;
    private final int range;
    private final double attSpeed;
    private final int maxTargets;
    private final BufferedImage img;
    private int shopX;
    private final BiFunction<Integer, Integer, Tower> factory;

    public TowerPrototype(String id,
                          String displayName,
                          BufferedImage img,
                          int cost,
                          int damage,
                          int range,
                          double attSpeed,
                          int maxTargets,
                          BiFunction<Integer, Integer, Tower> factory) {
        this.id = id;
        this.displayName = displayName;
        this.cost = cost;
        this.damage = damage;
        this.range = range;
        this.attSpeed = attSpeed;
        this.maxTargets = maxTargets;
        this.img = img;
        this.factory = Objects.requireNonNull(factory);
    }

    // getters
    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
    public int getCost() { return cost; }
    public int getDamage() { return damage; }
    public int getRange() { return range; }
    public double getAttSpeed() { return attSpeed; }
    public int getMaxTargets() { return maxTargets; }
    public BufferedImage getImg() { return img; }
    public int getShopX() { return shopX; }

    public void setCost(double delta) { cost = (int) (cost * delta); }
    public void setShopX(int x) {
        shopX = x;
    }

    public Tower createAt(int x, int y) {
        try {
            Tower t = factory.apply(x, y);
            if (t == null) throw new IllegalStateException("Factory returned null for prototype!");
            return t;
        } catch (RuntimeException e) {
            throw  new IllegalStateException("Failed to create Tower for prototype " + id + " . " + e.getMessage());
        }
    }
}
