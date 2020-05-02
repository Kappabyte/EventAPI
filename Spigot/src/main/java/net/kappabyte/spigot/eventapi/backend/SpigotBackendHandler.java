package net.kappabyte.spigot.eventapi.backend;

import java.io.IOException;

import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;
import net.kappabyte.spigot.events.Events;
import net.kappabyte.spigot.events.API.EventSystemAPI;

public class SpigotBackendHandler implements BackendHandler {

    @Override
    public void createEvent(Game game, Player host) {
        EventSystemAPI.createEvent(game.name, game, host);
    }

    @Override
    public void openEvent() {
        EventSystemAPI.openEvent();
    }

    @Override
    public void closeEvent() {
        EventSystemAPI.closeEvent();
    }

    @Override
    public void broadcastEvent() {
        EventSystemAPI.broadcastEvent();
    }

    @Override
    public void addPlayer(Player player) {
        EventSystemAPI.addPlayer(player);
    }

    @Override
    public void removePlayer(Player player) {
        EventSystemAPI.removePlayer(player);
    }

    @Override
    public void joinEvent(Player player) {
        EventSystemAPI.joinEvent(player);
    }

    @Override
    public void leaveEvent(Player player) {
        EventSystemAPI.leaveEvent(player);
    }

    @Override
    public void setHost(Player player) {
        EventSystemAPI.setHost(player);
    }

    @Override
    public void getHost(Player player) {
        EventSystemAPI.getHost(player);
    }

    @Override
    public void endGame() {
        EventSystemAPI.endGame();
    }

    @Override
    public void endGame(GamePlayer[] rankings) {
        if(rankings != null) {
            if(rankings.length >= 1 && rankings[0] != null) {
                EventSystemAPI.setFirstPlace(rankings[0].name);
            }
            if(rankings.length >= 2 && rankings[1] != null) {
                EventSystemAPI.setFirstPlace(rankings[1].name);
            }
            if(rankings.length >= 3 && rankings[2] != null) {
                EventSystemAPI.setFirstPlace(rankings[2].name);
            }
        }
        try {
            Events.instance.pasteventsConfig.save(Events.instance.pasteventsConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EventSystemAPI.endGame();
    }

}