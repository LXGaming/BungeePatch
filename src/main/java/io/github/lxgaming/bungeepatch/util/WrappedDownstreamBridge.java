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

package io.github.lxgaming.bungeepatch.util;

import io.github.lxgaming.bungeepatch.BungeePatch;
import net.md_5.bungee.ServerConnection;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.DownstreamBridge;
import net.md_5.bungee.protocol.PacketWrapper;

public final class WrappedDownstreamBridge extends DownstreamBridge {
    
    private final boolean debug = BungeePatch.getInstance().getConfiguration().map(configuration -> configuration.getBoolean("BungeePatch.Debug")).orElse(false);
    private final boolean forcePacket = BungeePatch.getInstance().getConfiguration().map(configuration -> configuration.getBoolean("BungeePatch.ForcePacket")).orElse(false);
    private final UserConnection userConnection;
    private final ServerConnection serverConnection;
    
    public WrappedDownstreamBridge(UserConnection userConnection) {
        this(ProxyServer.getInstance(), userConnection, userConnection.getServer());
    }
    
    public WrappedDownstreamBridge(ProxyServer proxyServer, UserConnection userConnection, ServerConnection serverConnection) {
        super(proxyServer, userConnection, serverConnection);
        this.userConnection = userConnection;
        this.serverConnection = serverConnection;
    }
    
    @Override
    public void handle(PacketWrapper packetWrapper) throws Exception {
        try {
            packetWrapper.buf.markReaderIndex();
            super.handle(packetWrapper);
        } catch (IllegalArgumentException ex) {
            if (this.debug) {
                BungeePatch.getInstance().getLogger().warning(ex.getMessage() + " (" + this.userConnection.getName() + ")");
            }
            
            if (this.forcePacket && packetWrapper.buf.refCnt() > 0) {
                packetWrapper.buf.resetReaderIndex();
                this.userConnection.sendPacket(packetWrapper);
            }
        }
    }
    
    @Override
    public String toString() {
        return "[" + this.userConnection.getName() + "] <-> " + getClass().getSimpleName() + " <-> [" + this.serverConnection.getInfo().getName() + "]";
    }
}