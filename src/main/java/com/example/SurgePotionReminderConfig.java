package com.example;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("SurgePotionReminder")
public interface SurgePotionReminderConfig extends Config
{
    @ConfigItem(
            keyName = "overlayDuration",
            name = "Overlay Duration",
            description = "Overlay duration in game ticks"
    )
    default int overlayDuration()
    {
        return 15;
    }

    @Alpha
    @ConfigItem(
            keyName = "overlayColor",
            name = "Overlay Color",
            description = "Overlay Background Color"
    )
    default Color overlayColor() {
        return new Color(255, 0, 0, 150);
    }
}