package net.kappabyte.spigot.tournament;

import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.bungee.BungeeAPI;

public class Tournament {
    public static void addTeam(Player player1, Player player2) {
        BungeeAPI.getInstance().sendData("addteam", player1.getName() + " + " + player2.getName(), "");
    }

    public static void removeTeam(Player player) {
        BungeeAPI.getInstance().sendData("removeteam", player.getName(), "");
    }
}