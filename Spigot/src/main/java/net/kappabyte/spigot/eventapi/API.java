package net.kappabyte.spigot.eventapi;

import org.bukkit.plugin.java.JavaPlugin;

import net.kappabyte.spigot.eventapi.backend.BackendHandler;
import net.kappabyte.spigot.eventapi.bungee.BungeeAPI;

public class API {
    public static boolean bungeeMode = true;

    private static JavaPlugin plugin;

    public static BackendHandler handler;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(JavaPlugin plugin) {
        if(API.plugin == null) {
            API.plugin = plugin;

            plugin.getServer().getMessenger().registerOutgoingPluginChannel( plugin, "BungeeCord" ); // Register the outgoing channel, to Bungee
            plugin.getServer().getMessenger().registerIncomingPluginChannel( plugin, "events:evnt", BungeeAPI.getInstance() ); // Register the incoming channel, from Bungee
            plugin.getServer().getMessenger().registerIncomingPluginChannel( plugin, "events:api", BungeeAPI.getInstance() ); // Register the incoming channel, from Bungee
            plugin.getServer().getMessenger().registerIncomingPluginChannel( plugin, "events:tournament", BungeeAPI.getInstance() ); // Register the incoming channel, from Bungee
        }
    }
}