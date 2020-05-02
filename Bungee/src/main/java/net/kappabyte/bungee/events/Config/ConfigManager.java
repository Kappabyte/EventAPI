package net.kappabyte.bungee.events.Config;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import net.kappabyte.bungee.events.Events;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigManager {

    private static Configuration configuration;
    public static Configuration eventStorage;

    public static void reload() {
        try {
            configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Events.instance.getDataFolder(), "config.yml"));
        } catch (IOException e) {
            createConfig();
        }
        
        try {
            eventStorage = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Events.instance.getDataFolder(), "pastevents.yml"));
        } catch (IOException e) {
            createStorage();
        }
    }

    public static void saveAll() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, new File(Events.instance.getDataFolder(), "config.yml"));
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(eventStorage, new File(Events.instance.getDataFolder(), "pastevents.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getEventServer() {
        if(configuration.contains("event-server-name")) {
            return configuration.getString("event-server-name");
        } else {
            return "event";
        }
    }

    private static void createConfig() {
        File configFile = new File(Events.instance.getDataFolder(), "config.yml");
        try {
            Events.instance.getDataFolder().mkdirs();
            if (configFile.createNewFile()) {
                FileWriter myWriter = new FileWriter(configFile);
                myWriter.write("# Event API configuration\nevent-server-name: \"event\"");
                myWriter.close();

                reload();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createStorage() {
        File configFile = new File(Events.instance.getDataFolder(), "pastevents.yml");
        try {
            Events.instance.getDataFolder().mkdirs();
            if (configFile.createNewFile()) {
                FileWriter myWriter = new FileWriter(configFile);
                myWriter.write("# Event API past event storage");
                myWriter.close();

                reload();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}