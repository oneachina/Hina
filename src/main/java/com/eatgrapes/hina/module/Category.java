/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module;

import com.eatgrapes.hina.skia.font.Icon;

public enum Category {
    COMBAT("Combat", Icon.SWORDS),
    MOVEMENT("Movement", Icon.DIRECTIONS_RUN),
    RENDER("Render", Icon.IMAGE),
    PLAYER("Player", Icon.PERSON),
    MISC("Misc", Icon.SETTINGS);

    private final String name;
    private final String icon;

    Category(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }
}