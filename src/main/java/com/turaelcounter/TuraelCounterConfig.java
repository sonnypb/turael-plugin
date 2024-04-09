package com.turaelcounter;

import net.runelite.api.ChatMessageType;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Units;
import java.time.Duration;



@ConfigGroup("turaelcounter")
public interface TuraelCounterConfig extends Config
{
	@ConfigSection(
			name = "Desired Slayer Tasks",
			description = "Select desired tasks",
			position = 0
	)
	String taskSection = "tasks";

	@ConfigItem(
			keyName = "chooseSmokeDevils",
			name = "Smoke Devils",
			description = "Choose if Smoke devils are desired",
			section = taskSection
	)
	default boolean isSmokeDevilDesired()
	{
		return false;
	}
	@ConfigItem(
			keyName = "chooseTzKal-Zuk",
			name = "TzKal-Zuk",
			description = "Choose if TzKal-Zuk is desired",
			section = taskSection
	)
	default boolean isTzKalZukDesired()
	{
		return true;
	}

	@ConfigItem(
			keyName = "chooseAbyssalDemons",
			name = "Abyssal Demons",
			description = "Choose if Abyssal demons are desired",
			section = taskSection
	)
	default boolean isAbyssalDemonDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chooseHellhounds",
			name = "Hellhounds",
			description = "Choose if Hellhounds are desired",
			section = taskSection
	)
	default boolean isHellhoundDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chooseGargoyles",
			name = "Gargoyles",
			description = "Choose if Gargoyles are desired",
			section = taskSection
	)
	default boolean isGargoyleDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chooseLizardmen",
			name = "Lizardmen",
			description = "Choose if Lizardmen are desired",
			section = taskSection
	)
	default boolean isLizardmenDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chooseRevenants",
			name = "Revenants",
			description = "Choose if Revenants are desired",
			section = taskSection
	)
	default boolean isRevenantDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "chooseHydras",
			name = "Hydras",
			description = "Choose if Hydras are desired",
			section = taskSection
	)
	default boolean isHydraDesired()
	{
		return false;
	}

	@ConfigItem(
			keyName = "streakReset",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	Integer streakReset();

	@ConfigItem(
			keyName = "streakReset",
			name = "",
			description = "",
			section = taskSection
	)
	void streakReset(Integer streakReset);

	@ConfigItem(
			keyName = "totalTimeSpent",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	Duration totalTimeSpent();

	@ConfigItem(
			keyName = "totalTimeSpent",
			name = "",
			description = "",
			section = taskSection
	)
	void totalTimeSpent(Duration totalTimeSpent);

	@ConfigItem(
			keyName = "turaelTasksCompleted",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	Integer turaelTasksCompleted();

	@ConfigItem(
			keyName = "turaelTasksCompleted",
			name = "",
			description = "",
			section = taskSection
	)
	void turaelTasksCompleted(Integer turaelTasksCompleted);

	@ConfigItem(
			position = 4,
			keyName = "statTimeout",
			name = "InfoBox Expiry",
			description = "Set the time until the InfoBox expires"
	)
	@Units(Units.MINUTES)
	default int statTimeout()
	{
		return 5;
	}

	@ConfigItem(
			position = 5,
			keyName = "turaelTimeTracking",
			name = "Turael Skipping Time",
			description = "Show time spent turael skipping"
	)
	default boolean isTimeTrackingDesired()
	{
		return true;
	}

	@ConfigItem(
			position = 6,
			keyName = "turaelTasksTracking",
			name = "Total Turael Tasks",
			description = "Show tasks completed while turael skipping"
	)
	default boolean isTaskTrackingDesired()
	{
		return true;
	}

	@ConfigItem(
			keyName = "turaelDuration",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	Duration turaelDuration();

	@ConfigItem(
			keyName = "turaelDuration",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	void turaelDuration(Duration turaelDuration);

	@ConfigItem(
			keyName = "turaelTimerState",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	TimerHandler.TimerState turaelTimerState();

	@ConfigItem(
			keyName = "turaelTimerState",
			name = "",
			description = "",
			section = taskSection,
			hidden = true
	)
	void turaelTimerState(TimerHandler.TimerState turaelTimerState);




}
