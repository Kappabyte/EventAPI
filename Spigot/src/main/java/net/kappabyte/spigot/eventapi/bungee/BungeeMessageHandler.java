package net.kappabyte.spigot.eventapi.bungee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;

public class BungeeMessageHandler {
    public static void handle(String key, String player, String value) {
        Player p = Bukkit.getServer().getPlayer(player);

        API.getPlugin().getLogger().info("Handling Message: \nKey: " + key + "\nPlayer: " + player + "\nValue: " + value);

        switch(key) {
            case "add":
                if(p != null) {
                    Game.currentGame.addPlayer(p);
                } else {
                    GamePlayer.toAdd.add(player);
                }
                break;
            case "remove":
                if(p != null) {
                    Game.currentGame.removePlayer(p);
                }
        }
    }
}