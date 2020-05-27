package net.kappabyte.spigot.events.API;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.game.Game;
import net.kappabyte.spigot.events.Events;
import net.md_5.bungee.api.ChatColor;

public class EventSystemAPI {

    private static Event currentEvent;

    public static void createEvent(String name, Object game, Player host) {
        currentEvent = new Event(game, name, host);
    }

    public static void openEvent() {
        API.getPlugin().getLogger().info("Game: " + currentEvent);
        API.getPlugin().getLogger().info("Host: " + currentEvent.host);
        if (currentEvent == null)
            return;
        currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lEvent &7Opened the event!"));
        currentEvent.open = true;
    }

    public static void closeEvent() {
        if (currentEvent == null)
            return;
        currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lEvent &7Closed the event!"));
        currentEvent.open = false;
    }

    public static void broadcastEvent() {
        if (currentEvent == null)
            return;
        if (!currentEvent.open) {
            currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&cCould not broadcast the event. The event is not open!"));
            return;
        }
        for (Player p : Events.instance.getServer().getOnlinePlayers()) {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                    "&4&lEvent &7A new &b" + currentEvent.name + " &7event has started. To join, use &b/joinevent"));
        }
    }

    public static boolean joinEvent(Player player) {
        if (currentEvent == null)
            return false;
        if (!currentEvent.open) {
            player.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', "&cCould not join event! The event is not open!"));
            return false;
        }
        currentEvent.players.put(player.getName(), player);
        currentEvent.playerOriginalPosition.put(player, player.getLocation());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lEvent &7Joined the event!"));
        for (Player p : Events.instance.getServer().getOnlinePlayers()) {
            if (!p.getName().equals(player.getName())) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lEvent &b" + player.getName() + " &7joined the event!"));
            }
        }
        try {
            currentEvent.game.getClass().getMethod("addPlayer", Player.class).invoke(currentEvent.game, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()
                    + " tried to join the event, but an error occurred with with the plugin. Please contact the plugin developer for the game you are hosting."));
            e.printStackTrace();
        }

        Location l = null;
        try {
            l = (Location) currentEvent.game.getClass().getField("gameLocation").get(currentEvent.game);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        if (l != null) {
            player.teleport(l);
        }

        return true;
    }

    public static boolean leaveEvent(Player player) {
        if (currentEvent == null)
            return false;
        if (!currentEvent.players.containsKey(player.getName())) {
            return false;
        }
        currentEvent.players.remove(player.getName());
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4&lEvent &7Left the event!"));

        for (Player p : Events.instance.getServer().getOnlinePlayers()) {
            if (!p.getName().equals(player.getName())) {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                        "&4&lEvent &b" + player.getName() + " &7left the event!"));
            }
        }
        try {
            currentEvent.game.getClass().getMethod("removePlayer", Player.class, Game.RemoveReason.class).invoke(currentEvent.game, player, Game.RemoveReason.LEFT_GAME);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()
                    + " tried to leave the event, but an error occurred with with the plugin. Please contact the plugin developer for the game you are hosting."));
            e.printStackTrace();
        }

        player.teleport(currentEvent.playerOriginalPosition.get(player));

        return true;
    }

    public static boolean addPlayer(Player player) {
        if (currentEvent == null)
            return false;
        if (!currentEvent.open) {
            return false;
        }
        currentEvent.players.put(player.getName(), player);
        currentEvent.playerOriginalPosition.put(player, player.getLocation());
        try {
            currentEvent.game.getClass().getMethod("addPlayer", Player.class).invoke(currentEvent.game, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()
                    + " tried to join the event, but an error occurred with with the plugin. Please contact the plugin developer for the game you are hosting."));
            e.printStackTrace();
        }

        Location l = null;
        try {
            l = (Location) currentEvent.game.getClass().getField("gameLocation").get(currentEvent.game);
        } catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        if (l != null) {
            player.teleport(l);
        }

        return true;
    }

    public static boolean removePlayer(Player player) {
        if (currentEvent == null)
            return false;
        if (!currentEvent.players.containsKey(player.getName())) {
            return false;
        }
        currentEvent.players.remove(player.getName());

        try {
            currentEvent.game.getClass().getMethod("removePlayer", Player.class).invoke(currentEvent.game, player);
        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            currentEvent.host.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c" + player.getName()
                    + " tried to leave the event, but an error occurred with with the plugin. Please contact the plugin developer for the game you are hosting."));
            e.printStackTrace();
        }

        player.teleport(currentEvent.playerOriginalPosition.get(player));

        return true;
    }

    public static void setHost(Player player) {
        if (currentEvent == null)
            return;
        currentEvent.host.sendMessage("&cYou are now the host of the event!");
        currentEvent.host = player;
        player.sendMessage("&aYou are now the host of the event!");
    }

    public static Player getHost(Player player) {
        if (currentEvent == null)
            return null;
        return currentEvent.host;
    }

    public static void getPlayer(Player player) {
        if (currentEvent == null)
            return;
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&4&lEvent &7The host of the event is " + currentEvent.host.getName()));
    }

    public static boolean getPlayer(String player) {
        if (currentEvent == null)
            return false;
        return currentEvent.players.containsKey(player);
    }

    public static void endGame(boolean tp) {
        if (currentEvent == null)
            return;
        // Teleport everyone back to where they where if wanted
        if(tp) {
            for (Player player : currentEvent.players.values()) {
                player.teleport(currentEvent.playerOriginalPosition.get(player));
            }
        }

        // Remove the event
        currentEvent = null;
    }

    public static void setFirstPlace(String player) {
        Events.instance.pasteventsConfig.set("events." + currentEvent.eventStartTime.getTime() + ".first", player);
    }

    public static void setSecondPlace(String player) {
        Events.instance.pasteventsConfig.set("events." + currentEvent.eventStartTime.getTime() + ".second", player);
    }

    public static void setThirdPlace(String player) {
        Events.instance.pasteventsConfig.set("events." + currentEvent.eventStartTime.getTime() + ".third", player);
    }
}