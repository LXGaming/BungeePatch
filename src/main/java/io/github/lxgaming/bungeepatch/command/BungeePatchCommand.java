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

package io.github.lxgaming.bungeepatch.command;

import io.github.lxgaming.bungeepatch.BungeePatch;
import io.github.lxgaming.bungeepatch.BungeePatchPlugin;
import io.github.lxgaming.bungeepatch.util.Reference;
import io.github.lxgaming.bungeepatch.util.Toolbox;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class BungeePatchCommand extends Command {
    
    public BungeePatchCommand() {
        super("bungeepatch");
    }
    
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("help") && sender.hasPermission(String.format("%s.help.base", Reference.ID))) {
            sender.sendMessage(Toolbox.getTextPrefix()
                    .append("Help").color(ChatColor.GREEN)
                    .append("\n> ").color(ChatColor.BLUE)
                    .append("/").color(ChatColor.GREEN).append(Reference.ID).color(ChatColor.GREEN).append(" reload").color(ChatColor.GREEN)
                    .append("\n> ").color(ChatColor.BLUE)
                    .append("/").color(ChatColor.GREEN).append(Reference.ID).color(ChatColor.GREEN).append(" verbose <Username>").color(ChatColor.GREEN)
                    .create());
            return;
        }
        
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && sender.hasPermission(String.format("%s.reload.base", Reference.ID))) {
            BungeePatchPlugin.getInstance().getProxy().getScheduler().runAsync(BungeePatchPlugin.getInstance(), () -> {
                if (BungeePatch.getInstance().reloadBungeePatch()) {
                    sender.sendMessage(Toolbox.getTextPrefix().append("Configuration reloaded").color(ChatColor.GREEN).create());
                } else {
                    sender.sendMessage(Toolbox.getTextPrefix().append("An error occurred. Please check the console").color(ChatColor.RED).create());
                }
            });
            
            return;
        }
        
        if (args.length >= 1 && args[0].equalsIgnoreCase("verbose") && sender.hasPermission(String.format("%s.verbose.base", Reference.ID))) {
            if (args.length == 1) {
                sender.sendMessage(Toolbox.getTextPrefix().append("Invalid arguments: <Username>").color(ChatColor.RED).create());
                return;
            }
            
            // Usernames are used as the target user may not be online or is unable to connect to network
            String name = args[1];
            if (!name.matches("[a-zA-Z0-9_]{3,16}")) {
                sender.sendMessage(Toolbox.getTextPrefix().append("Invalid username").color(ChatColor.RED).create());
                return;
            }
            
            if (BungeePatch.getInstance().getVerboseUsers().contains(name)) {
                BungeePatch.getInstance().getVerboseUsers().remove(name);
                sender.sendMessage(Toolbox.getTextPrefix()
                        .append("Verbose logging ").color(ChatColor.AQUA)
                        .append("disabled").color(ChatColor.RED)
                        .append(" for ").color(ChatColor.AQUA)
                        .append(name).color(ChatColor.GREEN)
                        .create());
            } else {
                BungeePatch.getInstance().getVerboseUsers().add(name);
                sender.sendMessage(Toolbox.getTextPrefix()
                        .append("Verbose logging ").color(ChatColor.AQUA)
                        .append("enabled").color(ChatColor.GREEN)
                        .append(" for ").color(ChatColor.AQUA)
                        .append(name).color(ChatColor.GREEN)
                        .create());
            }
            
            return;
        }
        
        if (args.length != 0) {
            sender.sendMessage(new ComponentBuilder("")
                    .append("Use ").color(ChatColor.BLUE)
                    .append("/").color(ChatColor.GREEN).append(Reference.ID).color(ChatColor.GREEN).append(" help ").color(ChatColor.GREEN)
                    .append("to view available commands.").color(ChatColor.BLUE)
                    .create());
            return;
        }
        
        sender.sendMessage(Toolbox.getPluginInformation().create());
    }
}