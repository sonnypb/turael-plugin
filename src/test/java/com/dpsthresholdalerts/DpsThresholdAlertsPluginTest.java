package com.dpsthresholdalerts;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DpsThresholdAlertsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DpsThresholdAlertsPlugin.class);
		RuneLite.main(args);
	}
}