package org.atcplugin.wzf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Random;

public final class NZFPRO extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info(ChatColor.GREEN + "欢迎使用 NZF-PRO");
        Bukkit.broadcastMessage(ChatColor.GREEN + "欢迎使用 NZF-PRO");

        // Register the command
        this.getCommand("kill-i").setExecutor(this);

        // Register the event listener
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info(ChatColor.RED + "NZF-PRO 已被禁用");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kill-i")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                killPlayer(player);
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "此命令只能由玩家使用。");
                return true;
            }
        }
        return false;
    }

    private void killPlayer(Player player) {
        player.setHealth(0);
        player.sendMessage(ChatColor.RED + "你已经被杀死！");
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Location bedLocation = player.getBedSpawnLocation();

        if (bedLocation != null) {
            // If the player has a valid bed spawn location, use that
            event.setRespawnLocation(bedLocation);
        } else {
            // Otherwise, generate a random location within the range of -5000 to 5000
            Random random = new Random();
            int x = random.nextInt(10001) - 5000;
            int z = random.nextInt(10001) - 5000;

            // Find a safe location at the ground level
            int y = world.getHighestBlockYAt(x, z);

            // Create a new location
            Location respawnLocation = new Location(world, x, y, z);

            // Set the player's respawn location
            event.setRespawnLocation(respawnLocation);
        }
    }
}
