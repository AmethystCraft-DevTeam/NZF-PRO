package org.atcplugin.wzf;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public final class NZFPRO extends JavaPlugin {

    private final CooldownManager cooldownManager = new CooldownManager();
    private final CommandManager commandManager = new CommandManager(this);

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info(ChatColor.GREEN + "欢迎使用 NZF-PRO");
        Bukkit.broadcastMessage(ChatColor.GREEN + "欢迎使用 NZF-PRO");

        // Register the command
        this.getCommand("kill-i").setExecutor(commandManager);
        this.getCommand("dupe").setExecutor(commandManager);

        // Register the event listener
        Bukkit.getPluginManager().registerEvents(new EventListener(this), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info(ChatColor.RED + "NZF-PRO 已被禁用");
    }

    void killPlayer(Player player) {
        player.setHealth(0);
        player.sendMessage(ChatColor.RED + "你已经被杀死！");
    }

    void dupeShulkerBox(Player player) {
        PlayerInventory inventory = player.getInventory();
        ItemStack heldItem = inventory.getItemInMainHand();

        if (isShulkerBox(heldItem)) {
            ItemStack newShulkerBox = heldItem.clone();
            newShulkerBox.setAmount(1);
            inventory.addItem(newShulkerBox);
        } else {
            player.sendMessage(ChatColor.RED + "你必须在主手中拿着一个潜影盒才能使用此命令。");
        }
    }

    boolean isShulkerBox(ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }
        Material type = item.getType();
        return type == Material.SHULKER_BOX ||
               type == Material.BLACK_SHULKER_BOX ||
               type == Material.BLUE_SHULKER_BOX ||
               type == Material.BROWN_SHULKER_BOX ||
               type == Material.CYAN_SHULKER_BOX ||
               type == Material.GRAY_SHULKER_BOX ||
               type == Material.GREEN_SHULKER_BOX ||
               type == Material.LIGHT_BLUE_SHULKER_BOX ||
               type == Material.LIGHT_GRAY_SHULKER_BOX ||
               type == Material.LIME_SHULKER_BOX ||
               type == Material.MAGENTA_SHULKER_BOX ||
               type == Material.ORANGE_SHULKER_BOX ||
               type == Material.PINK_SHULKER_BOX ||
               type == Material.PURPLE_SHULKER_BOX ||
               type == Material.RED_SHULKER_BOX ||
               type == Material.WHITE_SHULKER_BOX ||
               type == Material.YELLOW_SHULKER_BOX;
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public void dupeShulkerBoxForPlayer(Player player) {
        dupeShulkerBox(player);
    }

    public boolean isShulkerBoxForPlayer(ItemStack item) {
        return isShulkerBox(item);
    }
}

class CommandManager implements org.bukkit.command.CommandExecutor {
    private final NZFPRO plugin;

    public CommandManager(NZFPRO plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (command.getName().equalsIgnoreCase("kill-i")) {
                plugin.killPlayer(player);
                return true;
            } else if (command.getName().equalsIgnoreCase("dupe")) {
                if (plugin.getCooldownManager().canUseDupeCommand(player)) {
                    plugin.dupeShulkerBoxForPlayer(player);
                    plugin.getCooldownManager().setCooldown(player);
                }
                return true;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "此命令只能由玩家使用。");
        }
        return false;
    }
}

class CooldownManager {
    private final Map<Player, Long> cooldownMap = new HashMap<>();

    public boolean canUseDupeCommand(Player player) {
        Long lastUsedTime = cooldownMap.get(player);
        if (lastUsedTime == null || (System.currentTimeMillis() - lastUsedTime) > 600000) { // 10 minutes in milliseconds
            return true;
        }
        player.sendMessage(ChatColor.RED + "你必须等待10分钟才能再次使用此命令。");
        return false;
    }

    public void setCooldown(Player player) {
        cooldownMap.put(player, System.currentTimeMillis());
    }
}

class EventListener implements Listener {
    private final NZFPRO plugin;

    public EventListener(NZFPRO plugin) {
        this.plugin = plugin;
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
