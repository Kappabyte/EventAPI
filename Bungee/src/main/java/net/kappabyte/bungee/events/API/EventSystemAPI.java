package net.kappabyte.bungee.events.API;

import java.util.Date;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.kappabyte.bungee.events.Events;
import net.kappabyte.bungee.events.API.tournament.Tournament;
import net.kappabyte.bungee.events.API.tournament.TournamentTeam;
import net.kappabyte.bungee.events.Config.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

public class EventSystemAPI {

    private static ComponentBuilder prefix = new ComponentBuilder("Event ").color(ChatColor.DARK_RED).bold(true).append(" ").color(ChatColor.GRAY).bold(false);

    private static Event currentEvent;

    public static void createEvent(String name, ProxiedPlayer host) {
        currentEvent = new Event(name, host);
        Events.instance.getLogger().info("Created new event!");

        Configuration eventSection = new Configuration();
        eventSection.set("name" + new Date().getTime(), name);

        ConfigManager.eventStorage.set("events." + currentEvent.eventStartTime.getTime() + ".name", name);
        ConfigManager.saveAll();
    }

    public static boolean getEventOpen() {
        if(currentEvent == null) return false;
        return currentEvent.open;
    }

    public static void openEvent() {
        if(currentEvent == null) { 
            BungeeAPI.sendMessage("failopen", "", "");
            return;
        }
        currentEvent.host.sendMessage(new ComponentBuilder(prefix).append("opened the event!").create());
        currentEvent.open = true;
        BungeeAPI.sendMessage("open", currentEvent.host.getName(), currentEvent.name);

        if(Tournament.getTournamentActive()) {
            for(TournamentTeam team : Tournament.getTournament().getTeams()) {
                addPlayer(team.player1);
                addPlayer(team.player2);
            }
            for(ProxiedPlayer player : Tournament.getTournament().getSpectators()) {
                addSpectator(player);
            }

            BungeeAPI.sendMessage("start", "", "");
        }
    }

    public static void closeEvent() {
        if(currentEvent == null) return;
        currentEvent.host.sendMessage(new ComponentBuilder(prefix).append("closed the event!").create());
        currentEvent.open = false;
        BungeeAPI.sendMessage("close", currentEvent.host.getName(), currentEvent.name);
    }

