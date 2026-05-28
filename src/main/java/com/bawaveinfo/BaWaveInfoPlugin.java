package com.bawaveinfo;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;

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

		if (!event.getKey().equals("showOverlay"))
		{
			return;
		}

		if (config.showOverlay())
		{
			overlayManager.add(overlay);
		}
		else
		{
			overlayManager.remove(overlay);
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

		if (message.contains("Wave"))
		{
			extractWave(message);
		}
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

	private void extractWave(String message)
	{

		if (!message.startsWith("---- Wave:"))
		{
			return;
		}

		Matcher m = Pattern.compile("Wave:\\s*(\\d+)").matcher(message);

		if (m.find())
		{
			currentWave = Integer.parseInt(m.group(1));
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




	@Provides
	BaWaveInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BaWaveInfoConfig.class);
	}
}
