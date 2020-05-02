package net.kappabyte.spigot.eventapi;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInv {

    ItemStack[] items;
    ItemStack helmet = null;
    ItemStack chestplate = null;
    ItemStack leggings = null;
    ItemStack boots = null;

    public PlayerInv(PlayerInventory inv) {
        items = inv.getContents().clone();
        if(inv.getHelmet() != null) helmet = inv.getHelmet().clone();
        if(inv.getChestplate() != null) chestplate = inv.getChestplate().clone();
        if(inv.getLeggings() != null) leggings = inv.getLeggings().clone();
        if(inv.getBoots() != null) boots = inv.getBoots().clone();
    }

    public static PlayerInventory clearInventory(PlayerInventory inv){
        inv.clear();
        inv.setHelmet(null);
        inv.setChestplate(null);
        inv.setLeggings(null);
        inv.setBoots(null);

        return inv;
    }

    public PlayerInventory setInventoryContents(PlayerInventory inv) {
        inv.setContents(items);
        inv.setHelmet(helmet);
        inv.setChestplate(chestplate);
        inv.setLeggings(leggings);
        inv.setBoots(boots);

        return inv;
    }
}