    public static void broadcastEvent() {
        if(currentEvent == null) return;
        if (!currentEvent.open) {
            currentEvent.host.sendMessage(new ComponentBuilder("Cannot broadcast the event when the server is closed!").color(ChatColor.RED).create());
            return;
        }
        Events.instance.getProxy().broadcast(new ComponentBuilder(prefix).append("A new " + currentEvent.name + " event has started! To join, use: ").append("/joinevent").color(ChatColor.AQUA).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/joinevent")).event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to join the event!").create())).create());
    }

    public static boolean joinEvent(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if (!currentEvent.open) {
            player.sendMessage(new ComponentBuilder("Cannot join the event! The event is not open!").color(ChatColor.RED).create());
            return false;
        }
        Events.instance.getLogger().info("CurrentEvent: " + currentEvent);
        Events.instance.getLogger().info("Player: " + player);
        currentEvent.players.put(player.getName(), player);
        currentEvent.playerOriginalPosition.put(player, player.getServer().getInfo());

        player.sendMessage(new ComponentBuilder(prefix).append("Joined the event!").create());
        if(!player.getServer().getInfo().getName().equals(ConfigManager.getEventServer())) {
            player.connect(Events.instance.getProxy().getServerInfo(ConfigManager.getEventServer()));
        }

        BungeeAPI.addPlayer(player);

        for (ProxiedPlayer p : Events.instance.getProxy().getPlayers()) {
            if (!p.getName().equals(player.getName())) {
                player.sendMessage(new ComponentBuilder(prefix).append(player.getName()).color(ChatColor.AQUA).append(" has joined the event!").create());
            }
        }

        return true;
    }

    public static boolean leaveEvent(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if(!currentEvent.players.containsKey(player.getName())) {
            return false;
        }
        currentEvent.players.remove(player.getName());
        player.sendMessage(new ComponentBuilder(prefix).append("Left the event!").create());

        BungeeAPI.removePlayer(player);

        currentEvent.players.remove(player.getName());

        if(!player.getServer().getInfo().getName().equals(currentEvent.playerOriginalPosition.get(player).getName())) {
            player.connect(currentEvent.playerOriginalPosition.get(player));
        }
        currentEvent.playerOriginalPosition.remove(player);

        for (ProxiedPlayer p : Events.instance.getProxy().getPlayers()) {
            if (!p.getName().equals(player.getName())) {
                player.sendMessage(new ComponentBuilder(prefix).append(player.getName()).color(ChatColor.AQUA).append("has left the event!").create());
            }
        }

        return true;
    }

    public static boolean addPlayer(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if (!currentEvent.open) {
            return false;
        }
        currentEvent.players.put(player.getName(), player);
        currentEvent.playerOriginalPosition.put(player, player.getServer().getInfo());

        if(!player.getServer().getInfo().getName().equals(ConfigManager.getEventServer())) {
            player.connect(Events.instance.getProxy().getServerInfo(ConfigManager.getEventServer()));
        }
        BungeeAPI.addPlayer(player);

        return true;
    }

    public static boolean addSpectator(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if (!currentEvent.open) {
            return false;
        }
        currentEvent.spectators.put(player.getName(), player);
        currentEvent.playerOriginalPosition.put(player, player.getServer().getInfo());

        if(!player.getServer().getInfo().getName().equals(ConfigManager.getEventServer())) {
            player.connect(Events.instance.getProxy().getServerInfo(ConfigManager.getEventServer()));
        }
        BungeeAPI.addSpectator(player);

        return true;
    }

    public static boolean removePlayer(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if(!currentEvent.players.containsKey(player.getName())) {
            return false;
        }
        currentEvent.players.remove(player.getName());

        if(!player.getServer().getInfo().getName().equals(currentEvent.playerOriginalPosition.get(player).getName())) {
            player.connect(currentEvent.playerOriginalPosition.get(player));
        }
        currentEvent.playerOriginalPosition.remove(player);

        BungeeAPI.removePlayer(player);

        return true;
    }

    public static boolean removeSpectator(ProxiedPlayer player) {
        if(currentEvent == null) return false;
        if(!currentEvent.spectators.containsKey(player.getName())) {
            return false;
        }
        currentEvent.spectators.remove(player.getName());

        if(!player.getServer().getInfo().getName().equals(currentEvent.playerOriginalPosition.get(player).getName())) {
            player.connect(currentEvent.playerOriginalPosition.get(player));
        }
        currentEvent.playerOriginalPosition.remove(player);

        BungeeAPI.removeSpectator(player);

        return true;
    }

    public static void setHost(ProxiedPlayer player) {
        if(currentEvent == null) return;
        currentEvent.host.sendMessage(new ComponentBuilder("You are no longer the host of the event!").color(ChatColor.RED).create());
        currentEvent.host = player;
        player.sendMessage(new ComponentBuilder("You are now the host of the event!").color(ChatColor.GREEN).create());
    }

    public static void getHost(ProxiedPlayer p) {
        if(currentEvent == null) return;
        p.sendMessage(new ComponentBuilder(prefix).append("The host of the event is ").append(currentEvent.host.getName()).color(ChatColor.AQUA).create());
    }

    public static void getPlayer(ProxiedPlayer asking, ProxiedPlayer player) {
        if(currentEvent == null) return;
        getPlayer(asking, player.getName());
    }
    public static void getPlayer(ProxiedPlayer asking, String player) {
        if(currentEvent == null) return;
        asking.sendMessage(new ComponentBuilder(prefix).append("The player ").append(player).color(ChatColor.AQUA).append(currentEvent.players.containsKey(player) ? " is " : " is not ").color(ChatColor.GRAY).append("in the event").create());
    }

    public static void endGame(boolean tp) {
        if(currentEvent == null) return;

        if(tp) {
            for(ProxiedPlayer player : currentEvent.players.values()) {
                if(!player.getServer().getInfo().getName().equals(currentEvent.playerOriginalPosition.get(player).getName())) {
                    player.connect(currentEvent.playerOriginalPosition.get(player));
                }
            }
        }

        currentEvent = null;
    }

    public static void setFirstPlace(String player) {
        Events.instance.getLogger().info("Event Storage: " + ConfigManager.eventStorage);
        Events.instance.getLogger().info("CurrentEvent: " + currentEvent);
        Events.instance.getLogger().info("Start Time: " + currentEvent.eventStartTime);
        Events.instance.getLogger().info("Get Time: " + currentEvent.eventStartTime.getTime());
        ConfigManager.eventStorage.set("events." + currentEvent.eventStartTime.getTime() + ".first", player);
    }

    public static void setSecondPlace(String player) {
        ConfigManager.eventStorage.set("events." + currentEvent.eventStartTime.getTime() + ".second", player);
    }

    public static void setThirdPlace(String player) {
        ConfigManager.eventStorage.set("events." + currentEvent.eventStartTime.getTime() + ".third", player);
    }

	public static void getPastEvents(String player) {
        ConfigManager.reload();
        JsonObject json = new JsonObject();
        JsonArray events = new JsonArray();
        Configuration configEvents = ConfigManager.eventStorage.getSection("events");
        for(String key : configEvents.getKeys()) {
            JsonObject object = new JsonObject();
            object.addProperty("name", configEvents.getString(key + ".name") + "");
            object.addProperty("date", key);
            object.addProperty("first", configEvents.getString(key + ".first") + "");
            object.addProperty("second", configEvents.getString(key + ".second") + "");
            object.addProperty("third", configEvents.getString(key + ".third") + "");
            events.add(object);
        }

        json.add("events", events);

        BungeeAPI.sendPastEvents(json.toString(), player);
    }
}