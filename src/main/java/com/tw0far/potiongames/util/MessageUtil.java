package com.tw0far.potiongames.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

/**
 * Utility methods for message/component creation.
 */
public class MessageUtil {
    private MessageUtil() {}
    
    /**
     * Create a colored message
     */
    public static Component createMessage(String message, NamedTextColor color) {
        return Component.text(message)
            .color(color)
            .decoration(TextDecoration.ITALIC, false);
    }
    
    /**
     * Create an error message
     */
    public static Component createError(String message) {
        return createMessage(message, NamedTextColor.RED);
    }
    
    /**
     * Create a success message
     */
    public static Component createSuccess(String message) {
        return createMessage(message, NamedTextColor.GREEN);
    }
    
    /**
     * Create an info message
     */
    public static Component createInfo(String message) {
        return createMessage(message, NamedTextColor.GRAY);
    }
    
    /**
     * Create a warning message
     */
    public static Component createWarning(String message) {
        return createMessage(message, NamedTextColor.YELLOW);
    }
}
