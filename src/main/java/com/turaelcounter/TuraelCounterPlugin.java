package com.turaelcounter;

import com.google.inject.Provides;
import javax.inject.Inject;
import java.time.Duration;
import java.time.Instant;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.SkillIconManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.api.VarPlayer;
import net.runelite.api.Varbits;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import java.util.HashSet;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.ChatMessageType;
import net.runelite.client.util.ImageUtil;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.*;
import java.util.ArrayList;
import java.util.HashSet;
import static net.runelite.api.Skill.SLAYER;
import net.runelite.client.eventbus.EventBus;



@Slf4j
@PluginDescriptor(
	name = "Turael Reset Counter",
	description = "Counts slayer streak resets"
)
public class TuraelCounterPlugin extends Plugin
{
	private Integer streakReset = 0;

	private Integer turaelTasksCompleted = 0;

	@Inject
	private Client client;

	@Inject
	private TuraelCounterConfig config;

	@Provides
	TuraelCounterConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(TuraelCounterConfig.class);
	}

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private OverlayManager overlayManager;

	private TuraelStreakInfobox infobox;

	@Inject
	private ItemManager itemManager;

	private int streakVarbit = Varbits.SLAYER_TASK_STREAK;

	private int previousStreakValue = -1;

	private HashSet<Integer> desiredTaskSet = new HashSet<Integer>();

	private boolean isStreakReset = false;

	private Instant infoTimer;

	private boolean isTimeTrackerActive = false;

	@Inject
	private TimerHandler totalTimer;

	@Inject
	private TimerHandler sessionTimer;

	@Inject
	private TimerHandler afkkTimer;

	private Instant timeTracker;
	private Instant pauseSessionTimer;
	private Instant afkTimer;
	private Instant elapsedAfkTimerStart;
	private boolean isPauseSessionActive = false;
	private Duration sessionTimeSpent = Duration.ZERO;
	private Duration totalTimeSpent = Duration.ZERO;
	private Duration totalPausedTime = Duration.ZERO;

	private boolean isInfoboxCreated = false;


	@Override
	protected void startUp()
	{
		loadConfiguredTasks();
		removeUndesiredTasks();
		streakReset = config.streakReset();
		totalTimeSpent = config.totalTimeSpent();
		turaelTasksCompleted = config.turaelTasksCompleted();
		log.info("Total time spent on startup: " + totalTimeSpent);
		log.info("Turael tasks completed on startup: " + turaelTasksCompleted);

//		wider range of tasks for testing purposes, remove once done testing
		desiredTaskSet.add(92);
		desiredTaskSet.add(35);
		desiredTaskSet.add(49);
		desiredTaskSet.add(111);
		desiredTaskSet.add(112);
		desiredTaskSet.add(48);

		if (streakReset == null)
		{
			streakReset = 0;
		}

		if (totalTimeSpent == null)
		{
			totalTimeSpent = Duration.ZERO;
		}

		if (turaelTasksCompleted == null)
		{
			turaelTasksCompleted = 0;
		}

	}

	@Override
	protected void shutDown()
	{
		if (streakReset == null)
		{
			config.streakReset(0);
		}
		else
		{
			config.streakReset(streakReset);
		}

		if (totalTimeSpent == null)
		{
			config.totalTimeSpent(Duration.ZERO);
		}
		else
		{
			config.totalTimeSpent(totalTimeSpent);
		}

		if (turaelTasksCompleted == null)
		{
			config.turaelTasksCompleted(0);
		}
		else
		{
			config.turaelTasksCompleted(turaelTasksCompleted);
		}

		log.info("Total time spent on shutdown: " + totalTimeSpent);
		log.info("Turael tasks completed on shutdown: " + turaelTasksCompleted);

		infoBoxManager.removeIf(TuraelStreakInfobox.class::isInstance);

	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged varbitChanged)
	{
		int varbitId = varbitChanged.getVarbitId();
		int slayerTaskCreature = client.getVarpValue(VarPlayer.SLAYER_TASK_CREATURE);
		String taskName = client.getEnum(EnumID.SLAYER_TASK_CREATURE).getStringValue(slayerTaskCreature);

		//on task reset
		if (varbitId == streakVarbit)
		{
			int currentStreakValue = client.getVarbitValue(Varbits.SLAYER_TASK_STREAK);

			if (previousStreakValue != 0 && currentStreakValue < previousStreakValue)
			{
				streakReset++;
				turaelTasksCompleted++;
				infoTimer = Instant.now();

				if (!isInfoboxCreated)
				{
					infoBoxManager.addInfoBox(new TuraelStreakInfobox(itemManager.getImage(25912), this));
					isInfoboxCreated = true;
				}

				if (!isTimeTrackerActive)
				{
//					start total timer
					log.info("total timer started");
					totalTimer.timerStarted();

//					start session timer
					log.info("session timer started");
					sessionTimer.timerStarted();

					isTimeTrackerActive = true;
				}

			}
			previousStreakValue = currentStreakValue;
		}

		//desired slayer task
		if (desiredTaskSet.contains(slayerTaskCreature) && !isStreakReset && isTimeTrackerActive)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", taskName + " task obtained in " + streakReset + " tasks!", null);
			streakReset = 0;
			isStreakReset = true;
			infoBoxManager.removeIf(TuraelStreakInfobox.class::isInstance);
			isInfoboxCreated = false;
			isTimeTrackerActive = false;

			//end total timer
			totalTimer.timerStopped();

			//end session timer
			sessionTimer.timerStopped();

			//save the config values
//			saveConfigValues();

			if (config.isTaskTrackingDesired())
			{
				printTuraelTasksMessages();
			}

			if (config.isTimeTrackingDesired())
			{
				printTimeSpentMessages();
			}

			//reset the session timer, so it can be used for multiple sessions
			sessionTimer.timerReset();

		}
