package net.kappabyte.bungee.events.commands;

import net.kappabyte.bungee.events.Events;
import net.kappabyte.bungee.events.API.EventSystemAPI;
import net.kappabyte.bungee.events.API.tournament.Tournament;
import net.kappabyte.bungee.events.API.tournament.TournamentTeam;
import net.md_5.bungee.api.Callback;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TeamCmd extends Command {

    public static boolean canJoinEvent = false;

    public TeamCmd() {
        super("teams");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProxiedPlayer player = (ProxiedPlayer) sender;
        
        if(args.length >= 1) {
            if(args[0].equalsIgnoreCase("add")) {
                if(args.length == 3) {
                    if(Tournament.getTournamentActive()) {
                        ProxiedPlayer player1 = Events.instance.getProxy().getPlayer(args[1]);
                        ProxiedPlayer player2 = Events.instance.getProxy().getPlayer(args[2]);

                        if(player1 == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[1] + " could not be found. Did you spell it correctly?").color(ChatColor.RED).create());
                        } else if(player2 == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " could not be found. Did you spell it correctly?").color(ChatColor.RED).create());
                        } else {
                            Tournament.getTournament().addTeam(player1, player2);
                            player.sendMessage(new ComponentBuilder("Success! Added team: " + Tournament.getTournament().getTeams()).color(ChatColor.RED).create());
                        }
                    } else {
                        player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams add <player1> <player2>").color(ChatColor.RED).create());
                }
            } else if(args[0].equalsIgnoreCase("remove")) {
                if(args.length == 2) {
                    if(Tournament.getTournamentActive()) {
                        ProxiedPlayer teamPlayer = Events.instance.getProxy().getPlayer(args[1]);
                        TournamentTeam team = Tournament.getTournament().removeTeam(teamPlayer);
                        if(team == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but a team with that player could not be found!").color(ChatColor.RED).create());
                        } else {
                            player.sendMessage(new ComponentBuilder("Success! Removed team: " + team.getTeamName()).color(ChatColor.RED).create());
                        }
                    } else {
                        player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams remove <player>").color(ChatColor.RED).create());
                }
            } else if(args[0].equals("list")) {
                if(args.length == 1) {
                    if(Tournament.getTournamentActive()) {
                        player.sendMessage(new ComponentBuilder("                   > Tournament Teams <").color(ChatColor.GOLD).bold(true).create());
                        for(TournamentTeam team : Tournament.getTournament().getTeams()) {
                            player.sendMessage(new ComponentBuilder(team.getTeamName()+ ": ").color(ChatColor.GRAY).append(team.getTotalPoints() + " points").color(ChatColor.DARK_GRAY).create());
                        }
                    } else {
                        player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams list").color(ChatColor.RED).create());
                }
            } else {
                player.sendMessage(new ComponentBuilder("Invalid Usage: /teams <add|remove|list>").color(ChatColor.RED).create());
            }
        } else {
            player.sendMessage(new ComponentBuilder("Invalid Usage: /teams <add|remove|list>").color(ChatColor.RED).create());
        }
    }

}