package com.rapidwarning;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class RapidWarningPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RapidWarningPlugin.class);
		RuneLite.main(args);
	}
}
