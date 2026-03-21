package org.brigadepixel.util;

import java.awt.*;
import java.awt.image.BufferedImage;

public class OutlineUtil {

    public static BufferedImage createOutline(BufferedImage src, Color outlineColor) {
        int w = src.getWidth();
        int h = src.getHeight();

        BufferedImage outline = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                int pixel = src.getRGB(x, y);
                int alpha = (pixel >> 24) & 0xff;

                if (alpha == 0) continue;

                boolean isEdge = false;

                for (int ny = -1; ny <= 1 && !isEdge; ny++) {
                    for (int nx = -1; nx <= 1; nx++) {

                        int px = x + nx;
                        int py = y + ny;

                        if (px < 0 || py < 0 || px >= w || py >= h) {
                            isEdge = true;
                            break;
                        }

                        int neighbor = src.getRGB(px, py);
                        int neighborAlpha = (neighbor >> 24) & 0xff;

                        if (neighborAlpha == 0) {
                            isEdge = true;
                            break;
                        }
                    }
                }

                if (isEdge) {
                    outline.setRGB(x, y, outlineColor.getRGB());
                }
            }
        }

        return outline;
    }
}