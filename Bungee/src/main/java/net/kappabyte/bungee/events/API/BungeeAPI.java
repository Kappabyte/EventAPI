package net.kappabyte.bungee.events.API;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

import net.kappabyte.bungee.events.Events;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeAPI {

    public static void addPlayer(ProxiedPlayer player) {
        sendToBukkit("events:api", "add", player.getName(), "");
    }
    public static void removePlayer(ProxiedPlayer player) {
        sendToBukkit("events:api", "remove", player.getName(), "");
    }
    public static void sendPastEvents(String jsonData, String player) {
        sendToBukkit("events:api", "eventhistory", player, jsonData);
    }

    public static void sendMessage(String key, String player, String value) {
        sendToBukkit("events:api", key, player, value);
    }

    public static void sendTournamentMessage(String key, String player, String value) {
        sendToBukkit("events:tournament", key, player, value);
    }

    private static void sendToBukkit(String realChannel, String key, String player, String value) {
        Events.instance.getLogger().info("Sending " + key + " to bukkit");
        Map<String, ServerInfo> servers = Events.instance.getProxy().getServers();
        ArrayList<String> sent = new ArrayList<String>();
        for (Map.Entry<String, ServerInfo> en : servers.entrySet()) {
            if(sent.contains(en.getKey())) continue;
            sent.add(en.getKey());
            Events.instance.getLogger().info("Sending message to " + en.getKey() + "\nchannel:" + realChannel + "\nkey: " + key + "\nplayer: " + player + "\nvalue: " + value);
            String name = en.getKey();
            ServerInfo server = Events.instance.getProxy().getServerInfo(name);
            
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(stream);
            try {
                out.writeUTF(key);
                out.writeUTF(player);
                out.writeUTF(value);
                out.writeUTF(UUID.randomUUID().toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            server.sendData(realChannel, stream.toByteArray());
        }
    }
}