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

import com.google.common.base.Strings;
import io.github.lxgaming.bungeepatch.BungeePatch;
import io.github.lxgaming.bungeepatch.configuration.Config;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;

import java.util.List;

public class DecodeHandler extends MinecraftDecoder {
    
    private final UserConnection userConnection;
    
    public DecodeHandler(UserConnection userConnection) {
        super(Protocol.GAME, false, userConnection.getPendingConnection().getVersion());
        this.userConnection = userConnection;
    }
    
    // Preserve default constructor
    public DecodeHandler(Protocol protocol, boolean server, int protocolVersion) {
        super(protocol, server, protocolVersion);
        this.userConnection = null;
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (this.userConnection == null) {
            super.decode(ctx, in, out);
            return;
        }
        
        try {
            in.markReaderIndex();
            super.decode(ctx, in, out);
        } catch (Exception ex) {
            if (!handle(ex)) {
                throw ex;
            }
            
            in.resetReaderIndex();
            if (BungeePatch.getInstance().getConfig().map(Config::isDebug).orElse(false) || BungeePatch.getInstance().getVerboseUsers().contains(this.userConnection.getName())) {
                BungeePatch.getInstance().getLogger().warn("{} ({})", ex.getCause().getMessage(), this.userConnection.getName());
                
                // Waterfall - https://github.com/PaperMC/Waterfall/blob/master/BungeeCord-Patches/0032-Dump-the-raw-hex-of-a-packet-on-a-decoding-error.patch
                if (ProxyServer.getInstance().getName().equals("Waterfall")) {
                    BungeePatch.getInstance().getLogger().warn("{} ({})", ex.getMessage(), this.userConnection.getName());
                } else {
                    BungeePatch.getInstance().getLogger().warn("{}:\n{} ({})", ex.getMessage(), ByteBufUtil.prettyHexDump(in), this.userConnection.getName());
                }
            }
            
            if (BungeePatch.getInstance().getConfig().map(Config::isDecodeForcePacket).orElse(false)) {
                // https://github.com/SpigotMC/BungeeCord/issues/1714
                ByteBuf copy = in.copy();
                in.skipBytes(in.readableBytes());
                
                // As the MinecraftDecoder failed to handle the packet the UpstreamBridge shouldn't be able to either
                this.userConnection.sendPacket(new PacketWrapper(null, copy));
            }
        }
    }
    
    private boolean handle(Exception ex) {
        // BadPacketException - Caused by NotEnoughIDs
        if (ex instanceof DecoderException) {
            // Avoid capturing OverflowPacketException
            if (ex.getCause() instanceof BadPacketException) {
                // Waterfall - https://github.com/PaperMC/Waterfall/blob/master/BungeeCord-Patches/0048-Handle-empty-minecraft-packets.patch
                return !Strings.nullToEmpty(ex.getCause().getMessage()).equals("Empty minecraft packet!");
            }
        }
        
        return false;
    }
}