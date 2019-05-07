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

package io.github.lxgaming.bungeepatch.util;

import com.google.common.base.Strings;
import io.github.lxgaming.bungeepatch.BungeePatch;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import net.md_5.bungee.protocol.BadPacketException;
import net.md_5.bungee.protocol.MinecraftDecoder;
import net.md_5.bungee.protocol.PacketWrapper;
import net.md_5.bungee.protocol.Protocol;

import java.util.List;

public final class WrappedMinecraftDecoder extends MinecraftDecoder {
    
    private final boolean debug = BungeePatch.getInstance().getConfiguration().map(configuration -> configuration.getBoolean("BungeePatch.Debug")).orElse(false);
    private final boolean forcePacket = BungeePatch.getInstance().getConfiguration().map(configuration -> configuration.getBoolean("BungeePatch.ForcePacket")).orElse(false);
    
    public WrappedMinecraftDecoder(Protocol protocol, boolean server, int protocolVersion) {
        super(protocol, server, protocolVersion);
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        try {
            in.markReaderIndex();
            super.decode(ctx, in, out);
        } catch (DecoderException ex) {
            if (!(ex.getCause() instanceof BadPacketException)) {
                throw ex;
            }
            
            // Waterfall
            if (Strings.nullToEmpty(ex.getCause().getMessage()).equals("Empty minecraft packet!")) {
                throw ex;
            }
            
            if (this.debug) {
                BungeePatch.getInstance().getLogger().warning(ex.getCause().getMessage());
                BungeePatch.getInstance().getLogger().warning(ex.getMessage());
            }
            
            if (this.forcePacket) {
                in.resetReaderIndex();
                ByteBuf slice = in.copy(); // https://github.com/SpigotMC/BungeeCord/issues/1714
                in.skipBytes(in.readableBytes());
                out.add(new PacketWrapper(null, slice));
            }
        }
    }
}