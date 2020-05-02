package net.kappabyte.spigot.eventapi.gui;

import java.util.HashMap;

import org.bukkit.entity.Player;

public class GuiManager {

    private static HashMap<String, Gui> guis = new HashMap<String, Gui>();

    private static EventMenu eventGui;

    static {
        eventGui = new EventMenu();
    }

    public static void addGUI(String event, String guiName, Gui gui) {
        guis.put(event + "\\" + guiName, gui);
    }

    public static Gui getGUI(String event, String guiName) {
        return guis.get(event + "\\" + guiName);
    }

    public static void openGui(String event, String guiName, Player player) {
        Gui gui = getGUI(event, guiName);
        if(gui != null) {
            gui.openInventory(player);
        }
    }

    public static void openGui(String event, String guiName, Player player, Gui previousGui) {
        Gui gui = getGUI(event, guiName);
        if(gui != null) {
            gui.openInventory(player, previousGui);
        }
    }

    public static void openEventGui(Player player, Gui previousGui) {
        eventGui.openInventory(player, previousGui);
    }

    public static void openEventGui(Player player) {
        eventGui.openInventory(player, null);
    }

}