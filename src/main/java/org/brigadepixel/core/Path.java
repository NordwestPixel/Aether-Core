package org.brigadepixel.core;

import java.awt.*;
import java.awt.geom.Line2D;

public class Path {

    public Line2D[] paths = {
            new Line2D.Double(241, 273, 252,733),
            new Line2D.Double(252, 733, 392,771),
            new Line2D.Double(392, 771, 442,932),
            new Line2D.Double(442, 932, 517,951),
            new Line2D.Double(517, 951, 585,937),
            new Line2D.Double(585, 937, 644,813),
            new Line2D.Double(644, 813, 847,785),
            new Line2D.Double(847, 785, 880,640),
            new Line2D.Double(880, 640, 865,565),
            new Line2D.Double(865, 565, 656,275),
            new Line2D.Double(656, 275, 1116,102),
            new Line2D.Double(1116, 102, 1260,124),
            new Line2D.Double(1260, 124, 1106,404),
            new Line2D.Double(1106, 404, 1542,611),
            new Line2D.Double(1542, 611, 1697,431),
            new Line2D.Double(1697, 431, 1448,343),
            new Line2D.Double(1448, 343, 1152,743),
            new Line2D.Double(1152, 743, 1597,953),
    };

    public void render(Graphics2D g2) {
        for (Line2D line : paths) {
            g2.setColor(Color.white);
            g2.draw(line);
        }
    }
}
