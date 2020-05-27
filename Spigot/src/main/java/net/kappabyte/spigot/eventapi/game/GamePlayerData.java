package net.kappabyte.spigot.eventapi.game;

import java.util.Collection;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import net.kappabyte.spigot.eventapi.PlayerInv;

public class GamePlayerData {

    private PlayerInv inv;
    private GameMode gamemode;
    private Collection<PotionEffect> potionEffects;
    private double health;
    private int hunger;
    private float xp;
    private int levels;

    private Location location;

    public GamePlayerData(PlayerInv inv, GameMode gamemode, Collection<PotionEffect> potionEffects, double health, int hunger, float xp, int levels, Location location) {
        this.inv = inv;
        this.gamemode = gamemode;
        this.potionEffects = potionEffects;
        this.health = health;
        this.hunger = hunger;
        this.xp = xp;
        this.levels = levels;
        this.location = location;
    }

    public GamePlayerData(PlayerInv inv, GameMode gamemode, Collection<PotionEffect> potionEffects, double health, int hunger, float xp, int levels) {
        this.inv = inv;
        this.gamemode = gamemode;
        this.potionEffects = potionEffects;
        this.health = health;
        this.hunger = hunger;
        this.xp = xp;
        this.levels = levels;
    }

    public GamePlayerData(Player player, boolean includeLocation) {
        this.inv = new PlayerInv(player.getInventory());
        this.gamemode = player.getGameMode();
        this.potionEffects = player.getActivePotionEffects();
        this.health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
        this.hunger = player.getFoodLevel();
        this.xp = player.getExp();
        this.levels = player.getLevel();

        if(includeLocation) {
            location = player.getLocation().clone();
        }
    }

    public void setHealthAndHunger(Double health, Integer hunger) {
        if(health != null) {
            this.health = health;
        }
        if(hunger != null) {
            this.hunger = hunger;
        }
    }

    public static void clearPlayerData(Player player) {
        PlayerInv.clearInventory(player.getInventory());
        player.setExp(0);
        player.setLevel(0);
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
    }

    public void resetPlayerData(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        for(AttributeModifier mod : player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getModifiers()) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).removeModifier(mod);
        }

        PlayerInv.clearInventory(player.getInventory());
        inv.setInventoryContents(player.getInventory());
        player.setGameMode(gamemode);
        for(PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        for(PotionEffect effect : potionEffects) {
            player.addPotionEffect(effect);
        }
        player.setHealth(health);
        player.setFoodLevel(hunger);
        player.setExp(xp);
        player.setLevel(levels);

        if(location != null) {
            player.teleport(location);
        }
    }
}