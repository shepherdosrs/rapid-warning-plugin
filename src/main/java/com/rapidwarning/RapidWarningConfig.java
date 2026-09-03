package com.rapidwarning;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(RapidWarningPlugin.CONFIG_GROUP)
public interface RapidWarningConfig extends Config
{
	@Alpha
	@ConfigItem(
		keyName = "overlayColor",
		name = "Overlay colour",
		description = "Overlay background colour"
	)
	default Color overlayColor()
	{
		return new Color(255, 0, 0, 150);
	}
}
