package me.acablade.thebridge.objects;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
@Data
public class BoundingBox {

    private final Vector min;
    private final Vector max;

    public boolean contains(Vector pos){
        return pos.isInAABB(min,max);
    }

}
