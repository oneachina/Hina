package com.eatgrapes.hina.event.impl.server;

import com.eatgrapes.hina.event.Event;
import net.minecraft.text.Text;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 11:48
 */
public class ServerMessageEvent extends Event {
    private Text message;
    private boolean overlay;

    public ServerMessageEvent(Text message, boolean overlay) {
        this.message = message;
        this.overlay = overlay;
    }

    public Text getMessage() {
        return message;
    }

    public void setMessage(Text message) {
        this.message = message;
    }

    public boolean isOverlay() {
        return overlay;
    }

    public void setOverlay(boolean overlay) {
        this.overlay = overlay;
    }
}
