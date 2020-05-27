package net.kappabyte.spigot.tournament;

import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;

public class TournamentTeam {
    public OfflinePlayer player1, player2;
    public String teamName = "";

    private int points = 0;

    @SuppressWarnings("deprecation")
    public TournamentTeam(String teamName) {
        API.getPlugin().getLogger().info("Adding Team: " + teamName);
        String p1 = teamName.split(Pattern.quote(" + "))[0];
        String p2 = teamName.split(Pattern.quote(" + "))[1];
        this.player1 = Bukkit.getOfflinePlayer(p1);
        this.player2 = Bukkit.getOfflinePlayer(p2);

        this.teamName = teamName;
    }

    public void updatePoints(int amount) {
        points = amount;
    }

    public int getTotalPoints() {
        return points;
    }

    public String getTeamName() {
        return teamName;
    }
}