package org.brigadepixel.util;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public final class ToolTipUtil {

    private ToolTipUtil() {
    }

    public static final class TooltipLine {
        public final String text;
        public final Color color;
        public final boolean bold;
        public final boolean centered;

        public TooltipLine(String text, Color color, boolean bold, boolean centered) {
            this.text = text;
            this.color = color;
            this.bold = bold;
            this.centered = centered;
        }
    }

    public interface Tooltip {
        void render(Graphics2D g2, int x, int y);
        void render(Graphics2D g2, int x, int y, int width);
        Dimension getSize(Graphics2D g2);
        Dimension getSize(Graphics2D g2, int width);
    }

    public static final class BasicTooltip implements Tooltip {

        private final List<TooltipLine> lines;
        private final Color background;
        private final int padding;
        private final int arc;
        private final Font font;
        private final int lineSpacing;

        public BasicTooltip(List<TooltipLine> lines, Color background, int padding, Font font) {
            this(lines, background, padding, 16, font, 0);
        }

        public BasicTooltip(List<TooltipLine> lines, Color background, int padding, int arc, Font font, int lineSpacing) {
            this.lines = new ArrayList<>(lines);
            this.background = background;
            this.padding = padding;
            this.arc = arc;
            this.font = font;
            this.lineSpacing = lineSpacing;
        }

        @Override
        public void render(Graphics2D g2, int x, int y) {
            render(g2, x, y, -1);
        }

        @Override
        public void render(Graphics2D g2, int x, int y, int width) {
            Font oldFont = g2.getFont();
            g2.setFont(font);

            Dimension size = getSize(g2, width);

            g2.setColor(background);
            g2.fillRoundRect(x, y, size.width, size.height, arc, arc);

            FontMetrics fm = g2.getFontMetrics();
            int drawY = y + padding + fm.getAscent();

            for (TooltipLine line : lines) {
                Font lineFont = line.bold
                        ? font.deriveFont(Font.BOLD)
                        : font.deriveFont(Font.PLAIN);

                g2.setFont(lineFont);
                g2.setColor(line.color);

                FontMetrics lineFm = g2.getFontMetrics();
                int drawX = x + padding;

                if (line.centered) {
                    int textWidth = lineFm.stringWidth(line.text);
                    int boxWidth = size.width - (padding * 2);
                    drawX = x + padding + Math.max(0, (boxWidth - textWidth) / 2);
                }

                g2.drawString(line.text, drawX, drawY);
                drawY += lineFm.getHeight() + lineSpacing;
            }

            g2.setFont(oldFont);
        }

        @Override
        public Dimension getSize(Graphics2D g2) {
            return getSize(g2, -1);
        }

        @Override
        public Dimension getSize(Graphics2D g2, int width) {
            Font oldFont = g2.getFont();
            g2.setFont(font);

            FontMetrics fm = g2.getFontMetrics();
            int contentWidth = 0;
            int contentHeight = 0;

            for (TooltipLine line : lines) {
                contentWidth = Math.max(contentWidth, fm.stringWidth(line.text));
                contentHeight += fm.getHeight() + lineSpacing;
            }

            if (!lines.isEmpty()) {
                contentHeight -= lineSpacing;
            }

            int finalWidth = (width > 0 ? width : contentWidth + padding * 2);
            int finalHeight = contentHeight + padding * 2;

            g2.setFont(oldFont);
            return new Dimension(finalWidth, finalHeight);
        }
    }

    public static TooltipLine line(String text) {
        return new TooltipLine(text, Color.LIGHT_GRAY, false, false);
    }

    public static TooltipLine line(String text, Color color) {
        return new TooltipLine(text, color, false, false);
    }

    public static TooltipLine header(String text) {
        return new TooltipLine(text, Color.WHITE, true, true);
    }

    public static List<TooltipLine> newList() {
        return new ArrayList<>();
    }
}