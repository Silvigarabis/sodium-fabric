package me.jellysquid.mods.sodium.client.gui.options;

import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public enum OptionImpact implements TextProvider {
    LOW(Formatting.GREEN, "Low"),
    MEDIUM(Formatting.YELLOW, "Medium"),
    HIGH(Formatting.GOLD, "High"),
    EXTREME(Formatting.RED, "Extreme"),
    VARIES(Formatting.WHITE, "Varies");

    private final Text text;

    OptionImpact(Formatting color, String text) {
        this.text = new TranslatableText(text).formatted(color);    
    }

    @Override
    public Text getLocalizedName() {
        return this.text;
    }
}
