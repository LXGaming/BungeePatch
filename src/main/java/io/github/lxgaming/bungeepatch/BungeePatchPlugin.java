/*
 * Copyright 2019 Alex Thomson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.lxgaming.bungeepatch;

import io.github.lxgaming.bungeepatch.command.BungeePatchCommand;
import io.github.lxgaming.bungeepatch.configuration.Config;
import io.github.lxgaming.bungeepatch.listener.BungeePatchListener;
import io.github.lxgaming.bungeepatch.util.Logger;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePatchPlugin extends Plugin {
    
    private static BungeePatchPlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        BungeePatch bungeePatch = new BungeePatch();
        bungeePatch.getLogger()
                .add(Logger.Level.INFO, getLogger()::info)
                .add(Logger.Level.WARN, getLogger()::warning)
                .add(Logger.Level.ERROR, getLogger()::severe)
                .add(Logger.Level.DEBUG, message -> {
                    if (BungeePatch.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        BungeePatch.getInstance().getLogger().info(message);
                    }
                });
        
        bungeePatch.loadBungeePatch();
        
        getProxy().getPluginManager().registerCommand(getInstance(), new BungeePatchCommand());
        getProxy().getPluginManager().registerListener(getInstance(), new BungeePatchListener());
    }
    
    public static BungeePatchPlugin getInstance() {
        return instance;
    }
}