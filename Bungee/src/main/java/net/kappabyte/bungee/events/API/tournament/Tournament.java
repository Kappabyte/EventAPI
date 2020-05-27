package net.kappabyte.bungee.events.API.tournament;

import java.util.ArrayList;

import net.kappabyte.bungee.events.API.BungeeAPI;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Tournament {
    private static Tournament tournament;

    private static boolean isTournementActive = false;

    private ProxiedPlayer host;

    private ArrayList<TournamentTeam> teams = new ArrayList<TournamentTeam>();
    private ArrayList<ProxiedPlayer> spectators = new ArrayList<ProxiedPlayer>();

    public static void createTournament(ProxiedPlayer player) {
        tournament = new Tournament();
        isTournementActive = true;

        tournament.host = player;

        tournament.spectators.add(player);

        BungeeAPI.sendTournamentMessage("create", player.getName(), "");
    }

    public void sendAllData() {
        if (isTournementActive) {
            BungeeAPI.sendTournamentMessage("create", host.getName(), "");
            sleep();
            for(TournamentTeam team : teams) {
                BungeeAPI.sendTournamentMessage("addTeam", team.player1.getName(), team.player2.getName());
                sleep();

                BungeeAPI.sendTournamentMessage("points", team.getTeamName(), team.getTotalPoints() + "");
                sleep();
            }
        }
    }

    public boolean getPlayerIsSpectator(ProxiedPlayer player) {
        return spectators.contains(player);
    }

    public void addSpectator(ProxiedPlayer player) {
        if(!getPlayerIsSpectator(player)) {
            BungeeAPI.sendTournamentMessage("addSpec", player.getName(), "");
            spectators.add(player);
        }
    }

    public void removeSpectator(ProxiedPlayer player) {
        if(getPlayerIsSpectator(player)) {
            BungeeAPI.sendTournamentMessage("removeSpec", player.getName(), "");
            spectators.remove(player);
        }
    }

    public ArrayList<ProxiedPlayer> getSpectators() {
        return spectators;
    }

    public boolean getPlayerIsInTeam(ProxiedPlayer player) {
        for(TournamentTeam team : teams) {
            if(team.player1.getName().equals(player.getName())) {
                return true;
            }
            if(team.player2.getName().equals(player.getName())) {
                return true;
            }
        }

        return false;
    }

    private void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {}
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
        }
    }

    public void giveTeamPoints(int index, int amount) {
        teams.get(index).awardPoints(amount);
    }
    
    public void giveTeamPoints(String teamName, int amount) {
        for(TournamentTeam team : teams) {
            if(team.getTeamName().equals(teamName)) {
                team.awardPoints(amount);
            }
        }
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