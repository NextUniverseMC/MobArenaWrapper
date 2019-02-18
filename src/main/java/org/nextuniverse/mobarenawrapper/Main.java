package org.nextuniverse.mobarenawrapper;

import ml.nextuniverse.RandomGame.CountdownStartedEvent;
import net.md_5.bungee.api.ChatColor;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by TheDiamondPicks on 14/10/2017.
 */
public class Main extends JavaPlugin implements Listener {
    boolean canJoin = true;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        if (!setupPermissions()) {
            Bukkit.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
    }

    public static Permission permission = null;

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    @EventHandler
    public void onSignPlace(SignChangeEvent e) {
            Sign sign = (Sign) e.getBlock().getState();
            if (e.getLine(0).equals("[MobArenaW]")) {
                if (!e.getLine(1).equals("")) {
                    String arena = e.getLine(1);
                    e.setLine(0, "");
                    e.setLine(1, ChatColor.LIGHT_PURPLE + "[Join]");
                    e.setLine(2, arena);
                }
            }
    }

    @EventHandler
    public void onSignClick(PlayerInteractEvent e) {
        if (e.getClickedBlock() != null) {
            if (e.getClickedBlock().getType() == Material.WALL_SIGN) {
                Sign sign = (Sign) e.getClickedBlock().getState();
                if (sign.getLine(1).equals(ChatColor.LIGHT_PURPLE + "[Join]")) {
                    if (canJoin) {
                        permission.playerRemove(e.getPlayer(), "-mobarena.use.join");
                        e.getPlayer().performCommand("ma join " + sign.getLine(2));
                        permission.playerAdd(e.getPlayer(), "-mobarena.use.join");
                    }
                    else {
                        e.getPlayer().sendMessage(ChatColor.LIGHT_PURPLE + "[MobArena] " + ChatColor.GRAY + "Joining is disabled as the server is about to restart!");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        permission.playerAdd(e.getPlayer(), "-mobarena.use.join");
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        permission.playerRemove(e.getPlayer(), "-mobarena.use.join");
    }

    @EventHandler
    public void onCountDownStart(CountdownStartedEvent e) {
        canJoin = false;
    }

    @EventHandler
    public void hunger(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }
}
