package net.kappabyte.bungee.events;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import net.kappabyte.bungee.events.API.BungeeMessageHandler;
import net.kappabyte.bungee.events.API.currency.CurrencyHandler;
import net.kappabyte.bungee.events.Config.ConfigManager;
import net.kappabyte.bungee.events.commands.JoinEventCmd;
import net.kappabyte.bungee.events.commands.LeaveEventCmd;
import net.kappabyte.bungee.events.commands.ReloadCmd;
import net.kappabyte.bungee.events.commands.TeamCmd;
import net.kappabyte.bungee.events.commands.TournamentCmd;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Events extends Plugin implements Listener {
    public static Events instance;
    @Override
    public void onEnable() {
        instance = this;

        getProxy().getPluginManager().registerCommand(this, new JoinEventCmd());
        getProxy().getPluginManager().registerCommand(this, new LeaveEventCmd());
        getProxy().getPluginManager().registerCommand(this, new ReloadCmd());
        getProxy().getPluginManager().registerCommand(this, new TournamentCmd());
        getProxy().getPluginManager().registerCommand(this, new TeamCmd());

        getProxy().getScheduler().schedule(this, CurrencyHandler.getInstance(), 0, 5, TimeUnit.MINUTES);

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel("events:return");
        getProxy().registerChannel("events:evnt");
        getProxy().registerChannel("events:tournament");

        ConfigManager.reload();
    }

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
        getLogger().info("Got event: " + event.getTag());
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
            try {
                String key = in.readUTF();
                String player = in.readUTF();
                String value = in.readUTF();
                
                BungeeMessageHandler.handleMessage(key, value, player);
            } catch (IOException ignored) {
                getLogger().log(Level.WARNING, ignored.getStackTrace().toString());
             }
        }
    }
}