//		ensures task selection is required
		if (!desiredTaskSet.contains(slayerTaskCreature))
		{
			isStreakReset = false;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged) {
		switch (gameStateChanged.getGameState()){

			case LOGIN_SCREEN:

				//end timer on logout for test purposes (this should be changed to pause timer)
				totalTimer.timerStopped();
				log.info("total timer stopped");

				if (isTimeTrackerActive)
				{
					isPauseSessionActive = true;
				}
				break;

			case LOGGED_IN:

				if (isPauseSessionActive)
				{
					isPauseSessionActive = false;
				}
				break;
		}
	}

	@Subscribe
	public void onStatChanged(StatChanged statChanged)
	{
		if (statChanged.getSkill() != SLAYER)
		{
			return;
		}

		if (isTimeTrackerActive)
		{
			log.info("afk timer started");
			elapsedAfkTimerStart = Instant.now();
		}
	}


	public void printTimeSpentMessages()
	{
//		long sessionHours = sessionTimeSpent.toHours();
//		long sessionMinutes = sessionTimeSpent.minusHours(sessionHours).toMinutes();
//		long sessionSeconds = sessionTimeSpent.minusHours(sessionHours).minusMinutes(sessionMinutes).getSeconds();
//
//		long totalHours = totalTimeSpent.toHours();
//		long totalMinutes = totalTimeSpent.minusHours(totalHours).toMinutes();
//		long totalSeconds = totalTimeSpent.minusHours(totalHours).minusMinutes(totalMinutes).getSeconds();

//		get session and total duration times
//		this works as expected, just need to convert the time
		Duration totalDuration = totalTimer.getDuration();
		Duration sessionDuration = sessionTimer.getDuration();

//		isn't converted but can be done later
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",  "Session time spent Turael skipping: " + sessionDuration, null);
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",  "Total time spent Turael skipping: " + totalDuration, null);

//		String sessionDurationString = (sessionHours > 0) ? sessionHours + " hours, " + sessionMinutes + " minutes, and " + sessionSeconds + " seconds" : sessionMinutes + " minutes and " + sessionSeconds + " seconds";
//		String totalDurationString = (totalHours > 0) ? totalHours + " hours, " + totalMinutes + " minutes, and " + totalSeconds + " seconds" : totalMinutes + " minutes and " + totalSeconds + " seconds";

	}

	public void printTuraelTasksMessages()
	{
		client.addChatMessage(ChatMessageType.GAMEMESSAGE, "",  "Total Turael tasks completed: " + turaelTasksCompleted, null);
	}

	public void saveConfigValues()
	{
		config.totalTimeSpent(totalTimeSpent);
		config.turaelTasksCompleted(turaelTasksCompleted);
	}

	public int getStreakReset()
	{
		return streakReset;
	}


