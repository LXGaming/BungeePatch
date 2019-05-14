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

package io.github.lxgaming.bungeepatch.handler;

import io.github.lxgaming.bungeepatch.BungeePatch;
import io.github.lxgaming.bungeepatch.configuration.Config;
import io.github.lxgaming.bungeepatch.util.Reference;
import io.netty.buffer.ByteBufUtil;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.PacketWrapper;

public class ServerHandler extends DownstreamBridge {
    
    private final UserConnection userConnection;
    
    public ServerHandler(UserConnection userConnection) {
        this(ProxyServer.getInstance(), userConnection, userConnection.getServer());
    }
    
    // Preserve default constructor
    public ServerHandler(ProxyServer bungee, UserConnection con, ServerConnection server) {
        super(bungee, con, server);
        this.userConnection = con;
    }
    
    @Override
    public void handle(PacketWrapper packet) throws Exception {
        try {
            packet.buf.markReaderIndex();
            super.handle(packet);
        } catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
            // IllegalArgumentException - Thrown by EntityMap (Unknown meta type)
            // IndexOutOfBoundsException - Thrown by EntityMap
            
            packet.buf.resetReaderIndex();
            if (BungeePatch.getInstance().getConfig().map(Config::isDebug).orElse(false) || BungeePatch.getInstance().getVerboseUsers().contains(this.userConnection.getName())) {
                BungeePatch.getInstance().getLogger().warn("{}:\n{} ({})", ex.getMessage(), ByteBufUtil.prettyHexDump(packet.buf), this.userConnection.getName());
            }
            
            if (BungeePatch.getInstance().getConfig().map(Config::isServerForcePacket).orElse(false) && packet.buf.refCnt() > 0) {
                this.userConnection.sendPacket(packet);
            }
        }
    }
    
    @Override
    public String toString() {
        return super.toString().replace(getClass().getSuperclass().getSimpleName(), String.format("%s:%s", Reference.NAME, getClass().getSimpleName()));
    }
}