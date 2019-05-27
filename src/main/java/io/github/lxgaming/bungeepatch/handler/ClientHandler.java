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


import io.github.lxgaming.bungeepatch.util.Reference;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.connection.UpstreamBridge;

public class ClientHandler extends UpstreamBridge {
    
    private final UserConnection userConnection;
    
    public ClientHandler(UserConnection userConnection) {
        this(ProxyServer.getInstance(), userConnection);
    }
    
    // Preserve default constructor
    public ClientHandler(ProxyServer bungee, UserConnection con) {
        super(bungee, con);
        this.userConnection = con;
    }
    
    private boolean handle(Exception ex) {
        return false;
    }
    
    @Override
    public String toString() {
        return super.toString().replace(getClass().getSuperclass().getSimpleName(), String.format("%s:%s", Reference.NAME, getClass().getSimpleName()));
    }
}