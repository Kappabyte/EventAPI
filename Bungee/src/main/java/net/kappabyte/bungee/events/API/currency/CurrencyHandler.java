package net.kappabyte.bungee.events.API.currency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import net.kappabyte.bungee.events.Events;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class CurrencyHandler implements Runnable {

    private static CurrencyHandler instance;

    private HashMap<String, Integer> playerBalances = new HashMap<String, Integer>();

    private Configuration balanceConfig;

    public CurrencyHandler() {
        reload();
        loadBalances();
    }

    public static CurrencyHandler getInstance() {
        if(instance == null) {
            instance = new CurrencyHandler();
        }
        return instance;
    }

    public int updateCurrency(String player, int amount) {
        int balance;
        if(playerBalances.containsKey(player)) {
            balance = playerBalances.get(player);
        } else {
            balance = 0;
            playerBalances.put(player, balance);
        }
        balance += amount;
        playerBalances.replace(player, balance);

        return balance;
    }

    private void reload() {
        try {
            balanceConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(Events.instance.getDataFolder(), "balance.yml"));
        } catch (IOException e) {
            createConfig();
        }
    }

    private void saveAll() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(balanceConfig, new File(Events.instance.getDataFolder(), "balance.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createConfig() {
        File configFile = new File(Events.instance.getDataFolder(), "balance.yml");
        try {
            Events.instance.getDataFolder().mkdirs();
            if (configFile.createNewFile()) {
                FileWriter myWriter = new FileWriter(configFile);
                myWriter.write("# This file stores player balances \n");
                myWriter.close();

                reload();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBalances() {
        for(String key : balanceConfig.getKeys()) {
            playerBalances.put(key, balanceConfig.getInt(key));
        }
    }

    @Override
    public void run() {
        saveAll();
        reload();
    }
}