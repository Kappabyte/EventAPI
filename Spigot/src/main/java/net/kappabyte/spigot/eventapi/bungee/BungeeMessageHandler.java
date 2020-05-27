package net.kappabyte.spigot.eventapi.bungee;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.backend.BungeeBackendHandler;
import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.eventapi.game.GamePlayer;
import net.kappabyte.spigot.tournament.Tournament;
import net.minecraft.server.v1_15_R1.AdvancementRewards.b;

public class BungeeMessageHandler {
    public static void handle(String key, String player, String value) {
        Player p = Bukkit.getServer().getPlayer(player);

        API.getPlugin().getLogger()
                .info("Handling Message: \nKey: " + key + "\nPlayer: " + player + "\nValue: " + value);

        switch (key) {
            case "add":
                if (p != null) { // Player is already connected to the server
                    Game.currentGame.oldPlayerLocations.put(p, p.getLocation());
                    Game.currentGame.addPlayer(p);
                } else { // Player is being sent to the server
                    GamePlayer.toAdd.add(player);
                }
                break;
            case "remove":
                if (p != null) {
                    Game.currentGame.removePlayer(p, Game.RemoveReason.LEFT_GAME);
                }
                break;
            case "adds":
                if (p != null) { // Player is already connected to the server
                    Game.currentGame.oldPlayerLocations.put(p, p.getLocation());
                    Game.currentGame.addSpectator(p);
                } else { // Player is being sent to the server
                    GamePlayer.SpectatorsToAdd.add(player);
                }
                break;
            case "removes":
                if (p != null) {
                    Game.currentGame.removeSpectator(p);
                }
                break;
            case "create":
                Tournament.createTournament(p);
                break;
            case "end":
                Tournament.endTournament();
                break;
            case "addTeam":
                Tournament.getTournament().addTeam(player + " + " + value);
                break;
            case "removeTeam":
                Tournament.getTournament().addTeam(player + " + " + value);
                break;
            case "addSpec":
                Player addSpec = Bukkit.getPlayer(player);
                if (addSpec != null) {
                    Tournament.getTournament().addSpectator(addSpec);
                }
                break;
            case "removeSpec":
                Player removeSpec = Bukkit.getPlayer(player);
                if (removeSpec != null) {
                    Tournament.getTournament().removeSpectator(removeSpec);
                }
                break;
            case "points":
                Tournament.getTournament().updateTeamPoints(player, Integer.parseInt(value));
                break;
            case "start":
                if (Game.currentGame != null) {
                    Game.currentGame.startGame();
                }
                break;
            case "failopen":
                API.handler.createEvent(BungeeBackendHandler.pastGame, BungeeBackendHandler.pastHost);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
                API.handler.openEvent();
        }
    }
}