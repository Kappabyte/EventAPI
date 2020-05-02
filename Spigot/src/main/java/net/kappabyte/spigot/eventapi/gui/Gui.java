package net.kappabyte.spigot.eventapi.gui;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.kappabyte.spigot.eventapi.API;
import net.md_5.bungee.api.ChatColor;

public abstract class Gui implements InventoryHolder, Listener {

    public Gui previousGui;

    public Inventory inv;

    public Gui(String name, int size) {
        inv = Bukkit.createInventory(this, size, name);

        Bukkit.getPluginManager().registerEvents(this, API.getPlugin());

        initializeItems();
    }

    @Override
    public Inventory getInventory() {
        return inv;
    }

    public void initializeItems() {
        inv.clear();
        
        if(previousGui != null) {
            inv.setItem(8, createGuiItem(Material.BARRIER, ChatColor.DARK_RED + "Back"));
        }
    }

    public static ItemStack createGuiItem(Material material, String name, String...lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        ArrayList<String> metalore = new ArrayList<String>();

        for (String lorecomments: lore) {
            metalore.add(lorecomments);
        }

        meta.setLore(metalore);
        item.setItemMeta(meta);
        return item;
    }

    public void openInventory(Player p, Gui previous) {
        previousGui = previous;
        initializeItems();
        p.openInventory(inv);
    }

    public void openInventory(Player p) {
        initializeItems();
        p.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(!e.getClickedInventory().equals(inv)) return;

        Player p = (Player) e.getWhoClicked();

        if(e.getRawSlot() == 8) {
            previousGui.openInventory(p);
        } else {
            onItemClicked(p, e.getRawSlot());
        }
        
        e.setCancelled(true);
    }

    public void onItemClicked(Player player, int slot) {
    }
}