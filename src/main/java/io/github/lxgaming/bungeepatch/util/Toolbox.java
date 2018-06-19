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

import net.md_5.bungee.netty.ChannelWrapper;
import net.md_5.bungee.netty.PacketHandler;

import java.lang.reflect.Field;
import java.util.Optional;

public class Toolbox {
    
    public static boolean setHandler(Object object, PacketHandler packetHandler) {
        try {
            for (Field field : object.getClass().getDeclaredFields()) {
                if (!PacketHandler.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                
                field.setAccessible(true);
                field.set(object, packetHandler);
                return true;
            }
            
            throw new NoSuchFieldException();
        } catch (Exception ex) {
            return false;
        }
    }
    
    public static <T> Optional<ChannelWrapper> getChannelWrapper(Class<? extends T> classOfT, T instance) {
        try {
            for (Field field : classOfT.getDeclaredFields()) {
                if (!ChannelWrapper.class.isAssignableFrom(field.getType())) {
                    continue;
                }
                
                field.setAccessible(true);
                Object channelWrapper = field.get(instance);
                if (channelWrapper != null) {
                    return Optional.of((ChannelWrapper) channelWrapper);
                }
                
                return Optional.empty();
            }
            
            throw new NoSuchFieldException();
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
}