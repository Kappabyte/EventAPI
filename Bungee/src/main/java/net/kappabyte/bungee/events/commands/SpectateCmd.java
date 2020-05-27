package net.kappabyte.bungee.events.commands;

import net.kappabyte.bungee.events.Events;
import net.kappabyte.bungee.events.API.EventSystemAPI;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class SpectateCmd extends Command {

    public static boolean canJoinEvent = false;

    public SpectateCmd() {
        super("spectate");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        Events.instance.getProxy().getServers().get("event").ping(new Callback<ServerPing>() {
         
            @Override
            public void done(ServerPing result, Throwable error) {
                if(!EventSystemAPI.addSpectator(player)) {
                    player.sendMessage(new ComponentBuilder("You are already spectating the event!").color(ChatColor.RED).create());
                    return;
                }
            }
        });
    }

}