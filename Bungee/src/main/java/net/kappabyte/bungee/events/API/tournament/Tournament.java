package net.kappabyte.bungee.events.API.tournament;

import java.util.ArrayList;

import net.kappabyte.bungee.events.API.BungeeAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Tournament {
    private static Tournament tournament;

    private static boolean isTournementActive = false;

    private ProxiedPlayer host;

    private ArrayList<TournamentTeam> teams = new ArrayList<TournamentTeam>();

    public static void createTournament(ProxiedPlayer player) {
        tournament = new Tournament();
        isTournementActive = true;

        tournament.host = player;

        BungeeAPI.sendTournamentMessage("create", player.getName(), "");
    }

    public static void endTournament() {
        tournament = null;
        isTournementActive = false;

        BungeeAPI.sendTournamentMessage("end", "", "");
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

    public TournamentTeam addTeam(TournamentTeam team) {
        teams.add(team);
        BungeeAPI.sendTournamentMessage("addTeam", team.player1.getName(), team.player2.getName());
        return team;
    }

    public TournamentTeam addTeam(ProxiedPlayer player1, ProxiedPlayer player2) {
        TournamentTeam team = new TournamentTeam(player1, player2);
        BungeeAPI.sendTournamentMessage("addTeam", player1.getName(), player2.getName());
        teams.add(team);
        return team;
    }

    public TournamentTeam removeTeam(TournamentTeam team) {
        teams.remove(team);
        BungeeAPI.sendTournamentMessage("removeTeam", team.player1.getName(), team.player2.getName());
        return team;
    }

    public TournamentTeam removeTeam(int index) {
        TournamentTeam team = teams.get(index);
        BungeeAPI.sendTournamentMessage("removeTeam", team.player1.getName(), team.player2.getName());
        teams.remove(index);
        return team;
    }

    public TournamentTeam removeTeam(ProxiedPlayer player) {
        for(TournamentTeam team : teams) {
            if(team.player1.getUniqueId().equals(player.getUniqueId())) {
                teams.remove(team);
                BungeeAPI.sendTournamentMessage("removeTeam", team.player1.getName(), team.player2.getName());
                return team;
            } else if(team.player2.getUniqueId().equals(player.getUniqueId())) {
                teams.remove(team);
                BungeeAPI.sendTournamentMessage("removeTeam", team.player1.getName(), team.player2.getName());
                return team;
            }
        }
        return null;
    }

    public void giveTeamPoints(TournamentTeam team, int amount) {
        if(teams.contains(team)) {
            team.awardPoints(amount);
            BungeeAPI.sendTournamentMessage("points", team.player1.getName(), team.getTotalPoints() + "");
        }
    }

    public void giveTeamPoints(int index, int amount) {
        teams.get(index).awardPoints(amount);
        BungeeAPI.sendTournamentMessage("points", teams.get(index).player1.getName(), teams.get(index).getTotalPoints() + "");
    }

    public void giveTeamPoints(ProxiedPlayer player, int amount) {
        for(TournamentTeam team : teams) {
            if(team.player1.getUniqueId().equals(player.getUniqueId())) {
                team.awardPoints(amount);
            } else if(team.player2.getUniqueId().equals(player.getUniqueId())) {
                team.awardPoints(amount);
            }
        }
    }
}