package me.acablade.thebridge.objects;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.acablade.thebridge.TheBridgePlugin;
import me.acablade.thebridge.objects.gamestates.EndGameState;
import me.acablade.thebridge.objects.gamestates.PreGameState;
import me.acablade.thebridge.objects.gamestates.ingame.PlayingGameState;
import me.acablade.thebridge.objects.gamestates.ingame.WaitingGameState;
import me.acablade.thebridge.utils.ArrayHelper;
import me.acablade.thebridge.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@RequiredArgsConstructor
public class Game {


    /*
    index 0: team red
    index 1: team blue
     */
    private UUID[] players;

    private final Arena arena;
    private final String name;
    private final TheBridgePlugin plugin;

    private ItemStack[] items;
    private ItemStack[] armors;
    private ScheduledStateSeries stateSeries;

    private int redScore;
    private int blueScore;

    private Set<BlockState> oldBlockStates;

    public void init(){
        players = new UUID[2];
        stateSeries = new ScheduledStateSeries(plugin);
        stateSeries.add(new PreGameState(plugin,this));
        stateSeries.add(new PlayingGameState(plugin,this));
        stateSeries.add(new EndGameState(plugin,this));
        items = new ItemStack[]{
                ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,
                ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,
                ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,
                new ItemBuilder(Material.IRON_SWORD).withDisplayName("AAA").getItemStack(),new ItemBuilder(Material.BOW).withDisplayName("BUM").getItemStack(),ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY,ItemBuilder.EMPTY
        };

        armors = new ItemStack[]{
                ItemBuilder.EMPTY,
                new ItemBuilder(Material.LEATHER_CHESTPLATE).getItemStack(),
                new ItemBuilder(Material.LEATHER_LEGGINGS).getItemStack(),
                new ItemBuilder(Material.LEATHER_BOOTS).getItemStack()
        };
        oldBlockStates = new HashSet<>();
    }



    public void reset(){
        players = new UUID[2];
        oldBlockStates.clear();
    }

    public void addPlayer(Player player){
        int i = 0;
        while (players[i]!=null){
            i++;
        }
        players[i]=player.getUniqueId();

        player.teleport(getArena().getSpawnLocations()[i]);

    }

    public void removePlayer(Player player){
        if(stateSeries.getCurrentState() instanceof PlayingGameState || stateSeries.getCurrentState() instanceof WaitingGameState){
            end();
            return;
        }

        int index = ArrayHelper.indexOf(getPlayers(),player.getUniqueId());

        players[index] = null;
    }

    public void end(){
        getOldBlockStates().forEach(blockState -> {
            blockState.update(true);
        });
        getStateSeries().addNext(new PreGameState(plugin,this));
        reset();
    }




}
