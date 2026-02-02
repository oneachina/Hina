package com.eatgrapes.hina.event.impl.server;

import com.eatgrapes.hina.event.Event;
import net.minecraft.network.chat.Component;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 11:48
 */
public class ServerMessageEvent extends Event {
    private Component message;
    private boolean overlay;

    public ServerMessageEvent(Component message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    public Component getMessage() {
        return message;
    }

    public void setMessage(Component message) {
        this.message = message;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public void setOverlay(boolean overlay) {
        this.overlay = overlay;
    }
}
