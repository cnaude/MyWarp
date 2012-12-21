package me.taylorkelly.mywarp.data;

import me.taylorkelly.mywarp.LanguageManager;
import me.taylorkelly.mywarp.WarpSettings;
import org.apache.commons.lang.StringUtils;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

public class SignWarp {

    /**
     * Precondition: Only call if isSignWarp() returned true
     */
    public static void warpSign(Sign sign, WarpList list, Player player) {
        String lines[] = {
            sign.getLine(1),
            sign.getLine(2),
            sign.getLine(3)
        };
        String name = StringUtils.join(lines);

        if (!list.warpExists(name)) {
            player.sendMessage(LanguageManager.getString(
                    "error.noSuchWarp").replaceAll("%warp%", name));
            return;
        }
        Warp warp = list.getWarp(name);

        if (!warp.playerCanWarp(player)) {
            player.sendMessage(LanguageManager.getString(
                    "error.noPermission.warpto").replaceAll("%warp%", name));
            return;
        }
        if (WarpSettings.worldAccess
                && !list.playerCanAccessWorld(player, warp.world)) {
            player.sendMessage(LanguageManager.getString(
                    "error.noPermission.world").replaceAll("%world%",
                    warp.world));
            return;
        }
        list.warpTo(name, player);
    }

    public static void createSignWarp(SignChangeEvent sign) {
        sign.setLine(0, "[MyWarp]");
    }

    public static boolean isSignWarp(Sign sign) {
        if (sign.getLine(0).equalsIgnoreCase("MyWarp") || sign.getLine(0).equalsIgnoreCase("[MyWarp]")) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isSignWarp(SignChangeEvent sign) {
        if (sign.getLine(0).equalsIgnoreCase("MyWarp") || sign.getLine(0).equalsIgnoreCase("[MyWarp]")) {
            return true;
        } else {
            return false;
        }
    }
}