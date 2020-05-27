package net.kappabyte.bungee.events.API;

import java.util.Date;
import java.util.HashMap;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;



public class Event {
    public Object game;
    public String name;
    public ProxiedPlayer host;

    public boolean open = false;

    public HashMap<String, ProxiedPlayer> players;
    public HashMap<String, ProxiedPlayer> spectators;

    public HashMap<ProxiedPlayer, ServerInfo> playerOriginalPosition;

    public Date eventStartTime;

    public Event(String name, ProxiedPlayer host) {
        players = new HashMap<String, ProxiedPlayer>();
        spectators = new HashMap<String, ProxiedPlayer>();
        playerOriginalPosition = new HashMap<ProxiedPlayer, ServerInfo>();

        this.name = name;
        this.host = host;

        eventStartTime = new Date();
    }

}