package net.kappabyte.bungee.events.API;

import net.kappabyte.bungee.events.Events;
import net.kappabyte.bungee.events.API.currency.CurrencyHandler;
import net.kappabyte.bungee.events.API.tournament.Tournament;
import net.kappabyte.bungee.events.Config.ConfigManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeMessageHandler {

    public static void handleMessage(String key, String value, String player) {
        ProxiedPlayer p = Events.instance.getProxy().getPlayer(player);

        Events.instance.getLogger().info("Handling message with key: " + key);

        switch(key) {
            case "create":
                EventSystemAPI.createEvent(value, p);
                break;
            case "open":
                EventSystemAPI.openEvent();
                break;
            case "close":
                EventSystemAPI.closeEvent();
                break;
            case "broadcast":
                EventSystemAPI.broadcastEvent();
            case "add":
                EventSystemAPI.addPlayer(p);
                break;
            case "remove":
                EventSystemAPI.removePlayer(p);
                break;
            case "join":
                EventSystemAPI.joinEvent(p);
                break;
            case "leave":
                EventSystemAPI.leaveEvent(p);
                break;
            case "sethost":
                EventSystemAPI.setHost(p);
                break;
            case "gethost":
                EventSystemAPI.getHost(p);
                break;
            case "getplayer":
                EventSystemAPI.getPlayer(p, value);
                break;
            case "end":
                if(value != null) {
                    String[] leaderboard = value.split("\\.");
                    if(leaderboard.length >= 1 && leaderboard[0] != null) {
                        EventSystemAPI.setFirstPlace(leaderboard[0]);
                        if(Tournament.getTournamentActive()) {
                            Tournament.getTournament().giveTeamPoints(Events.instance.getProxy().getPlayer(leaderboard[0]), 3);
                        }
                    }
                    if(leaderboard.length >= 2 && leaderboard[1] != null) {
                        EventSystemAPI.setSecondPlace(leaderboard[1]);
                        if(Tournament.getTournamentActive()) {
                            Tournament.getTournament().giveTeamPoints(Events.instance.getProxy().getPlayer(leaderboard[1]), 2);
                        }
                    }
                    if(leaderboard.length >= 3 && leaderboard[2] != null) {
                        EventSystemAPI.setThirdPlace(leaderboard[2]);
                        if(Tournament.getTournamentActive()) {
                            Tournament.getTournament().giveTeamPoints(Events.instance.getProxy().getPlayer(leaderboard[2]), 1);
                        }
                    }
                }
                ConfigManager.saveAll();
                EventSystemAPI.endGame();
                break;
            case "eventhistory":
                EventSystemAPI.getPastEvents(player);
                break;
            case "updateplayerbalance":
                BungeeAPI.sendMessage("updateplayerbalance", player, CurrencyHandler.getInstance().updateCurrency(player, Integer.parseInt(value)) + "");
                break;
            case "addteam":
                if(!Tournament.getTournamentActive()) {
                    Tournament.createTournament();
                }
                Tournament.getTournament().addTeam(Events.instance.getProxy().getPlayer(player.split(" + ")[0]), Events.instance.getProxy().getPlayer(player.split(" + ")[1]));
                break;
            case "removeteam":
                if(!Tournament.getTournamentActive()) {
                    Tournament.createTournament();
                }
                Tournament.getTournament().addTeam(Events.instance.getProxy().getPlayer(player.split(" + ")[0]), Events.instance.getProxy().getPlayer(player.split(" + ")[1]));
                break;
        }
    }
}