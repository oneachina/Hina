/*
 * Hina Client
 * Copyright (C) 2026 Hina Client
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.hinaclient.hina.utils.chat;

import com.hinaclient.hina.module.impl.render.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public class ChatUtils {
    private static final String PREFIX = "§7[§bHina§7] ";
    private static final String PREFIX_FORMATTED = ChatFormatting.GRAY + "[" + ChatFormatting.AQUA + "Hina" + ChatFormatting.GRAY + "] ";

    private static final Component PREFIX_TEXT = Component.literal("[")
            .withStyle(ChatFormatting.GRAY)
            .append(Component.literal("Hina").withStyle(ChatFormatting.AQUA))
            .append(Component.literal("] ").withStyle(ChatFormatting.GRAY));

    public static void debug(String prefix, String message) {
        if (!ClickGuiModule.debug.getValue()) return;

        MutableComponent text = Component.literal(PREFIX)
                .append(Component.literal("[" + prefix + "] ").withStyle(ChatFormatting.LIGHT_PURPLE))
                .append(Component.literal(message).withStyle(ChatFormatting.GRAY));

        component(text);
    }

    public static void component(Component component) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) return;

        minecraft.execute(() -> {
            ChatComponent chat = minecraft.gui.getChat();
            if (chat != null) {
                chat.addMessage(component);
            }
        });
    }

    public static void addChatMessage(String message) {
        addChatMessage(true, message);
    }

    public static void addChatMessage(Component message) {
        if (message == null || message.getString().isEmpty()) return;
        MutableComponent fullMessage = Component.empty().append(PREFIX_TEXT).append(message);
        component(fullMessage);
    }

    public static void addChatMessage(boolean prefix, String message) {
        if (message == null || message.isEmpty()) return;
        String formattedMessage = (prefix ? PREFIX : "") + message;
        component(Component.literal(formattedMessage));
    }

    public static void addFormattedMessage(String message, ChatFormatting... formattings) {
        MutableComponent text = Component.literal(PREFIX_FORMATTED).withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(message).withStyle(formattings));
        component(text);
    }

    public static void error(String message) { addFormattedMessage(message, ChatFormatting.RED); }
    public static void success(String message) { addFormattedMessage(message, ChatFormatting.GREEN); }
    public static void warning(String message) { addFormattedMessage(message, ChatFormatting.YELLOW); }
    public static void info(String message) { addFormattedMessage(message, ChatFormatting.BLUE); }
}