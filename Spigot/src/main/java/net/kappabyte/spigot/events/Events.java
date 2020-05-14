package net.kappabyte.spigot.events;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.backend.BungeeBackendHandler;
import net.kappabyte.spigot.eventapi.backend.SpigotBackendHandler;
import net.kappabyte.spigot.eventapi.game.Game;

public class Events extends JavaPlugin implements Listener {

    public static Events instance;

    public File pasteventsConfigFile;
    public FileConfiguration pasteventsConfig;

    @Override
    public void onEnable() {
        instance = this;

        API.setPlugin(this);

        saveDefaultConfig();

        if(getConfig().getString("backend").equalsIgnoreCase("bungee")) {
            API.handler = new BungeeBackendHandler();
        } else {
            API.handler = new SpigotBackendHandler();
        }

        createCustomConfig();
    }

    @Override
    public void onDisable() {
        if(Game.currentGame != null) {
            Game.currentGame.onGameEnd();
        }
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("joinevent")) {
            API.handler.joinEvent((Player) sender);
        }
        if(cmd.getName().equalsIgnoreCase("leaveevent")) {
            API.handler.leaveEvent((Player) sender);
        }
        if(cmd.getName().equalsIgnoreCase("eventapi")) {
            if(args[0].equals("reload")) {
                reloadConfig();
                createCustomConfig();
                if(getConfig().getString("backend").equalsIgnoreCase("bungee")) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&1&4Event &r&7Event API Reloaded (bungee backend)"));
                    API.handler = new BungeeBackendHandler();
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&1&4Event &r&7Event API Reloaded (spigot backend)"));
                    API.handler = new SpigotBackendHandler();
                }

                Game.currentGame.onGameEnd();
            }
        }
        return true;
    }

    private void createCustomConfig() {
        pasteventsConfigFile = new File(getDataFolder(), "pastevents.yml");
        if (!pasteventsConfigFile.exists()) {
            pasteventsConfigFile.getParentFile().mkdirs();
            saveResource("pastevents.yml", false);
         }

         pasteventsConfig = new YamlConfiguration();
        try {
            pasteventsConfig.load(pasteventsConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }
}