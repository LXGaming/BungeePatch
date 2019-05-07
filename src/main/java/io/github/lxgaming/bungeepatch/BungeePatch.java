/*
 * Copyright 2018 Alex Thomson
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

import io.github.lxgaming.bungeepatch.configuration.Config;
import io.github.lxgaming.bungeepatch.listener.BungeePatchListener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.util.Optional;

public class BungeePatch extends Plugin {
    
    private static BungeePatch instance;
    private final Config config = new Config();
    
    @Override
    public void onEnable() {
        instance = this;
        getConfig().loadConfig();
        getProxy().getPluginManager().registerListener(getInstance(), new BungeePatchListener());
        getLogger().info("BungeePatch has started.");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("BungeePatch has stopped.");
    }
    
    public static BungeePatch getInstance() {
        return instance;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public Optional<Configuration> getConfiguration() {
        if (getConfig() != null) {
            return Optional.ofNullable(getConfig().getConfiguration());
        }
        
        return Optional.empty();
    }
}