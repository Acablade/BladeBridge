package me.acablade.thebridge.objects;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.World;

@Data
@RequiredArgsConstructor
public class Arena {

    /*
    index 0: team red
    index 1: team blue
     */
    private final BoundingBox[] illegalAreas;
    private final BoundingBox[] goalAreas;
    private final Location[] spawnLocations;

    private final World world;
    private final int maxY;
    private final int minY;
    private final String name;


}