//infobox and afk timer
@Subscribe
public void onGameTick(GameTick tick) {

	if (infoTimer != null && config.statTimeout() != 0)
	{
		Duration timeSinceInfobox = Duration.between(infoTimer, Instant.now());
		Duration statTimeout = Duration.ofMinutes(config.statTimeout());

		if (timeSinceInfobox.compareTo(statTimeout) >= 0)
		{
			infoBoxManager.removeIf(TuraelStreakInfobox.class::isInstance);
			isInfoboxCreated = false;
		}
	}

	if (isTimeTrackerActive && elapsedAfkTimerStart != null)
	{
		Duration elapsedAfkTime = Duration.between(elapsedAfkTimerStart, Instant.now());

		if (elapsedAfkTime.compareTo(Duration.ofMinutes(1)) > 0)
		{
			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "AFK timer reached, ending Turael session", null);
			log.info("1 minutes of slayer afk is reached");
			isTimeTrackerActive = false;

			//stop all timers
			sessionTimer.timerStopped();
			totalTimer.timerStopped();
			elapsedAfkTimerStart = null;

			if (config.isTaskTrackingDesired())
			{
				printTuraelTasksMessages();
			}

			if (config.isTimeTrackingDesired())
			{
				printTimeSpentMessages();
			}

			//reset the session timer, so it can be used for multiple sessions
			sessionTimer.timerReset();
		}
	}
}
	//	value corresponds to slayer task id - needs cleaning up
	public void loadConfiguredTasks()
	{
		if (config.isAbyssalDemonDesired()) {
			desiredTaskSet.add(42);
		}

		if (config.isSmokeDevilDesired()) {
			desiredTaskSet.add(95);
		}

		if (config.isTzKalZukDesired()) {
			desiredTaskSet.add(105);
		}

		if (config.isHellhoundDesired()) {
			desiredTaskSet.add(31);
		}

		if (config.isGargoyleDesired()) {
			desiredTaskSet.add(46);
		}

		if (config.isLizardmenDesired()) {
			desiredTaskSet.add(90);
		}

		if (config.isRevenantDesired()) {
			desiredTaskSet.add(107);
		}

		if (config.isHydraDesired()) {
			desiredTaskSet.add(113);
		}

	}

	public void removeUndesiredTasks()
	{
		if (!config.isAbyssalDemonDesired())
		{
			desiredTaskSet.remove(42);
		}

		if (!config.isSmokeDevilDesired())
		{
			desiredTaskSet.remove((95));
		}

		if (!config.isTzKalZukDesired()) {
			desiredTaskSet.remove(105);
		}

		if (!config.isHellhoundDesired()) {
			desiredTaskSet.remove(31);
		}

		if (!config.isGargoyleDesired()) {
			desiredTaskSet.remove(46);
		}

		if (!config.isLizardmenDesired()) {
			desiredTaskSet.remove(90);
		}

		if (!config.isRevenantDesired()) {
			desiredTaskSet.remove(107);
		}

		if (!config.isHydraDesired()) {
			desiredTaskSet.remove(113);
		}
	}

	@Subscribe
	public void onConfigChanged (ConfigChanged event)
	{
		loadConfiguredTasks();
		removeUndesiredTasks();
	}
}



//		slayer id info
//Creature ID: 31, Name: Hellhounds
//Creature ID: 35, Name: Dagannoth
//Creature ID: 42, Name: Abyssal Demons
//Creature ID: 48, Name: Bloodveld
//Creature ID: 49, Name: Dust Devils

//Creature ID: 90, Name: Lizardmen
//Creature ID: 92, Name: Cave Kraken
//Creature ID: 94, Name: Aviansies
//Creature ID: 95, Name: Smoke Devils
//Creature ID: 96, Name: TzHaar
//Creature ID: 97, Name: TzTok-Jad
//Creature ID: 98, Name: Bosses
//Creature ID: 105, Name: TzKal-Zuk
//Creature ID: 107, Name: Revenants
//Creature ID: 111, Name: Wyrms
//Creature ID: 112, Name: Drakes
//Creature ID: 113, Name: Hydras
//nechryaels ?


