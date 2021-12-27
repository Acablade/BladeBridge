package me.acablade.thebridge.objects.gamestates.ingame;

import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.objects.gamestates.GameState;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class WaitingGameState extends GameState {
    public WaitingGameState(JavaPlugin plugin, Game game) {
        super(plugin,game);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(5);
    }

    @Override
    protected void onEnd() {
        game.getStateSeries().addNext(new PlayingGameState(plugin,game));
    }

    @Override
    protected void onStart() {
        getPlayers().forEach(player -> player.setGameMode(GameMode.ADVENTURE));
    }

    @Override
    public void onUpdate() {

    }


    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        event.setTo(event.getFrom());
    }
}
