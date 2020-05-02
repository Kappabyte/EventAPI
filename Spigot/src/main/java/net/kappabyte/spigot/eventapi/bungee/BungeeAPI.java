package net.kappabyte.spigot.eventapi.bungee;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import net.kappabyte.spigot.eventapi.API;

public class BungeeAPI implements PluginMessageListener {

    public void sendData(String key, String player, String value) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(key);
        out.writeUTF(player);
        out.writeUTF(value);

        API.getPlugin().getServer().sendPluginMessage(API.getPlugin(), "BungeeCord", out.toByteArray()); // Send to Bungee
    }

    @Override
    public void onPluginMessageReceived(String string, Player p, byte[] bytes) {
        API.getPlugin().getLogger().info("Recieved message!");
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            String key = in.readUTF(); // Read the channel
            String player = in.readUTF(); // Read the message
            String value = in.readUTF(); // Read the message
            
            BungeeMessageHandler.handle(key, player, value);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static BungeeAPI instance;

    public static BungeeAPI getInstance() {
        if (instance == null) {
            instance = new BungeeAPI();
        }
        return instance;
    }
}