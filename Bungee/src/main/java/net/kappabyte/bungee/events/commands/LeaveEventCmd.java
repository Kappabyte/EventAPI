package net.kappabyte.bungee.events.commands;

import net.kappabyte.bungee.events.API.EventSystemAPI;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class LeaveEventCmd extends Command {

    public static boolean canJoinEvent = false;

    public LeaveEventCmd() {
        super("leaveevent");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        if(!EventSystemAPI.leaveEvent(player)) {
            player.sendMessage(new ComponentBuilder("You aren't currently part of the event!").color(ChatColor.RED).create());
            return;
        }
    }
}