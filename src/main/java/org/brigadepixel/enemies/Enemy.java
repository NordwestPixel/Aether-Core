package org.brigadepixel.enemies;

import org.brigadepixel.core.Game;
import org.brigadepixel.core.Path;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public abstract class Enemy {

    private int x, y;
    private int health;
    private int maxHealth;
    private int damage;
    private int speed;
    private BufferedImage sprite;
    private BufferedImage spriteDmgOne;
    private BufferedImage spriteDmgTwo;
    private Line2D[] path;
    private int bounty;

    private boolean dead = false;

    private int segmentIndex;
    private boolean reachedEnd;

    public Enemy(int health, int damage, int speed, BufferedImage sprite, BufferedImage spriteDmgOne, BufferedImage spriteDmgTwo, int bounty) {
        this.x = 241 - sprite.getWidth() / 2; this.y = 283 - sprite.getHeight() / 2;
        this.health = health; this.maxHealth = health;
        this.damage = damage;
        this.speed = speed;
        this.sprite = sprite; this.spriteDmgOne = spriteDmgOne; this.spriteDmgTwo = spriteDmgTwo;
        this.path = new Path().paths;
        this.bounty = bounty;
    }

    public void move() {
        if (path == null || path.length == 0 || speed <= 0 || reachedEnd) return;

        // Current precise center (computed from top-left x,y so startX off-path is respected)
        double cx = x + sprite.getWidth() * 0.5;
        double cy = y + sprite.getHeight() * 0.5;

        int seg = Math.max(0, segmentIndex);
        double remaining = speed;

        // If we already finished the path, mark and return
        if (seg >= path.length) {
            reachedEnd = true;
            return;
        }

        // Loop allowing a single tick to move across multiple segment boundaries if speed is large
        while (remaining > 0 && seg < path.length) {
            java.awt.geom.Line2D current = path[seg];
            double ax = current.getX1(), ay = current.getY1();
            double bx = current.getX2(), by = current.getY2();
            double vx = bx - ax, vy = by - ay;
            double vv = vx * vx + vy * vy;

            // 1) Project current center onto the current segment (clamped)
            double t;
            if (vv == 0.0) {
                t = 0.0; // degenerate segment
            } else {
                double wx = cx - ax, wy = cy - ay;
                t = (wx * vx + wy * vy) / vv;
                if (t < 0.0) t = 0.0;
                else if (t > 1.0) t = 1.0;
            }
            double projX = ax + t * vx;
            double projY = ay + t * vy;

            // Distance from center to projection (i.e., distance to "enter" the path segment)
            double toProj = Math.hypot(projX - cx, projY - cy);

            if (toProj > 1e-9) {
                // We are off the path for this segment: move toward the projection point first.
                if (toProj > remaining) {
                    // Move partially toward projection and finish this tick
                    double nx = cx + (projX - cx) * (remaining / toProj);
                    double ny = cy + (projY - cy) * (remaining / toProj);
                    cx = nx; cy = ny;
                    remaining = 0;
                    break;
                } else {
                    // Reach the projection point, consume distance, and continue along the segment
                    cx = projX; cy = projY;
                    remaining -= toProj;
                    // Fall through to proceed toward segment end with remaining distance
                }
            }

            // Now we are on the segment (or exactly on its projection). Move toward segment end (bx,by).
            double toEnd = Math.hypot(bx - cx, by - cy);
            if (toEnd <= 1e-9) {
                // Already at the end of this segment: advance to next
                seg++;
                continue;
            }

            if (toEnd > remaining) {
                // Move along the segment fractionally by 'remaining'
                double nx = cx + (bx - cx) * (remaining / toEnd);
                double ny = cy + (by - cy) * (remaining / toEnd);
                cx = nx; cy = ny;
                remaining = 0;
                break;
            } else {
                // Reach the end of this segment, consume remaining and advance
                cx = bx; cy = by;
                remaining -= toEnd;
                seg++; // next loop will handle next segment (or exit)
            }
        }

        // Update persistent state
        this.segmentIndex = Math.min(seg, path.length); // if seg == path.length => finished
        this.reachedEnd = (this.segmentIndex >= path.length);

        // Update top-left for rendering from center
        this.x = (int) Math.round(cx - sprite.getWidth() * 0.5);
        this.y = (int) Math.round(cy - sprite.getHeight() * 0.5);
    }

    public boolean getReachedEnd() {
        return reachedEnd;
    }

    public void render(Graphics2D g2) {
        g2.setColor(Color.GREEN);
        g2.drawImage(sprite,x,y,null);
    }

    public Rectangle2D getShape() {
        return new Rectangle2D.Double(x,y,sprite.getWidth(),sprite.getHeight());
    }

    public void setHealth(int damage) {
        if (health >= 1) {
            health -= damage;
            if (health <= 0) dead = true;
            if (health <= maxHealth / 1.5) {
                sprite = spriteDmgOne;
            }
            if (health <= maxHealth / 3) {
                sprite = spriteDmgTwo;
            }
        } else {
            dead = true;
        }
    }

    public boolean isDead() {
        return dead;
    }

    public Point getPos() {
        return new Point(x, y);
    }

    public int getBounty() {
        return bounty;
    }
}