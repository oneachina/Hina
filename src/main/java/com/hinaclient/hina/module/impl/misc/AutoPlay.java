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

package com.hinaclient.hina.module.impl.misc;

import com.hinaclient.hina.event.EventListener;
import com.hinaclient.hina.event.impl.server.ServerMessageEvent;
import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.regex.Pattern;

/**
 * @Author: oneachina
 * @Date: 2026/1/30 16:26
 */
public class AutoPlay extends Module {
    public AutoPlay() {
        super("AutoPlay", Category.MISC);
    }

    @EventListener
    public void onServerMessage(ServerMessageEvent event) {
        String content = event.getMessage().getString();

        if (Minecraft.getInstance().player != null) {
            Pattern win = Pattern.compile("^恭喜! " + Minecraft.getInstance().player.getName().getString() + " 在地图 (.+) 获胜!$"); // win
            Pattern lose = Pattern.compile("^你现在是观察者!$");

            if ((win.matcher(content).find() || lose.matcher(content).find())) {
                Minecraft.getInstance().gui.getChat().addMessage(Component.literal("HeyPixel"));
                Minecraft.getInstance().player.connection.sendCommand("/again");
            }
        }
    }
}
