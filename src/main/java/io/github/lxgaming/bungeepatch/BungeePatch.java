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

import com.google.common.collect.Sets;
import io.github.lxgaming.bungeepatch.configuration.Config;
import io.github.lxgaming.bungeepatch.configuration.Configuration;
import io.github.lxgaming.bungeepatch.util.Logger;

import java.util.Optional;
import java.util.Set;

public class BungeePatch {
    
    public static final String ID = "bungeepatch";
    public static final String NAME = "BungeePatch";
    public static final String VERSION = "1.1.4";
    public static final String DESCRIPTION = "Patches the stupid out of the BungeeCord.";
    public static final String AUTHORS = "LX_Gaming";
    public static final String SOURCE = "https://github.com/LXGaming/BungeePatch";
    public static final String WEBSITE = "https://lxgaming.github.io/";
    
    private static BungeePatch instance;
    private final Logger logger;
    private final Configuration configuration;
    private final Set<String> verboseUsers;
    
    public BungeePatch() {
        instance = this;
        this.logger = new Logger();
        this.configuration = new Configuration(BungeePatchPlugin.getInstance().getDataFolder().toPath());
        this.verboseUsers = Sets.newConcurrentHashSet();
    }
    
    public void loadBungeePatch() {
        getLogger().info("Initializing...");
        reloadBungeePatch();
        getLogger().info("{} v{} has loaded", BungeePatch.NAME, BungeePatch.VERSION);
    }
    
    public boolean reloadBungeePatch() {
        if (!getConfiguration().loadConfiguration()) {
            return false;
        }
        
        getConfiguration().saveConfiguration();
        if (getConfig().map(Config::isDebug).orElse(false)) {
            getLogger().debug("Debug mode enabled");
        } else {
            getLogger().info("Debug mode disabled");
        }
        
        return true;
    }
    
    public static BungeePatch getInstance() {
        return instance;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Optional<Config> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
    
    public Set<String> getVerboseUsers() {
        return verboseUsers;
    }
}