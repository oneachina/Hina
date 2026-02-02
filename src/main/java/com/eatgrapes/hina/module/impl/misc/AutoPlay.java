package com.eatgrapes.hina.module.impl.misc;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.impl.server.ServerMessageEvent;
import com.eatgrapes.hina.module.Category;
import com.eatgrapes.hina.module.Module;
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
