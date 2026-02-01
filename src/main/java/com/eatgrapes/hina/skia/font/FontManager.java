/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.skia.font;

import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Font;
import io.github.humbleui.skija.Typeface;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class FontManager {
    public static final FontManager INSTANCE = new FontManager();
    
    private Typeface textTypeface;
    private Typeface iconTypeface;
    
    private final Map<Float, Font> textFonts = new HashMap<>();
    private final Map<Float, Font> iconFonts = new HashMap<>();

    private boolean initialized = false;

    public boolean isInitialized() {
        return initialized;
    }

    public void init() {
        if (initialized) return;
        try {
            InputStream textStream = MinecraftClient.getInstance().getResourceManager()
                .getResource(Identifier.of("hina", "fonts/pingfang-regular.ttf")).get().getInputStream();
            byte[] textBytes = textStream.readAllBytes();
            textTypeface = Typeface.makeFromData(Data.makeFromBytes(textBytes));
            
            InputStream iconStream = MinecraftClient.getInstance().getResourceManager()
                .getResource(Identifier.of("hina", "fonts/icon.ttf")).get().getInputStream();
            byte[] iconBytes = iconStream.readAllBytes();
            iconTypeface = Typeface.makeFromData(Data.makeFromBytes(iconBytes));
            
            initialized = true;
        } catch (Exception ignored) {
        }
    }

    public Font getIconFont(float size) {
        return iconFonts.computeIfAbsent(size, s -> new Font(iconTypeface, s));
    }

    public Font getTextFont(float size) {
        return textFonts.computeIfAbsent(size, s -> new Font(textTypeface, s));
    }
}