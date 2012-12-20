package me.taylorkelly.mywarp.permissions;

import java.util.LinkedHashMap;
import java.util.Map;
import me.taylorkelly.mywarp.WarpSettings;
import me.taylorkelly.mywarp.data.WarpLimit;
import me.taylorkelly.mywarp.timer.Cooldown;
import me.taylorkelly.mywarp.timer.Warmup;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public final class PermissionsHandler {

    private final transient Plugin plugin;
    private static PluginManager pm;

    public PermissionsHandler(final Plugin plugin) {
        this.plugin = plugin;
        PermissionsHandler.pm = plugin.getServer().getPluginManager();
        registerPermissions();
    }

    public boolean hasPermission(final Player player, final String node) {
        return player.hasPermission(node);
    }

    // Only register permissions here that cannot be registered in plugin.yml!!!
    public void registerPermissions() {
        // mywarp.limit permissions
        for (WarpLimit warpLimit : WarpSettings.warpLimits) {
            pm.addPermission(new org.bukkit.permissions.Permission(
                    "mywarp.limit." + warpLimit.name,
                    "Gives acess to the number of warps defined for '"
                    + warpLimit.name + "' in the config",
                    PermissionDefault.FALSE));
        }

        // mywarp.cooldown permissions
        for (Cooldown warpCooldown : WarpSettings.warpCooldowns) {
            pm.addPermission(new org.bukkit.permissions.Permission(
                    "mywarp.cooldown." + warpCooldown.name,
                    "User is affected by the cooldowns defined for '"
                    + warpCooldown.name + "' in the config",
                    PermissionDefault.FALSE));
        }

        // mywarp.warmup permissions
        for (Warmup warpWarmup : WarpSettings.warpWarmups) {
            pm.addPermission(new org.bukkit.permissions.Permission(
                    "mywarp.warmup." + warpWarmup.name,
                    "User is affected by the warmups defined for '"
                    + warpWarmup.name + "' in the config",
                    PermissionDefault.FALSE));
        }

        // mywarp.warp.world permissions
        Map<String, Boolean> worldMap = new LinkedHashMap<String, Boolean>();
        for (World world : plugin.getServer().getWorlds()) {
            pm.addPermission(new org.bukkit.permissions.Permission(
                    "mywarp.warp.world." + world.getName(),
                    "User may warp to worlds in world '" + world.getName()
                    + "'", PermissionDefault.OP));
            worldMap.put("mywarp.warp.world." + world.getName(), true);
        }
        worldMap.put("mywarp.warp.world.currentworld", true);

        pm.addPermission(new org.bukkit.permissions.Permission(
                "mywarp.warp.world.*", "User may warp to all worlds",
                PermissionDefault.OP, worldMap));
    }
}
