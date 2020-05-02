package net.kappabyte.bungee.events.commands;

import net.kappabyte.bungee.events.Config.ConfigManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCmd extends Command {

    public static boolean canJoinEvent = false;

    public ReloadCmd() {
        super("event-reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ConfigManager.reload();
    }

}