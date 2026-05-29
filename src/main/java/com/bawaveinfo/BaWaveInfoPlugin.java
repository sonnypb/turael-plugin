package com.bawaveinfo;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.api.events.NpcSpawned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
	name = "BA Wave Info"
)
public class BaWaveInfoPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BaWaveInfoConfig config;

	@Inject
	private OverlayManager overlayManager;
	@Inject
	private BaWaveInfoOverlay overlay;
	private int currentWave = -1;
	private BaRole currentRole;
	private int runnersRemaining;
	private int healersRemaining;
	private int fightersRemaining;
	private int rangersRemaining;
	private boolean inBaSession;
	private static final Pattern BA_WAVE_PATTERN = Pattern.compile("Wave:\\s*(\\d+)");
	@Override
	protected void startUp() throws Exception
	{
		log.debug("BA Wave Info started!");
		if (config.showOverlay())
		{
			overlayManager.add(overlay);
		}
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("BA Wave Info stopped!");
		overlayManager.remove(overlay);
		currentWave = -1;
	}


	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!event.getGroup().equals("bawaveinfo"))
		{
			return;
		}

		if (event.getKey().equals("showOverlay")) {
			if (config.showOverlay())
			{
				overlayManager.add(overlay);
			} else {
				overlayManager.remove(overlay);
			}
		}
	}

	public BaRole getCurrentRole()
	{
		return currentRole;
	}

	public int getCurrentWave()
	{
		return currentWave;
	}

	public WaveInfo getWaveInfo()
	{
		if (currentWave <= 0)
		{
			return null;
		}
		return WaveData.get(getCurrentWave());
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = event.getMessage();

		if (message == null)
		{
			return;
		}

		// check which wave you're on to load overlay
		if (message.contains("Wave"))
		{
			extractWave(message);
		}

		// end of BA run
		handleBaCompletion(message);

	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		Widget root = client.getWidget(256, 0);

		if (root == null)
		{
			return;
		}

		updateRole();
	}

	// Maybe make this a bit more nuanced later, it will do for now
	// Logic for showing number of remaining spawns
	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		if (!config.trackSpawns() || currentWave <= 0)
		{
			return;
		}

		NPC npc = event.getNpc();
		int npcId = npc.getId();

		if (NpcData.HEALER_NPCS.contains(npcId))
		{
			healersRemaining--;
		}
		else if (NpcData.RUNNER_NPCS.contains(npcId))
		{
			runnersRemaining--;
		}
		else if (NpcData.FIGHTER_NPCS.contains(npcId))
		{
			fightersRemaining--;
		}
		else if (NpcData.RANGER_NPCS.contains(npcId))
		{
			rangersRemaining--;
		}
	}

	private void extractWave(String message)
	{

		if (!message.startsWith("---- Wave:"))
		{
			return;
		}

		Matcher waveMatcher = BA_WAVE_PATTERN.matcher(message);

		if (waveMatcher.find())
		{
			currentWave = Integer.parseInt(waveMatcher.group(1));
			startWaveState();
			log.info("Detected BA wave: {}", currentWave);
		}
	}

	private void updateRole()
	{
		if (client.getLocalPlayer() == null)
		{
			return;
		}

		String playerName = client.getLocalPlayer().getName();

		for (int i = 8; i <= 12; i++)
		{
			Widget playerWidget = client.getWidget(256, i);

			if (playerWidget == null)
			{
				continue;
			}


			if (!playerName.equals(playerWidget.getText()))
			{
				continue;
			}

			Widget roleWidget = client.getWidget(256, i + 10);

			if (roleWidget == null)
			{
				return;
			}

			BaRole newRole = getRoleFromModelId(roleWidget.getModelId());

			if (newRole != currentRole)
			{
				currentRole = newRole;
				log.info("Resolved role: {}", currentRole);
			}

			return;
		}
	}

	private BaRole getRoleFromModelId(int modelId)
	{
		switch (modelId)
		{
			case 20561:
				return BaRole.ATTACKER;

			case 20566:
				return BaRole.DEFENDER;

			case 20563:
				return BaRole.COLLECTOR;

			case 20569:
				return BaRole.HEALER;

			default:
				return null;
		}
	}

	private void startWaveState()
	{
		WaveInfo waveData = WaveData.get(currentWave);

		if (waveData == null)
		{
			return;
		}

		inBaSession = true;

		runnersRemaining = waveData.runners() + waveData.runnerReserves();
		rangersRemaining = waveData.rangers()+ waveData.rangerReserves();
		fightersRemaining = waveData.fighters() + waveData.fighterReserves();
		healersRemaining = waveData.healers() + waveData.healerReserves();
	}

	private void handleBaCompletion(String message)
	{
		if (message.contains("Wave 10 duration: "))
		{
			log.info("Wave 10 completed, resetting state");
			currentWave = -1;
			currentRole = null;
			inBaSession = false;
		}
	}

	public int getFightersRemaining()
	{
		return fightersRemaining;
	}

	public int getRunnersRemaining()
	{
		return runnersRemaining;
	}

	public int getHealersRemaining()
	{
		return healersRemaining;
	}

	public int getRangersRemaining()
	{
		return rangersRemaining;
	}

	public boolean isTrackingSpawns()
	{
		return config.trackSpawns();
	}

	public boolean isInBaSession()
	{
		return inBaSession;
	}

	@Provides
	BaWaveInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BaWaveInfoConfig.class);
	}
}
