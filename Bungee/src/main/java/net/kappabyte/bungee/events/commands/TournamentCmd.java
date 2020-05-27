package net.kappabyte.bungee.events.commands;

import net.kappabyte.bungee.events.API.tournament.Tournament;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TournamentCmd extends Command {

    public static boolean canJoinEvent = false;

    public TournamentCmd() {
        super("tournament", "tournament.manage", "tourn", "t");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("create")) {
                if(Tournament.getTournamentActive()) {
                    player.sendMessage(new ComponentBuilder("There is an ongoing tournament. Please end that one before creating a new one!").color(ChatColor.GREEN).create());
                    return;
                }
                Tournament.createTournament(player);
                player.sendMessage(new ComponentBuilder("Created new tournament! manage teams using /teams").color(ChatColor.GREEN).create());
            } else if(args[0].equalsIgnoreCase("end")) {
                if(Tournament.getTournamentActive()) {
                    Tournament.endTournament();
                    player.sendMessage(new ComponentBuilder("Ended the tournament!").color(ChatColor.GREEN).create());
                } else {
                    player.sendMessage(new ComponentBuilder("There is no ongoing tournaments. You must create a tournament before you can end it!").color(ChatColor.GREEN).create());
                }
            }
        }
    }

}