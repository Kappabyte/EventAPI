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
import net.md_5.bungee.api.plugin.TabExecutor;

public class TeamCmd extends Command {

    public static boolean canJoinEvent = false;

    public TeamCmd() {
        super("teams", "tournament.manage", "team", "t", "t");
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
                            if(Tournament.getTournament().getPlayerIsInTeam(player1)) {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + player1.getName() + " is already in a team!").color(ChatColor.RED).create());
                                return;
                            }
                            if(Tournament.getTournament().getPlayerIsInTeam(player2)) {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + player2.getName() + " is already in a team!").color(ChatColor.RED).create());
                                return;
                            }
                            if(Tournament.getTournament().getPlayerIsSpectator(player1)) {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + player1.getName() + " is currently a spectator").color(ChatColor.RED).create());
                                return;
                            }
                            if(Tournament.getTournament().getPlayerIsSpectator(player2)) {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + player2.getName() + " is currently a spectator!").color(ChatColor.RED).create());
                                return;
                            }

                            TournamentTeam team = Tournament.getTournament().addTeam(player1, player2);
                            team.player1.sendMessage(new ComponentBuilder("You are now teaming with " + team.player2.getName()).color(ChatColor.GRAY).create());
                            team.player2.sendMessage(new ComponentBuilder("You are now teaming with " + team.player1.getName()).color(ChatColor.GRAY).create());
                            player.sendMessage(new ComponentBuilder("Success! Added team: " + team.getTeamName()).color(ChatColor.GREEN).create());
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

                        if(teamPlayer == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but a team with that player could not be found!").color(ChatColor.RED).create());
                            return;
                        }

                        if(!Tournament.getTournament().getPlayerIsInTeam(teamPlayer)) {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[1] + " is not in a team!").color(ChatColor.RED).create());
                            return;
                        }

                        TournamentTeam team = Tournament.getTournament().removeTeam(teamPlayer);
                        if(team == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but a team with that player could not be found!").color(ChatColor.RED).create());
                        } else {
                            team.player1.sendMessage(new ComponentBuilder("You are no longer teaming with " + team.player2.getName() + "\nYou have been removed from the tournament").color(ChatColor.GRAY).create());
                            team.player2.sendMessage(new ComponentBuilder("You are no longer teaming with " + team.player1.getName() + "\nYou have been removed from the tournament").color(ChatColor.GRAY).create());
                            player.sendMessage(new ComponentBuilder("Success! Removed team: " + team.getTeamName()).color(ChatColor.GREEN).create());
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
                        player.sendMessage(new ComponentBuilder("                   > Tournament Teams <").color(ChatColor.WHITE).bold(true).create());
                        for(TournamentTeam team : Tournament.getTournament().getTeams()) {
                            player.sendMessage(new ComponentBuilder(team.getTeamName()+ ": ").color(ChatColor.GRAY).append(team.getTotalPoints() + " points").color(ChatColor.DARK_GRAY).create());
                        }
                    } else {
                        player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams list").color(ChatColor.RED).create());
                }
            } else if(args[0].equals("give")) {
                if(args.length == 3) {
                    if(Tournament.getTournamentActive()) {
                        ProxiedPlayer teamPlayer = Events.instance.getProxy().getPlayer(args[1]);

                        if(teamPlayer == null) {
                            player.sendMessage(new ComponentBuilder("Sorry, but a team with that player could not be found!").color(ChatColor.RED).create());
                            return;
                        }

                        if(!Tournament.getTournament().getPlayerIsInTeam(teamPlayer)) {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[1] + " is not in a team!").color(ChatColor.RED).create());
                            return;
                        } else {
                            Tournament.getTournament().giveTeamPoints(teamPlayer, Integer.parseInt(args[2]));
                            player.sendMessage(new ComponentBuilder("Success! Gave team: " +  args[2] + " points").color(ChatColor.GREEN).create());
                        }
                    } else {
                        player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams remove <player>").color(ChatColor.RED).create());
                }
            } else if(args[0].equals("spectator")) {
                if(args.length >= 2) {
                    if(args[1].equalsIgnoreCase("add")) {
                        if(args.length != 3) {
                            player.sendMessage(new ComponentBuilder("Invalid Usage: /teams spectator add <player>").color(ChatColor.RED).create());
                            return;
                        }
                        if(!Tournament.getTournamentActive()) {
                            player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                            return;
                        }
                        if(Events.instance.getProxy().getPlayer(args[2]) != null) {
                            ProxiedPlayer p = Events.instance.getProxy().getPlayer(args[2]);
                                
                            if(!Tournament.getTournament().getPlayerIsSpectator(p)) {
                                if(!Tournament.getTournament().getPlayerIsInTeam(p)) {
                                    Tournament.getTournament().addSpectator(p);
                                    p.sendMessage(new ComponentBuilder("You are now spectating the tournament").color(ChatColor.GRAY).create());
                                    player.sendMessage(new ComponentBuilder("The player " + p.getName() + " is now a spectator!").color(ChatColor.GREEN).create());
                                } else {
                                    player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " is already in a team!").color(ChatColor.RED).create());
                                }
                            } else {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " is already a spectator!").color(ChatColor.RED).create());
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " could not be found. Did you spell it correctly?").color(ChatColor.RED).create());
                        }
                    } else if(args[1].equalsIgnoreCase("remove")) {
                        if(args.length != 3) {
                            player.sendMessage(new ComponentBuilder("Invalid Usage: /teams spectator remove <player>").color(ChatColor.RED).create());
                            return;
                        }
                        if(!Tournament.getTournamentActive()) {
                            player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                            return;
                        }
                        if(Events.instance.getProxy().getPlayer(args[2]) != null) {
                            ProxiedPlayer p = Events.instance.getProxy().getPlayer(args[2]);
                                
                            if(Tournament.getTournament().getPlayerIsSpectator(p)) {
                                Tournament.getTournament().removeSpectator(p);
                                p.sendMessage(new ComponentBuilder("You are no longer spectating the tournament\nYou have been removed from the tournament").color(ChatColor.GRAY).create());
                                player.sendMessage(new ComponentBuilder("The player" + p.getName() + " is no longer a spectator!").color(ChatColor.GREEN).create());
                            } else {
                                player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " is not a spectator!").color(ChatColor.RED).create());
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("Sorry, but the player " + args[2] + " could not be found. Did you spell it correctly?").color(ChatColor.RED).create());
                        }
                    } else if(args[1].equalsIgnoreCase("list")) {
                        if(args.length == 2) {
                            if(Tournament.getTournamentActive()) {
                                player.sendMessage(new ComponentBuilder("                > Tournament Spectators <").color(ChatColor.WHITE).bold(true).create());
                                for(ProxiedPlayer team : Tournament.getTournament().getSpectators()) {
                                    player.sendMessage(new ComponentBuilder(team.getName()).color(ChatColor.GRAY).create());
                                }
                            } else {
                                player.sendMessage(new ComponentBuilder("Sorry, but there is no active tournament!").color(ChatColor.RED).create());
                            }
                        } else {
                            player.sendMessage(new ComponentBuilder("Invalid Usage: /teams list").color(ChatColor.RED).create());
                        }
                    }
                } else {
                    player.sendMessage(new ComponentBuilder("Invalid Usage: /teams spectator <add|remove|list>").color(ChatColor.RED).create());
                }
            } else {
                player.sendMessage(new ComponentBuilder("Invalid Usage: /teams <add|remove|list|spectator>").color(ChatColor.RED).create());
            }
        } else {
            player.sendMessage(new ComponentBuilder("Invalid Usage: /teams <add|remove|list|spectator>").color(ChatColor.RED).create());
        }
    }

}