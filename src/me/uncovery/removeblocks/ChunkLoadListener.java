package me.uncovery.removeblocks;

import java.util.List;
import java.util.logging.Level;
import static me.uncovery.removeblocks.Main.chunkLoaderEnabled;
import static me.uncovery.removeblocks.Main.thisPlugin;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import static me.uncovery.removeblocks.Main.confChunkListFileConfig;


public class ChunkLoadListener implements Listener{
    String chunkID;
    
    @EventHandler
    public void ChunkLoadEvent(ChunkLoadEvent event) {
        if (!chunkLoaderEnabled) {
            return;
        }
        
        Chunk thisChunk = event.getChunk();
        
        Integer cX = thisChunk.getX();
        Integer cZ = thisChunk.getZ();

        List chunkList = confChunkListFileConfig.getList("chunkList");
        
        World thisWorld = thisChunk.getWorld();
        String thisWorldName = thisWorld.getName();
        
        // we create a unique ID for the chunk in this world
        chunkID = thisWorldName + ":" + cX + "/" + cZ;
        
        // we check if we have a world that is in the config
        if (Main.confSearchWorlds.contains(thisWorldName)) {
            // we check if we have a new chunk
            //if (!chunkList.contains(chunkID)) {
                // iterate the chunk and replace
                Integer count = new manageChunk().iterateChunk(thisChunk);
                // chunkMap.put(chunkID, count);
                chunkList.add(chunkID);
            //} else {
            //    if (Main.debug) {
            //        thisPlugin.getLogger().log(Level.INFO, "Skipping already checked chunk world: {0}", chunkID);
            //    } 
            //}
        } else {
            if (Main.debug) {
                thisPlugin.getLogger().log(Level.INFO, "Skipping loaded chunk in ignored world: {0}", thisWorldName);
            } 
        }
        confChunkListFileConfig.set("chunkList", chunkList);
    }
}
