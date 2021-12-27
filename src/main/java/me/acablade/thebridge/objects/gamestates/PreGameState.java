package me.acablade.thebridge.objects.gamestates;

import me.acablade.thebridge.TheBridgePlugin;
import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.utils.ArrayHelper;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PreGameState extends GameState{

    public PreGameState(JavaPlugin plugin, Game game) {
        super(plugin,game);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofMinutes(1);
    }

    @Override
    protected void onEnd() {
        /*if(getPlayers().size() < 2){
            game.getStateSeries().addNext(new PreGameState(plugin,game));
        }*/
    }

    @Override
    protected void onStart() {
        getPlayers().forEach(player -> player.setGameMode(GameMode.ADVENTURE));
    }

    @Override
    public void onUpdate() {

    }

    @EventHandler
    public void onJoin(PlayerChangedWorldEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()){
            schedule(() -> {
                player.teleport(((TheBridgePlugin)plugin).getLobby().getSpawnLocation());
            },1);
            return;
        }
        if(game.getPlayers().length==2){
            schedule(() -> {
                player.teleport(((TheBridgePlugin)plugin).getLobby().getSpawnLocation());
            },1);
            return;
        }
        ArrayHelper.add(game.getPlayers(),player.getUniqueId());
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        event.setCancelled(true);
    }

}
