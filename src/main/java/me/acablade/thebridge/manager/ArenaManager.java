package me.acablade.thebridge.manager;

import lombok.Getter;
import me.acablade.thebridge.TheBridgePlugin;
import me.acablade.thebridge.objects.Arena;
import me.acablade.thebridge.objects.BoundingBox;
import me.acablade.thebridge.objects.configs.ConfigurationFile;
import me.acablade.thebridge.utils.SerializationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class ArenaManager {

    private static ArenaManager instance;
    private ArenaManager(){}

    public static ArenaManager getInstance() {
        return instance;
    }

    @Getter
    private Set<Arena> arenaSet = new HashSet<>();

    public void init(TheBridgePlugin plugin){
        File directory = new File(plugin.getDataFolder()+"/maps");
        if(!directory.isDirectory()){
            directory.mkdir();
            return;
        }

        for (File file : directory.listFiles()){
            if(!file.isFile()) continue;
            String[] fileName = file.getName().split("\\.");
            if(!fileName[1].equalsIgnoreCase("yml")) continue;
            arenaSet.add(read(plugin,fileName[0]));
        }

    }

    public void write(TheBridgePlugin plugin, Arena arena){
        ConfigurationFile configurationFile = new ConfigurationFile(plugin,"maps/"+arena.getName());
        configurationFile.getConfiguration().set("world",arena.getWorld().getName());
        configurationFile.getConfiguration().set("maxY",arena.getMaxY());
        configurationFile.getConfiguration().set("minY",arena.getMinY());

        //BLUE TEAM
        ConfigurationSection blueSection = configurationFile.getConfiguration().createSection("blue");
        SerializationUtil.writeVector(arena.getSpawnLocations()[1].toVector(),blueSection.createSection("spawn"));
        SerializationUtil.writeBoundingBox(arena.getGoalAreas()[1], blueSection.createSection("goal"));
        SerializationUtil.writeBoundingBox(arena.getIllegalAreas()[1], blueSection.createSection("goalProtectionZone"));

        // RED TEAM
        ConfigurationSection redSection = configurationFile.getConfiguration().createSection("red");
        SerializationUtil.writeVector(arena.getSpawnLocations()[0].toVector(),redSection.createSection("spawn"));
        SerializationUtil.writeBoundingBox(arena.getGoalAreas()[0], redSection.createSection("goal"));
        SerializationUtil.writeBoundingBox(arena.getIllegalAreas()[0], redSection.createSection("goalProtectionZone"));
    }

    public Arena read(TheBridgePlugin plugin, String name){

        ConfigurationFile configurationFile = new ConfigurationFile(plugin,"maps/"+name);
        YamlConfiguration configuration = configurationFile.getConfiguration();

        int maxY = configuration.getInt("maxY");
        int minY = configuration.getInt("minY");
        String worldName = configuration.getString("world");

        ConfigurationSection blueSection = configuration.getConfigurationSection("blue");
        Vector blueSpawn = SerializationUtil.readVector(blueSection.getConfigurationSection("spawn"));
        BoundingBox blueGoal = SerializationUtil.readBoundingBox(blueSection.getConfigurationSection("goal"));
        BoundingBox blueIllegalArea = SerializationUtil.readBoundingBox(blueSection.getConfigurationSection("goalProtectionZone"));

        ConfigurationSection redSection = configuration.getConfigurationSection("red");
        Vector redSpawn = SerializationUtil.readVector(redSection.getConfigurationSection("spawn"));
        BoundingBox redGoal = SerializationUtil.readBoundingBox(redSection.getConfigurationSection("goal"));
        BoundingBox redIllegalArea = SerializationUtil.readBoundingBox(redSection.getConfigurationSection("goalProtectionZone"));

        World world = Bukkit.createWorld(new WorldCreator(worldName));

        return new Arena(
                new BoundingBox[]{redIllegalArea,blueIllegalArea},
                new BoundingBox[]{redGoal,blueGoal},
                new Location[]{
                        new Location(world,redSpawn.getX(),redSpawn.getY(),redSpawn.getZ()),
                        new Location(world,blueSpawn.getX(),blueSpawn.getY(),blueSpawn.getZ())
                },
                world,
                maxY,
                minY,
                name);

    }

}
