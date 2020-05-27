package net.kappabyte.spigot.eventapi.backend;

import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;

public interface BackendHandler {
    public void createEvent(Game game, Player host);

    public void openEvent();
    public void closeEvent();

    public void broadcastEvent();

    public void addPlayer(Player player);
    public void removePlayer(Player player);
    public void joinEvent(Player player);
    public void leaveEvent(Player player);

    public void setHost(Player player);
    public void getHost(Player player);

    public void endGame();
    public void endGame(String[] rankings);
    public void endGameNoTP();
    public void endGameNoTP(String[] rankings);
}