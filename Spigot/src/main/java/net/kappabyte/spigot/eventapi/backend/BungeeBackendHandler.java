package net.kappabyte.spigot.eventapi.backend;

import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.bungee.BungeeAPI;
import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;

public class BungeeBackendHandler implements BackendHandler {

    @Override
    public void createEvent(Game game, Player host) {
        BungeeAPI.getInstance().sendData("create", host.getName(), game.name);
    }

    @Override
    public void openEvent() {
        BungeeAPI.getInstance().sendData("open", "", "");
    }

    @Override
    public void closeEvent() {
        BungeeAPI.getInstance().sendData("close", "", "");
    }

    @Override
    public void broadcastEvent() {
        BungeeAPI.getInstance().sendData("broadcast", "", "");
    }

    @Override
    public void addPlayer(Player player) {
        BungeeAPI.getInstance().sendData("add", player.getName(), "");
    }

    @Override
    public void removePlayer(Player player) {
        BungeeAPI.getInstance().sendData("remove", player.getName(), "");
    }

    @Override
    public void joinEvent(Player player) {
        BungeeAPI.getInstance().sendData("join", player.getName(), "");
    }

    @Override
    public void leaveEvent(Player player) {
        BungeeAPI.getInstance().sendData("leave", player.getName(), "");
    }

    @Override
    public void setHost(Player player) {
        BungeeAPI.getInstance().sendData("sethost", player.getName(), "");
    }

    @Override
    public void getHost(Player player) {
        BungeeAPI.getInstance().sendData("getHost", player.getName(), "");
    }

    @Override
    public void endGame() {
        BungeeAPI.getInstance().sendData("end", "", "");
    }

    @Override
    public void endGame(GamePlayer[] rankings) {
        String ranks = "";
        for(GamePlayer player : rankings) {
            ranks += player.name + ".";
        }
        if(ranks.length() - 1 < 0) {
            endGame();
            return;
        }
        ranks.substring(0, ranks.length() - 1);
        BungeeAPI.getInstance().sendData("end", "", ranks);
    }

}