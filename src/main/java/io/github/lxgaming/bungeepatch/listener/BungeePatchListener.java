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

package io.github.lxgaming.bungeepatch.listener;

import io.github.lxgaming.bungeepatch.BungeePatch;
import io.github.lxgaming.bungeepatch.handler.ClientHandler;
import io.github.lxgaming.bungeepatch.handler.DecodeHandler;
import io.github.lxgaming.bungeepatch.handler.ServerHandler;
import io.github.lxgaming.bungeepatch.util.Toolbox;
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
        if (channel == null) {
            BungeePatch.getInstance().getLogger().error("Channel is null for {}", userConnection.getName());
            return;
        }
        
        // Cannot reference HandlerBoss class as it breaks SpongePls
        if (Toolbox.setHandler(channel.pipeline().get(PipelineUtils.BOSS_HANDLER), new ClientHandler(userConnection))) {
            BungeePatch.getInstance().getLogger().debug("Successfully wrapped upstream for {}", userConnection.getName());
        } else {
            BungeePatch.getInstance().getLogger().warn("Failed to wrap upstream for {}", userConnection.getName());
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerSwitch(ServerSwitchEvent event) {
        UserConnection userConnection = (UserConnection) event.getPlayer();
        Channel channel = Toolbox.getChannelWrapper(userConnection.getServer().getClass(), userConnection.getServer()).map(ChannelWrapper::getHandle).orElse(null);
        if (channel == null) {
            BungeePatch.getInstance().getLogger().error("Channel is null for {}", userConnection.getName());
            return;
        }
        
        // Cannot reference HandlerBoss class as it breaks SpongePls
        if (Toolbox.setHandler(channel.pipeline().get(PipelineUtils.BOSS_HANDLER), new ServerHandler(userConnection))) {
            BungeePatch.getInstance().getLogger().debug("Successfully wrapped downstream for " + userConnection.getName());
        } else {
            BungeePatch.getInstance().getLogger().warn("Failed to wrap downstream for {}", userConnection.getName());
        }
        
        DecodeHandler decodeHandler = new DecodeHandler(userConnection);
        if (channel.pipeline().replace(PipelineUtils.PACKET_DECODER, PipelineUtils.PACKET_DECODER, decodeHandler) != null) {
            BungeePatch.getInstance().getLogger().debug("Successfully wrapped decoder for {}", userConnection.getName());
        } else {
            BungeePatch.getInstance().getLogger().warn("Failed to wrap decoder for {}", userConnection.getName());
        }
    }
}