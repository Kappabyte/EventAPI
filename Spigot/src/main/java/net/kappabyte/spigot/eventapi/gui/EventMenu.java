package net.kappabyte.spigot.eventapi.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.kappabyte.spigot.eventapi.API;
import net.kappabyte.spigot.eventapi.gui.Gui;
import net.md_5.bungee.api.ChatColor;

public class EventMenu extends Gui {

    public EventMenu() {
        super("Event Controls", 54);
    }

    @Override
    public void initializeItems() {
        super.initializeItems();
        inv.setItem(10, createGuiItem(Material.LIME_WOOL, ChatColor.GREEN + "Open Event"));
        inv.setItem(16, createGuiItem(Material.RED_WOOL, ChatColor.RED + "Close Event"));
        inv.setItem(13, createGuiItem(Material.ORANGE_WOOL, ChatColor.WHITE + "Broadcast Event"));
        inv.setItem(40, createGuiItem(Material.ANVIL, ChatColor.DARK_GRAY + "Kick Players"));
    }

    @Override
    public void onItemClicked(Player player, int slot) {
        switch(slot) {
            case 10:
                API.getPlugin().getLogger().info("sending open message!");
                API.handler.openEvent();
                API.getPlugin().getLogger().info("sent open message!");
                break;
            case 16:
                API.handler.closeEvent();
                break;
            case 13:
                API.handler.broadcastEvent();
                break;
            case 40:
                player.sendMessage("comming soon");
                break;
        }
    }
}