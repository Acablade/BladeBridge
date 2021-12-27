package me.acablade.thebridge.objects.gamestates;

import me.acablade.thebridge.objects.Game;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class EndGameState extends GameState{
    public EndGameState(JavaPlugin plugin, Game game) {
        super(plugin, game);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofSeconds(10);
    }

    @Override
    protected void onEnd() {
        game.end();
    }

    @Override
    protected void onStart() {
        String winner = "&6&lBERABERE!";
        if(game.getRedScore() > game.getBlueScore()){
            winner = "&4&lKIRMIZI!";
        }else if(game.getBlueScore() > game.getRedScore()){
            winner = "&9&lMAVİ!";
        }
        title(winner, "&eKAZANAN");
    }

    @Override
    public void onUpdate() {

    }
}
