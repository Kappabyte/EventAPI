package net.kappabyte.spigot.eventapi.scoreboard;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.ScoreboardManager;

import net.kappabyte.spigot.eventapi.game.GamePlayer;

public class Scoreboard {
    public static ScoreboardManager manager = Bukkit.getScoreboardManager();
    static org.bukkit.scoreboard.Scoreboard board = manager.getNewScoreboard();
    Objective obj;

    String[] lines;
    
    public Scoreboard(String displayName) {
        if(board.getObjective("info") != null) {
            board.getObjective("info").unregister();
        }
        obj = board.registerNewObjective("info", "dummy", displayName);
        lines = new String[256];
    }

    public Scoreboard(String name, String displayName) {
        if(board.getObjective(name) != null) {
            board.getObjective(name).unregister();
        }
        obj = board.registerNewObjective("info", "dummy", displayName);
        lines = new String[256];
    }

    public void initBoard() {
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    public void addPlayers(ArrayList<GamePlayer> players) {
        for(int i = 0; i < players.size(); i++) {
            players.get(i).bukkitPlayer.setScoreboard(board);
        }
    }

    public void addPlayer(GamePlayer player) {
        player.bukkitPlayer.setScoreboard(board);
    }

	public void setSlotText(int slot, String text) {
        try {
            board.resetScores(lines[slot + 64]);
        } catch(Exception e) {

        }
        obj.getScore(text).setScore(slot);
        lines[slot + 64] = text;
    }

    public void setPlayerScore(Player p, int score) {
        obj.getScore(p.getName()).setScore(score);
    }
    
    public void removeSlotText(int slot) {
        try {
            board.resetScores(lines[slot + 64]);
        } catch(Exception e) {

        }
        lines[slot + 64] = null;
	}
}