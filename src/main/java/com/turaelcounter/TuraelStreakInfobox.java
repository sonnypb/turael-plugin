package com.turaelcounter;

import java.awt.*;
import java.awt.image.BufferedImage;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import java.awt.Color;

public class TuraelStreakInfobox extends InfoBox {

    private final TuraelCounterPlugin plugin;

    public TuraelStreakInfobox(BufferedImage image, TuraelCounterPlugin plugin)
    {
        super(image, plugin);
        this.plugin = plugin;
        setTooltip("Tasks since");
    }

    @Override
    public String getText()
    {
        Integer value = plugin.getStreakReset();

        if (value == null)
        {
            return "0";
        }
        return String.valueOf(plugin.getStreakReset());
    }

    @Override
    public Color getTextColor() {
        int streakResetCount = plugin.getStreakReset();

        if (streakResetCount <= 6 )
        {
            return Color.GREEN;
        }

        else if (streakResetCount <= 14)
        {
            return Color.ORANGE;
        }

        else
        {
            return Color.RED;
        }
    }
}

