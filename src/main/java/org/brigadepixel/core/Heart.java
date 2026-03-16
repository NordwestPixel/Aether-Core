package org.brigadepixel.core;

public class Heart {
    private int health = 100;
    private final int maxHealth = health;

    public Heart() {

    }

    public void takeDamage(int amount) {
        this.health -= amount;

        if (health <= 0) {

        }
    }

}
