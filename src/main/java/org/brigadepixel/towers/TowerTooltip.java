package org.brigadepixel.towers;

import org.brigadepixel.util.NumberFormatUtil;
import org.brigadepixel.util.ToolTipUtil;

import java.awt.*;
import java.util.List;

public class TowerTooltip  {

    public static ToolTipUtil.Tooltip build(Tower tower) {
        List<ToolTipUtil.TooltipLine> lines = ToolTipUtil.newList();

        lines.add(ToolTipUtil.header(tower.getName()));
        lines.add(ToolTipUtil.line("Total Damage: " + tower.getTotalDmg()));
        lines.add(ToolTipUtil.line("Damage: " + tower.getDamage()));
        lines.add(ToolTipUtil.line("Range: " + tower.getRange()));
        lines.add(ToolTipUtil.line("Attack Speed: " + NumberFormatUtil.round2(tower.getAttSpeed())));
        lines.add(ToolTipUtil.line("Max Targets: " + tower.getMaxTargets()));

        Font towerFont = new Font("Arial", Font.PLAIN, 18);

        return new ToolTipUtil.BasicTooltip(
                lines,
                new Color(29, 14, 48, 150),
                8,
                16,
                towerFont,
                8
        );
    }
}
