package net.kappabyte.spigot.eventapi.game;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.kappabyte.spigot.eventapi.PlayerInv;

public class GamePlayer {

    public static ArrayList<String> toAdd = new ArrayList<String>();
	public static ArrayList<String> SpectatorsToAdd = new ArrayList<String>();

    public GamePlayer(Player player) {
        bukkitPlayer = player;
        name = player.getName();
	}

	public void tpToStartingPosition() {
        bukkitPlayer.teleport(startingLocation);
    }
    
    public void dropInventory() {
        for (ItemStack item : bukkitPlayer.getInventory().getStorageContents()) {
            if(item == null) continue;
            if(!item.getType().equals(Material.AIR)) {
                bukkitPlayer.getWorld().dropItemNaturally(bukkitPlayer.getLocation(), item);
            }
        }
        for (ItemStack item : bukkitPlayer.getInventory().getArmorContents()) {
            if(item == null) continue;
            if(!item.getType().equals(Material.AIR)) {
                bukkitPlayer.getWorld().dropItemNaturally(bukkitPlayer.getLocation(), item);
            }
        }
        ExperienceOrb orb = bukkitPlayer.getWorld().spawn(bukkitPlayer.getLocation(), ExperienceOrb.class);
        orb.setExperience(bukkitPlayer.getTotalExperience() / 2);
        
        PlayerInv.clearInventory(bukkitPlayer.getInventory());
        bukkitPlayer.setLevel(0);
        bukkitPlayer.setExp(0.0f);
    }

	public Location startingLocation;
    public Player bukkitPlayer;

    public int killCount = 0;

    public String name;
}