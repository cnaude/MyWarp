package me.taylorkelly.mywarp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import me.taylorkelly.mywarp.commands.AdminWarpToCommand;
import me.taylorkelly.mywarp.commands.CommandHandler;
import me.taylorkelly.mywarp.commands.CreateCommand;
import me.taylorkelly.mywarp.commands.CreatePrivateCommand;
import me.taylorkelly.mywarp.commands.DeleteCommand;
import me.taylorkelly.mywarp.commands.GiveCommand;
import me.taylorkelly.mywarp.commands.HelpCommand;
import me.taylorkelly.mywarp.commands.ImportCommand;
import me.taylorkelly.mywarp.commands.InviteCommand;
import me.taylorkelly.mywarp.commands.ListCommand;
import me.taylorkelly.mywarp.commands.PointCommand;
import me.taylorkelly.mywarp.commands.PrivateCommand;
import me.taylorkelly.mywarp.commands.PublicCommand;
import me.taylorkelly.mywarp.commands.ReloadCommand;
import me.taylorkelly.mywarp.commands.ListAllCommand;
import me.taylorkelly.mywarp.commands.SearchCommand;
import me.taylorkelly.mywarp.commands.UninviteCommand;
import me.taylorkelly.mywarp.commands.UpdateCommand;
import me.taylorkelly.mywarp.commands.WarpToCommand;
import me.taylorkelly.mywarp.commands.WelcomeCommand;
import me.taylorkelly.mywarp.data.WarpList;
import me.taylorkelly.mywarp.dataconnections.ConnectionManager;
import me.taylorkelly.mywarp.dataconnections.DataConnectionException;
import me.taylorkelly.mywarp.listeners.MWBlockListener;
import me.taylorkelly.mywarp.listeners.MWEntityListener;
import me.taylorkelly.mywarp.listeners.MWPlayerListener;
import me.taylorkelly.mywarp.permissions.WarpPermissions;
import me.taylorkelly.mywarp.utils.WarpLogger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MyWarp extends JavaPlugin {

    private WarpList warpList;
    private MWBlockListener blockListener;
    private MWEntityListener entityListener;
    private MWPlayerListener playerListener;

    public String name;
    public String version;
    private PluginManager pm;
    private CommandHandler commandHandler;
    private static WarpPermissions warpPermissions;
    public static ConnectionManager connectionManager;

    @Override
    public void onDisable() {
        if (connectionManager != null) {
            connectionManager.close();
        }
        Bukkit.getServer().getScheduler().cancelTasks(this);
    }

    @Override
    public void onEnable() {
        name = this.getDescription().getName();
        version = this.getDescription().getVersion();
        pm = getServer().getPluginManager();

        WarpSettings.initialize(this);
        LanguageManager.initialize(this);

        try {
            connectionManager = new ConnectionManager(WarpSettings.usemySQL, true, true);
        } catch (DataConnectionException e) {
            WarpLogger
                    .severe("Could not establish database connection. Disabling MyWarp.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        File newDatabase = new File(getDataFolder(), "warps.db");
        File oldDatabase = new File("homes-warps.db");
        if (!newDatabase.exists() && oldDatabase.exists()) {
            updateFiles(oldDatabase, newDatabase);
        }

        warpList = new WarpList(getServer());
        warpPermissions = new WarpPermissions();
        blockListener = new MWBlockListener(this);
        entityListener = new MWEntityListener();
        playerListener = new MWPlayerListener(this);

        pm.registerEvents(blockListener, this);
        pm.registerEvents(entityListener, this);
        pm.registerEvents(playerListener, this);

        commandHandler = new CommandHandler(this);

        // basic commands
        commandHandler.addCommand(new CreateCommand(this));
        commandHandler.addCommand(new CreatePrivateCommand(this));
        commandHandler.addCommand(new DeleteCommand(this));
        commandHandler.addCommand(new ListCommand(this));
        commandHandler.addCommand(new ListAllCommand(this));
        commandHandler.addCommand(new PointCommand(this));
        commandHandler.addCommand(new SearchCommand(this));
        commandHandler.addCommand(new UpdateCommand(this));
        commandHandler.addCommand(new WelcomeCommand(this));
        commandHandler.addCommand(new WarpToCommand(this));

        // social commands
        commandHandler.addCommand(new GiveCommand(this));
        commandHandler.addCommand(new InviteCommand(this));
        commandHandler.addCommand(new PrivateCommand(this));
        commandHandler.addCommand(new PublicCommand(this));
        commandHandler.addCommand(new UninviteCommand(this));

        // help command
        commandHandler.addCommand(new HelpCommand(this));

        // admin commands
        commandHandler.addCommand(new AdminWarpToCommand(this));
        commandHandler.addCommand(new ReloadCommand(this));
        commandHandler.addCommand(new ImportCommand(this));

        WarpLogger.info(name + " " + version + " enabled");
    }

    private void updateFiles(File oldDatabase, File newDatabase) {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        if (newDatabase.exists()) {
            newDatabase.delete();
        }
        try {
            newDatabase.createNewFile();
        } catch (IOException ex) {
            WarpLogger.severe("Could not create new database file", ex);
        }
        copyFile(oldDatabase, newDatabase);
    }

    /**
     * File copier from xZise
     * 
     * @param fromFile
     * @param toFile
     */
    private void copyFile(File fromFile, File toFile) {
        FileInputStream from = null;
        FileOutputStream to = null;
        try {
            from = new FileInputStream(fromFile);
            to = new FileOutputStream(toFile);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = from.read(buffer)) != -1) {
                to.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            WarpLogger.severe("Failed to rename " + fromFile.getName() + "to " + toFile.getName() + ": ", ex);
        } finally {
            if (from != null) {
                try {
                    from.close();
                } catch (IOException e) {
                }
            }
            if (to != null) {
                try {
                    to.close();
                } catch (IOException e) {
                }
            }
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {  
        if (commandHandler == null) {
            System.out.println("NULL");
            return false;
        } else {
            return commandHandler.dispatch(sender, command, commandLabel, args);
        }
    }

    public static WarpPermissions getWarpPermissions() {
        return warpPermissions;
    }

    public WarpList getWarpList() {
        return warpList;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }
}
