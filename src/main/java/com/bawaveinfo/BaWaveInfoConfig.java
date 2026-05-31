package com.bawaveinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bawaveinfo")
public interface BaWaveInfoConfig extends Config
{
	@ConfigItem(
			keyName = "showOverlay",
			name = "Show Overlay",
			description = "Toggle BA wave info overlay"
	)
	default boolean showOverlay() {
		return true;
	}

	@ConfigItem(
			keyName = "trackSpawns",
			name = "Track NPC Spawns",
			description = "Shows remaining NPCs by decreasing counts as they spawn in each wave"
	)
	default boolean trackSpawns()
	{
		return true;
	}
}
