package me.uncovery.removeblocks;

import java.util.logging.Level;
import static me.uncovery.removeblocks.Main.thisPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

public class manageChunk {
    /**
     * We iterate a chunk and replace blocks
     * 
     * @param thisChunk
     * @return count
     */
    public Integer iterateChunk(Chunk thisChunk) {     
        // set a counter how many blocks we have found that match the list
        Integer count = 0;
        // we iterate the chunk coordinates
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                for (int y = 0; y < 128; y++) {
                    // get the block into a variable
                    Block thisBlock = thisChunk.getBlock(x, y, z);
                   
                    // get that blocks material
                    Material thisMat = thisBlock.getType();
                    // we iterate all the block types from the config
                    String thisMatString = thisMat.toString();
                    // we check if the above string is the stringlist of the config values
                    if (Main.confSearchMaterials.contains(thisMatString)) {
                        thisBlock.setType(Main.confTargetMaterial);
                        count++;
                        if (Main.debug) {
                            thisPlugin.getLogger().log(Level.INFO, "found {0} in {1}/{2}/{3}", new Object[]{thisMatString, thisBlock.getX(), thisBlock.getY(), thisBlock.getZ()});
                        }                               
                    }
                }
            }
        }
        // let's add the chunk to the list with the counted removals
        
        if (count > 0 && Main.debug) {
            // thisPlugin.getLogger().log(Level.INFO, "Replaced {0} blocks in Chunk", new Object[]{count}); 
        } else if (Main.debug) {
            // thisPlugin.getLogger().log(Level.INFO, "Checked chunk {0}, no blocks found", chunkID);
        }
        return count;
    }  
    
    /**
     * Iterate all loaded chunks in the configurated worlds
     * 
     * @return 
     */
    public Integer iterateLoadedChunks() {
        thisPlugin.getLogger().log(Level.INFO, "Iterating loaded Chunks in worlds " + Bukkit.getWorlds().toString());
        Integer count = 0;
        for (World currentWorld : Bukkit.getWorlds()) {
            thisPlugin.getLogger().log(Level.INFO, "checking world {0}", currentWorld.getName());
            if (Main.confSearchWorlds.contains(currentWorld.getName())) {
                if (Main.debug) {
                    thisPlugin.getLogger().log(Level.INFO, "Iterating world {0}", currentWorld.getName());
                }                
                for (Chunk currentChunk : currentWorld.getLoadedChunks()) {
                    Integer blockCount = iterateChunk(currentChunk);
                    count++;
                }
            }
        }
        return count;
    }
}
