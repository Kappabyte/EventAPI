package net.kappabyte.spigot.eventapi;

import org.bukkit.plugin.java.JavaPlugin;

import net.kappabyte.spigot.eventapi.backend.BackendHandler;

public class API {
    public static boolean bungeeMode = true;

    private static JavaPlugin plugin;

    public static BackendHandler handler;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public static void setPlugin(JavaPlugin plugin) {
        API.plugin = plugin;
    }
}