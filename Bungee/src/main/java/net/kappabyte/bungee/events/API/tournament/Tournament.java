package net.kappabyte.bungee.events.API.tournament;

import java.util.ArrayList;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Tournament {
    private static Tournament tournament;

    private static boolean isTournementActive = false;

    private ArrayList<TournamentTeam> teams = new ArrayList<TournamentTeam>();

    public static void createTournament() {
        tournament = new Tournament();
        isTournementActive = true;
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

    public void addTeam(TournamentTeam team) {
        teams.add(team);
    }

    public void addTeam(ProxiedPlayer player1, ProxiedPlayer player2) {
        teams.add(new TournamentTeam(player1, player2));
    }

    public void removeTeam(TournamentTeam team) {
        teams.remove(team);
    }

    public void removeTeam(int index) {
        teams.remove(index);
    }

    public void removeTeam(ProxiedPlayer player) {
        for(TournamentTeam team : teams) {
            if(team.player1.getUniqueId().equals(player.getUniqueId())) {
                teams.remove(team);
            } else if(team.player2.getUniqueId().equals(player.getUniqueId())) {
                teams.remove(team);
            }
        }
    }

    public void giveTeamPoints(TournamentTeam team, int amount) {
        if(teams.contains(team)) {
            team.awardPoints(amount);
        }
    }

    public void giveTeamPoints(int index, int amount) {
        teams.get(index).awardPoints(amount);
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