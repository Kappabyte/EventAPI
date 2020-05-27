package net.kappabyte.spigot.eventapi.backend;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.bungee.BungeeAPI;
import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;

public class BungeeBackendHandler implements BackendHandler {

    public static Game pastGame;
    public static Player pastHost;

    @Override
    public void createEvent(Game game, Player host) {
        pastGame = game;
        pastHost = host;
        if(game != null && host != null) {
            BungeeAPI.getInstance().sendData("create", host, host.getName(), game.name);
        }
    }

    @Override
    public void openEvent() {
        API.getPlugin().getLogger().info("opening event!");
        Player player = null;
        API.getPlugin().getLogger().info("finding player!");
        if(Bukkit.getOnlinePlayers().size() >= 1) {
            API.getPlugin().getLogger().info("found player!");
            player = Bukkit.getOnlinePlayers().stream().findAny().get();
            API.getPlugin().getLogger().info("got player!");
        }
        API.getPlugin().getLogger().info("checking player!");
        if(player != null) {
            API.getPlugin().getLogger().info("sending data!");
            BungeeAPI.getInstance().sendData("open", player, "", "");
        }
    }

    @Override
    public void closeEvent() {
        Player player = null;
        if(Bukkit.getOnlinePlayers().size() > 1) {
            player = Bukkit.getOnlinePlayers().stream().findFirst().get();
        }
        if(player != null) {
            BungeeAPI.getInstance().sendData("close", player, "", "");
        }
    }

    @Override
    public void broadcastEvent() {
        Player player = null;
        if(Bukkit.getOnlinePlayers().size() > 1) {
            player = Bukkit.getOnlinePlayers().stream().findFirst().get();
        }
        if(player != null) {
            BungeeAPI.getInstance().sendData("broadcast", player, "", "");
        }
    }

    @Override
    public void addPlayer(Player player) {
        BungeeAPI.getInstance().sendData("add", player, player.getName(), "");
    }

    @Override
    public void removePlayer(Player player) {
        BungeeAPI.getInstance().sendData("remove", player, player.getName(), "");
    }

    @Override
    public void joinEvent(Player player) {
        BungeeAPI.getInstance().sendData("join", player, player.getName(), "");
    }

    @Override
    public void leaveEvent(Player player) {
        BungeeAPI.getInstance().sendData("leave", player, player.getName(), "");
    }

    @Override
    public void setHost(Player player) {
        BungeeAPI.getInstance().sendData("sethost", player, player.getName(), "");
    }

    @Override
    public void getHost(Player player) {
        BungeeAPI.getInstance().sendData("getHost", player, player.getName(), "");
    }

    @Override
    public void endGame() {
        Player player = null;
        if(Bukkit.getOnlinePlayers().size() > 1) {
            player = Bukkit.getOnlinePlayers().stream().findFirst().get();
        }
        if(player != null) {
            BungeeAPI.getInstance().sendData("end", player, "", "");
        }
    }

    @Override
    public void endGame(String[] rankings) {
        String ranks = "";
        for(String player : rankings) {
            if(player != null) ranks += player + ".";
        }
        if(ranks.length() - 1 < 0) {
            endGame();
            return;
        }
        ranks.substring(0, ranks.length() - 1);
        BungeeAPI.getInstance().sendData("end", Bukkit.getOnlinePlayers().stream().findFirst().get(), "", ranks);
    }

    @Override
    public void endGameNoTP() {
        BungeeAPI.getInstance().sendData("end", Bukkit.getOnlinePlayers().stream().findFirst().get(), "notp", "");
    }

    @Override
    public void endGameNoTP(String[] rankings) {
        String ranks = "";
        for(String player : rankings) {
            if(player != null) ranks += player + ".";
        }
        if(ranks.length() - 1 < 0) {
            endGame();
            return;
        }
        ranks.substring(0, ranks.length() - 1);
        BungeeAPI.getInstance().sendData("end", Bukkit.getOnlinePlayers().stream().findFirst().get(), "notp", ranks);
    }
}