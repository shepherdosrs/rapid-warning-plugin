package com.rapidwarning;

import lombok.Getter;

/**
 * Attack style, identified by the index of its button in the combat tab.
 * <p>
 * VarPlayer COM_MODE holds <em>which button is selected</em> (0-3), not a global
 * style id. These indices are therefore only meaningful for the bow and crossbow
 * weapon categories, which both present exactly these three styles. Do not reuse
 * them for melee weapons, where the same index means something else entirely.
 */
@Getter
public enum RangedStyle
{
	ACCURATE(0, "Accurate"),
	RAPID(1, "Rapid"),
	LONGRANGE(3, "Longrange");

	private final int index;
	private final String displayName;

	RangedStyle(int index, String displayName)
	{
		this.index = index;
		this.displayName = displayName;
	}

	public static RangedStyle fromIndex(int index)
	{
		for (RangedStyle style : values())
		{
			if (style.index == index)
			{
				return style;
			}
		}

		return null;
	}
}
