package com.eatgrapes.hina.event.fabric;

import com.eatgrapes.hina.HinaClient;
import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.impl.server.ServerMessageEvent;
import com.eatgrapes.hina.skia.font.FontManager;
import com.eatgrapes.hina.ui.ClickGuiScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 11:44
 */
public class HinaHandler {
    public HinaHandler() {}

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.getResourceManager() != null && !FontManager.INSTANCE.isInitialized()) {
                try {
                    FontManager.INSTANCE.init();
                } catch (Exception ignored) {
                }
            }
            while (HinaClient.INSTANCE.clickGuiKey.wasPressed()) {
                if (!(client.currentScreen instanceof ClickGuiScreen)) {
                    HinaClient.INSTANCE.moduleManager.getClickGuiModule().setEnabled(true);
                }
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            HinaClient.INSTANCE.configManager.save();
        });
    }

    public static void initEvent() {
        ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            EventBus.INSTANCE.post(new ServerMessageEvent(message, overlay));
        });
    }
}
