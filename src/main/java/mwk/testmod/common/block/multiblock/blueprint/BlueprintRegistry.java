package mwk.testmod.common.block.multiblock.blueprint;

import java.util.HashMap;
import java.util.Map;

import mwk.testmod.TestMod;
import net.minecraft.resources.ResourceLocation;

/**
 * A registry for multiblock blueprints.
 * @TODO Is this class necessary?
 */
public class BlueprintRegistry {
    
    // The singleton instance of the blueprint registry.
    private static BlueprintRegistry instance;

    // Map of blueprints by name.
    private Map<String, MultiBlockBlueprint> blueprints;
    
    private BlueprintRegistry() {
        blueprints = new HashMap<String, MultiBlockBlueprint>();
    }

    /**
     * Get the singleton instance of the blueprint registry.
     * 
     * @return The singleton instance of the blueprint registry.
     */
    public static synchronized BlueprintRegistry getInstance() {
        if (instance == null) {
            instance = new BlueprintRegistry();
        }
        return instance;
    }

    /**
     * Get the blueprint with the given name.
     * 
     * @param name The name of the blueprint to get.
     * @return The blueprint with the given name.
     *         Null if no blueprint with the given name exists.
     */
    public MultiBlockBlueprint getBlueprint(String name) {
        return blueprints.get(name);
    }

    /**
     * Register a blueprint.
     * 
     * @param name The name of the blueprint to register.
     * @param blueprint The blueprint to register.
     */
    public void registerBlueprint(String name, MultiBlockBlueprint blueprint) {
        blueprints.put(name, blueprint);
    }

    /**
     * Load a blueprint from a JSON file.
     * 
     * @param name The name of the blueprint to load.
     */
    public void loadBlueprint(String name) {
        ResourceLocation location = new ResourceLocation(
            TestMod.MODID, "blueprints/" + name + ".json");
        // Print the location for debugging.
        System.out.println(location);
        System.out.println(location.getPath());
    }

    public void load() {
        
    }
}
