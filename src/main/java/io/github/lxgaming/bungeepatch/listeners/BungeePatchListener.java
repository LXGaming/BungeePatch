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

package io.github.lxgaming.bungeepatch.listeners;

import io.github.lxgaming.bungeepatch.BungeePatch;
import io.github.lxgaming.bungeepatch.util.Toolbox;
import io.github.lxgaming.bungeepatch.util.WrappedDownstreamBridge;
import io.github.lxgaming.bungeepatch.util.WrappedUpstreamBridge;
import io.netty.channel.Channel;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PipelineUtils;

public class BungeePatchListener implements Listener {
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {
        UserConnection userConnection = (UserConnection) event.getPlayer();
        Channel channel = Toolbox.getChannelWrapper(userConnection.getClass(), userConnection).map(ChannelWrapper::getHandle).orElse(null);
        
        // Cannot reference HandlerBoss class as it breaks SpongePls
        if (channel != null && Toolbox.setHandler(channel.pipeline().get(PipelineUtils.BOSS_HANDLER), new WrappedUpstreamBridge(userConnection))) {
            BungeePatch.getInstance().getLogger().info("Successfully wrapped upstream for " + userConnection.getName());
        } else {
            BungeePatch.getInstance().getLogger().warning("Failed to wrap upstream for " + userConnection.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerSwitch(ServerSwitchEvent event) {
        UserConnection userConnection = (UserConnection) event.getPlayer();
        Channel channel = Toolbox.getChannelWrapper(userConnection.getServer().getClass(), userConnection.getServer()).map(ChannelWrapper::getHandle).orElse(null);
        
        // Cannot reference HandlerBoss class as it breaks SpongePls
        if (channel != null && Toolbox.setHandler(channel.pipeline().get(PipelineUtils.BOSS_HANDLER), new WrappedDownstreamBridge(userConnection))) {
            BungeePatch.getInstance().getLogger().info("Successfully wrapped downstream for " + userConnection.getName());
        } else {
            BungeePatch.getInstance().getLogger().warning("Failed to wrap downstream for " + userConnection.getName());
        }
    }
}