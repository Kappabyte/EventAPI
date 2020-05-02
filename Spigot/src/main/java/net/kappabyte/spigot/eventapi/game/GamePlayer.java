package net.kappabyte.spigot.eventapi.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class GamePlayer {

    public static ArrayList<String> toAdd = new ArrayList<String>();

    public GamePlayer(Player player) {
        bukkitPlayer = player;
        name = player.getName();
	}

	public void tpToStartingPosition() {
        bukkitPlayer.teleport(startingLocation);
	}

	public Location startingLocation;
    public Player bukkitPlayer;

    public int killCount = 0;

    public String name;
}