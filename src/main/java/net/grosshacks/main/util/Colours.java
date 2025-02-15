package net.grosshacks.main.util;

import net.grosshacks.main.GrossHacks;
import net.handbook.main.config.HandbookConfig;

public class Colours {

    public static int text() {
        return GrossHacks.handbookAvailable ? HandbookConfig.INSTANCE.textColor : -1;
    }

    public static int background(boolean isMouseOver) {
        if (GrossHacks.handbookAvailable)
            return isMouseOver ? HandbookConfig.INSTANCE.highlightColor : HandbookConfig.INSTANCE.tradeBackgroundColor;
        else return isMouseOver ? 1688906410 : 866822826;
    }

    public static int backgroundAlt() {
        return GrossHacks.handbookAvailable ? HandbookConfig.INSTANCE.screenHeadColor : 548055807;
    }

    public static int border() {
        return GrossHacks.handbookAvailable ? HandbookConfig.INSTANCE.bordersColor : -1;
    }

    public static int highlight() {
        return GrossHacks.handbookAvailable ? HandbookConfig.INSTANCE.favouriteColor : 2030023680;
    }
}
