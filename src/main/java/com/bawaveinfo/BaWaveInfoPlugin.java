package com.bawaveinfo;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.ui.overlay.infobox.Counter;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.ChatMessageType;
import net.runelite.api.MessageNode;
import net.runelite.client.eventbus.Subscribe;

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
	private int currentWave = 10;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("Example started!");
		overlayManager.add(overlay);
		currentWave = 10;
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("Example stopped!");
		overlayManager.remove(overlay);
		currentWave = 1;
	}

	public int getCurrentWave()
	{
		return currentWave;
	}

	public WaveInfo getWaveInfo()
	{
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

	private void extractWave(String message)
	{
		String[] parts = message.split(" ");

		for (int i = 0; i < parts.length; i++)
			{
				if (parts[i].equalsIgnoreCase("Wave"))
				{
					currentWave = Integer.parseInt(parts[i + 1].replaceAll("\\D", ""));
					return;
				}}
	}


	@Provides
	BaWaveInfoConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BaWaveInfoConfig.class);
	}
}
