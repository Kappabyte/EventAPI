package net.kappabyte.spigot.events.API;

import java.util.Date;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Event {
    public Object game;
    public String name;
    public Player host;

    public boolean open = false;

    public HashMap<String, Player> players;

    public HashMap<Player, Location> playerOriginalPosition;

    public Date eventStartTime;

    public Event(Object game, String name, Player host) {
        this.game = game;
        this.name = name;
        this.host = host;

        players = new HashMap<String, Player>();
        playerOriginalPosition = new HashMap<Player, Location>();

        eventStartTime = new Date();
    }

}