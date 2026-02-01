package com.eatgrapes.hina.event.mod;

import com.eatgrapes.hina.event.Event;

/**
 * @Author: oneachina
 * @Date: 2026/2/1 12:38
 */
public class KeyEvent extends Event {
    private final int key;

    public KeyEvent(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }
}
