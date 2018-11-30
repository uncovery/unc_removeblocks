package me.uncovery.removeblocks;

import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Main extends JavaPlugin {
    public static File confChunkListFile;
    public static FileConfiguration confChunkListFileConfig;    
    public static Material confTargetMaterial;
    public static List<String> confSearchMaterials;
    public static List<String> confSearchWorlds;
    public static boolean debug;
    public static JavaPlugin thisPlugin;
    public static boolean chunkLoaderEnabled;
    
    @Override
    public void onEnable() { 
        thisPlugin = this;
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.reloadConfig();        

        this.debug = this.getConfig().getBoolean("debug");
        createCustomConfig();
        
        loadConfig();
        Boolean autostart = Main.thisPlugin.getConfig().getBoolean("autostart");        
        if (autostart) {
            thisPlugin.getLogger().log(Level.INFO, "Configured Autostart activated");          
            chunkLoaderEnabled = true;
        }

        getServer().getPluginManager().registerEvents(new ChunkLoadListener(), this);
    }
    
    public void loadConfig() {
        // get & display config
        String replacerMatString = this.getConfig().getString("replace_with");
        getLogger().log(Level.INFO, "Configured replace with {0}", replacerMatString);
        System.out.println(); 
        confTargetMaterial = Material.valueOf(replacerMatString);
        
        confSearchMaterials = this.getConfig().getStringList("blocks");
        getLogger().log(Level.INFO, "Configured search for blocks {0}", confSearchMaterials);

        confSearchWorlds = this.getConfig().getStringList("worlds");
        getLogger().log(Level.INFO, "Configured search worlds: {0}", confSearchWorlds);
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
        if(cmd.getName().equalsIgnoreCase("removeblocks") && args.length > 0) {
            if (args[0].equalsIgnoreCase("debug")) {
                if (sender.hasPermission("removeblocks.debug") || sender instanceof ConsoleCommandSender) {
                    this.debug = !this.debug;
                    sender.sendMessage("Toggled debugging for removeBlocks, now " + this.debug);
                }
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("removeblocks.reload") || sender instanceof ConsoleCommandSender) {
                    this.reloadConfig();
                    this.loadConfig();
                    sender.sendMessage("Reloaded configuration for removeblocks");
                }
                return true;
            } else if (args[0].equalsIgnoreCase("start")) {
                if (sender.hasPermission("removeblocks.start") || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Starting to check newly loaded chunks");
                    thisPlugin.getLogger().log(Level.INFO, "Plugin check activated");
                    chunkLoaderEnabled = true;
                }
                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (sender.hasPermission("removeblocks.stop") || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Stopping to check newly loaded chunks");
                    thisPlugin.getLogger().log(Level.INFO, "Plugin check deactivated");
                    chunkLoaderEnabled = false;
                }
                return true;                
            } else if (args[0].equalsIgnoreCase("checkloaded")) {
                if (sender.hasPermission("removeblocks.checkloaded") || sender instanceof ConsoleCommandSender) {
                    sender.sendMessage("Starting to check newly loaded chunks");
                    thisPlugin.getLogger().log(Level.INFO, "Checking all loaded Chunks");
                    new manageChunk().iterateLoadedChunks();
                }
                return true;
            }        
        }
        return false;
    }
    


    public void createCustomConfig() {
        confChunkListFile = new File(this.getDataFolder(), "chunkList.yml");
        if (!confChunkListFile.exists()) {
            if (Main.debug) {
                thisPlugin.getLogger().log(Level.INFO,"Creating new Chunklist file from template");
            }             
            confChunkListFile.getParentFile().mkdirs();
            this.saveResource("chunkList.yml", false);
        }

        confChunkListFileConfig = new YamlConfiguration();
        try {
            confChunkListFileConfig.load(confChunkListFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }     
    
    public void saveCustomConfig() {
        try {
            confChunkListFileConfig.save(confChunkListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }        
    }    
    
    @Override
    public void onDisable() {
        // invoke on disable to close the MySQL connection
        saveCustomConfig();
    }
}
