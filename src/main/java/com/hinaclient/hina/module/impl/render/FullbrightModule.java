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

package com.hinaclient.hina.module.impl.render;

import com.hinaclient.hina.module.Category;
import com.hinaclient.hina.module.Module;
import com.hinaclient.hina.setting.NumberSetting;

public class FullbrightModule extends Module {
    private final NumberSetting Gamma = new NumberSetting("Gamma", 4.0, 0.0, 4.0, 0.1);

    public FullbrightModule() {
        super("Fullbright", Category.RENDER);

        addSetting(Gamma);
    }

    public NumberSetting getGamma() {
        return Gamma;
    }
}