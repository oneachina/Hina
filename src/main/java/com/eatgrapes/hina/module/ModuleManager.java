/**
 * @author Eatgrapes
 * @link github.com/Eatgrapes
 */
package com.eatgrapes.hina.module;

import com.eatgrapes.hina.event.EventListener;
import com.eatgrapes.hina.event.EventBus;
import com.eatgrapes.hina.event.mod.KeyEvent;
import com.eatgrapes.hina.module.impl.movement.Flight;
import com.eatgrapes.hina.module.impl.player.NoFall;
import com.eatgrapes.hina.module.impl.render.ClickGuiModule;
import com.eatgrapes.hina.module.impl.render.ESPModule;
import com.eatgrapes.hina.module.impl.render.FullbrightModule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    private ClickGuiModule clickGuiModule;
    private final List<Module> modules = new ArrayList<>();

    public void init() {
        EventBus.INSTANCE.register(this);
        clickGuiModule = new ClickGuiModule();
        modules.add(clickGuiModule);

        modules.add(new Flight());
        modules.add(new ESPModule());
        modules.add(new FullbrightModule());
        modules.add(new NoFall());
    }

    @EventListener
    public void onKey(KeyEvent event) {
        if (event.getKey() <= 0) return;
        modules.forEach(m -> {
            if (m.getKey() == event.getKey()) {
                m.toggle();
            }
        });
    }

    public ClickGuiModule getClickGuiModule() {
        return clickGuiModule;
    }

    public List<Module> getModules() {
        return modules;
    }

    public List<Module> getModulesByCategory(Category category) {
        return modules.stream().filter(m -> m.getCategory() == category).collect(Collectors.toList());
    }

    public Module getModuleByName(String name) {
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}