package net.grosshacks.main.util;

import net.grosshacks.main.GrossHacks;
import net.handbook.main.config.HandbookConfig;

public class Colours {

    private static final HandbookConfig config = HandbookConfig.INSTANCE;

    public static int text() {
        return GrossHacks.handbookAvailable ? config.textColor : -1;
    }

    public static int background(boolean isMouseOver) {
        if (GrossHacks.handbookAvailable)
            return isMouseOver ? config.highlightColor : config.tradeBackgroundColor;
        else return isMouseOver ? 1688906410 : 866822826;
    }

    public static int backgroundAlt() {
        return GrossHacks.handbookAvailable ? config.screenHeadColor : 548055807;
    }

    public static int border() {
        return GrossHacks.handbookAvailable ? config.bordersColor : -1;
    }

    public static int highlight() {
        return GrossHacks.handbookAvailable ? config.favouriteColor : 2030023680;
    }
}
