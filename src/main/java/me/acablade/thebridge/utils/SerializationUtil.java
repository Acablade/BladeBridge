package me.acablade.thebridge.utils;

import me.acablade.thebridge.objects.BoundingBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class SerializationUtil {

    public static void writeBoundingBox(BoundingBox boundingBox, ConfigurationSection section){
        ConfigurationSection minSection = section.createSection("min");
        ConfigurationSection maxSection = section.createSection("max");
        writeVector(boundingBox.getMin(),minSection);
        writeVector(boundingBox.getMax(),maxSection);
    }


    public static void writeVector(Vector vector, ConfigurationSection section){
        section.set("x", vector.getBlockX());
        section.set("y", vector.getBlockY());
        section.set("z", vector.getBlockZ());
    }


    public static BoundingBox readBoundingBox(ConfigurationSection section){
        ConfigurationSection minSection = section.getConfigurationSection("min");
        ConfigurationSection maxSection = section.getConfigurationSection("max");

        return new BoundingBox(readVector(minSection),readVector(maxSection));
    }

    public static Vector readVector(ConfigurationSection section){
        return new Vector(section.getInt("x"),section.getInt("y"),section.getInt("z"));
    }

}
