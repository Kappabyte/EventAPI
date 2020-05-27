package net.kappabyte.spigot.tournament;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import net.kappabyte.spigot.eventapi.PlayerInv;
import net.kappabyte.spigot.eventapi.bungee.BungeeAPI;
import net.kappabyte.spigot.eventapi.scoreboard.Scoreboard;
import net.md_5.bungee.api.ChatColor;

public class Tournament {
    private static Tournament tournament;

    private static boolean isTournementActive = false;

    private Player host;

    private Scoreboard scoreboard;

    private ArrayList<TournamentTeam> teams = new ArrayList<TournamentTeam>();
    private ArrayList<Player> spectators = new ArrayList<Player>();

    public static void createTournament(Player player) {
        tournament = new Tournament();
        isTournementActive = true;

        tournament.host = player;
    }

    private Tournament() {
        scoreboard = new Scoreboard(ChatColor.GOLD + "" + ChatColor.BOLD + "Minecraft" + ChatColor.YELLOW + "" + ChatColor.BOLD + "Sundays");
        scoreboard.initBoard();
        scoreboard.setSlotText(-1, ChatColor.DARK_AQUA + "Minecraft" + ChatColor.AQUA + "Epic" + ChatColor.DARK_GRAY + ".tk");
    }

    public static void endTournament() {
        tournament = null;
        isTournementActive = false;
    }

    public static Tournament getTournament() {
        return tournament;
    }
    
    public static boolean getTournamentActive() {
        return isTournementActive;
    }

    public ArrayList<TournamentTeam> getTeams() {
        return teams;
    }

    public Player getTeamMember(Player player) {
        for(TournamentTeam team : teams) {
            if(team.player1.getName().equals(player.getName())) {
                if(team.player2.isOnline()) {
                    return team.player2.getPlayer();
                }
            }
            if(team.player2.getName().equals(player.getName())) {
                if(team.player1.isOnline()) {
                    return team.player1.getPlayer();
                }
            }
        }
        return null;
     }

    public void addSpectator(Player player) {
        if(!spectators.contains(player)) {
            spectators.add(player);
        }
    }

    public void removeSpectator(Player player) {
        if(spectators.contains(player)) {
            spectators.remove(player);
        }
    }

    public void sendAllPlayersToHub() {
        for(TournamentTeam team : teams) {
            Player player1;
            Player player2;
            if(team.player1.isOnline()) {
                player1 = team.player1.getPlayer();

                player1.teleport(new Location(Bukkit.getWorld("main"), -406.5, 13, 13.5));
                PlayerInv.clearInventory(player1.getInventory());
                player1.setGameMode(GameMode.ADVENTURE);
                player1.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20000000, 255, true));
                player1.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20000000, 255, true));
                scoreboard.addPlayer(player1);
            }
            if(team.player2.isOnline()) {
                player2 = team.player2.getPlayer();
                player2.teleport(new Location(Bukkit.getWorld("main"), -406.5, 13, 13.5));

                PlayerInv.clearInventory(player2.getInventory());

                player2.setGameMode(GameMode.ADVENTURE);

                player2.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20000000, 255, true));
                player2.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20000000, 255, true));

                scoreboard.addPlayer(player2);
            }
        }
        for(Player player : spectators) {
            player.teleport(new Location(Bukkit.getWorld("main"), -406.5, 13, 13.5));

            PlayerInv.clearInventory(player.getInventory());

            player.setGameMode(GameMode.ADVENTURE);

            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20000000, 255, true));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20000000, 255, true));

            scoreboard.addPlayer(player);
        }

        Bukkit.getWorld("main").getBlockAt(-444, 20, 45).setType(Material.REDSTONE_BLOCK);
    }

    public TournamentTeam getTeamWithPlayer(Player player) {
        for(TournamentTeam team : teams) {
            if(team.player1.getName().equals(player.getName())) {
                return team;
            } else if(team.player2.getName().equals(player.getName())) {
                return team;
            }
        }
        return null;
    }

    public TournamentTeam addTeam(String teamName) {
        TournamentTeam team = new TournamentTeam(teamName);
        teams.add(team);
        scoreboard.setRawScore(ChatColor.GRAY + teamName, 0);
        return team;
    }

    public TournamentTeam removeTeam(String teamName) {
        for(TournamentTeam team : teams) {
            if(team.getTeamName().equals(teamName)) {
                teams.remove(team);
                scoreboard.setRawScore(teamName, null);
                if(team.player1.isOnline()) {
                    Scoreboard.removePlayer(team.player1.getPlayer());
                }
                if(team.player2.isOnline()) {
                    Scoreboard.removePlayer(team.player2.getPlayer());
                }
                return team;
            }
        }
        return null;
    }

    public void updateTeamPoints(String teamName, int amount) {
        for(TournamentTeam team : teams) {
            if(team.getTeamName().equals(teamName)) {
                scoreboard.setRawScore(ChatColor.GRAY + teamName, amount);
                team.updatePoints(amount);
            }
        }
    }
}