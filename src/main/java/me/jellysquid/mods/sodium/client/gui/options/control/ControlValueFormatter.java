package me.jellysquid.mods.sodium.client.gui.options.control;

import net.minecraft.text.TranslatableText;

public interface ControlValueFormatter {
    static ControlValueFormatter guiScale() {
        return (v) -> (v == 0) ? new TranslatableText("Auto").getString() : v + "x";
    }

    static ControlValueFormatter fpsLimit() {
        return (v) -> (v == 260) ? new TranslatableText("Unlimited").getString() : v + " FPS";
    }

    static ControlValueFormatter brightness() {
        return (v) -> {
            if (v == 0) {
                return new TranslatableText("Moody").getString();
            } else if (v == 100) {
                return new TranslatableText("Bright").getString();
            } else {
                return v + "%";
            }
        };
    }

    String format(int value);

    static ControlValueFormatter percentage() {
        return (v) -> v + "%";
    }

    static ControlValueFormatter multiplier() {
        return (v) -> v + "x";
    }

    static ControlValueFormatter quantity(TranslatableText translatableText) {
        return (v) -> v + " " + translatableText;
    }

    static ControlValueFormatter quantityOrDisabled(String name, String disableText) {
        return (v) -> v == 0 ? disableText : v + " " + name;
    }

    static ControlValueFormatter number() {
        return String::valueOf;
    }
}
