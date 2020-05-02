package net.kappabyte.bungee.events.API.tournament;

import net.kappabyte.bungee.events.API.BungeeAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class TournamentTeam {
    public ProxiedPlayer player1, player2;

    private int points = 0;

    public TournamentTeam(ProxiedPlayer player1, ProxiedPlayer player2) {
        this.player1 = player1;
        this.player2 = player2;
    }

    public void awardPoints(int amount) {
        if(points < 0) return;
        points += amount;

        BungeeAPI.sendTournamentMessage("points", getTeamName(), points + "");
    }

    public int getTotalPoints() {
        return points;
    }

    public String getTeamName() {
        String teamName = player1.getName() + " + " + player2.getName();
        return teamName;
    }
}