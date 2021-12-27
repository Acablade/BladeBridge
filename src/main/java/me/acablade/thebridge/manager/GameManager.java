package me.acablade.thebridge.manager;

import lombok.Getter;
import me.acablade.thebridge.TheBridgePlugin;
import me.acablade.thebridge.objects.Arena;
import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.objects.gamestates.PreGameState;
import me.acablade.thebridge.utils.ArrayHelper;
import org.bukkit.entity.Player;

import java.util.*;


@Getter
public class GameManager {


    private static GameManager instance;
    private Set<Game> gameSet = new HashSet<>();


    private GameManager(){}



    public static GameManager getInstance(){
        if(instance==null){
            instance = new GameManager();
        }
        return instance;
    }


    public boolean init(TheBridgePlugin plugin, int gameNumber){

        if(gameSet.size() > 0) return false;

        Arena[] arenas = ArenaManager.getInstance().getArenaSet().toArray(new Arena[0]);

        Random random = new Random();

        for (int i = 0; i < gameNumber; i++) {

            Arena randomArena = arenas[random.nextInt(arenas.length)];

            Game game = new Game(randomArena, randomArena.getName()+"_"+System.currentTimeMillis(), plugin);
            game.init();
            game.getStateSeries().start();
            gameSet.add(game);
        }

        return true;
    }

    public boolean demoGame(Game game){
        game.init();
        game.getStateSeries().start();
        gameSet.add(game);
        return true;
    }

    public Game findGame() {

        return gameSet.stream().filter(game -> game.getStateSeries().getCurrentState() instanceof PreGameState && Arrays.stream(game.getPlayers()).filter(Objects::nonNull).toArray().length<2).findFirst().get();

    }

    public Game getGame(Player player){
        return getGame(player.getUniqueId());
    }

    public Game getGame(UUID uuid){
        return gameSet.stream().filter(game -> ArrayHelper.contains(game.getPlayers(),uuid)).findFirst().get();
    }


}
