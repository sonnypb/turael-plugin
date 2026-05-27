package com.bawaveinfo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("bawaveinfo")
public interface BaWaveInfoConfig extends Config
{
	@ConfigItem(
			keyName = "enableThresholdAlert",
			name = "Enable Threshold Alerts",
			description = "Enable alert when threshold is exceeded"
	)
	default boolean enableThresholdAlert() {
		return true;
	}
	@ConfigItem(
			keyName = "thresholdValue",
			name = "Damage Threshold",
			description = "Trigger alert when damage exceeds this"
	)
	default int thresholdValue()
	{
		return 40;
	}

}
