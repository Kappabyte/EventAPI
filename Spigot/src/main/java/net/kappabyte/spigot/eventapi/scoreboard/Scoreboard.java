package net.kappabyte.spigot.eventapi.scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.game.GamePlayer;
import net.md_5.bungee.api.ChatColor;

public class Scoreboard {
    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
    static org.bukkit.scoreboard.Scoreboard blank = manager.getNewScoreboard();
    Objective obj;

    public Scoreboard(String displayName) {
        if (board.getObjective("info") != null) {
            board.getObjective("info").unregister();
        }
        obj = board.registerNewObjective("info", "dummy", displayName);
    }

    public Scoreboard(String name, String displayName) {
        if (board.getObjective(name) != null) {
            board.getObjective(name).unregister();
        }
        obj = board.registerNewObjective(name, "dummy", displayName);
    }

    public void initBoard() {
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addPlayers(ArrayList<GamePlayer> players) {
        for (int i = 0; i < players.size(); i++) {
            players.get(i).bukkitPlayer.setScoreboard(board);
        }
    }

    public static void removePlayer(Player player) {
        player.setScoreboard(blank);
    }

    public void addPlayer(GamePlayer player) {
        player.bukkitPlayer.setScoreboard(board);
    }

    public void addPlayer(Player player) {
        player.setScoreboard(board);
    }

    public void setSlotText(int slot, String text) {
        Team team;
        if (board.getTeam("slot" + slot) == null) {
            team = board.registerNewTeam("slot" + slot);
            team.addEntry(getEntryName(slot));
        } else {
            team = board.getTeam("slot" + slot);
        }
        team.setPrefix(text);

        obj.getScore(getEntryName(slot)).setScore(slot);
    }

    private String getEntryName(int slot) {
        int temp = slot;
        if (temp < 0) {
            temp *= -1;
            temp += 1000;
        }
        String result = "";

        do {
            result += "&" + (temp % 10);
            temp /= 10;
        } while (temp > 0);

        result += "&r";
        return ChatColor.translateAlternateColorCodes('&', result);
    }

    public void setRawScore(String text, Integer score) {
        if (score == null) {
            board.resetScores(text);
        } else {
            obj.getScore(text).setScore(score);
        }
    }

    public void setPlayerScore(Player p, int score) {
        obj.getScore(p.getName()).setScore(score);
    }

    public void removeSlotText(int slot) {
        Team team = board.getTeam("slot" + slot);
        if (team != null) {
            team.unregister();
        }
        board.resetScores(getEntryName(slot));
    }

    @Override
    public Object clone() {
        try {
            return (Scoreboard) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}