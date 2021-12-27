package me.acablade.thebridge;

import com.google.common.base.Strings;
import dev.jcsoftware.jscoreboards.JGlobalMethodBasedScoreboard;
import dev.jcsoftware.jscoreboards.JPerPlayerScoreboard;
import dev.jcsoftware.jscoreboards.JScoreboardOptions;
import lombok.Getter;
import me.acablade.thebridge.commands.FreezeCommand;
import me.acablade.thebridge.commands.SkipCommand;
import me.acablade.thebridge.manager.GameManager;
import me.acablade.thebridge.objects.Arena;
import me.acablade.thebridge.objects.BoundingBox;
import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.utils.SerializationUtil;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Getter
public final class TheBridgePlugin extends JavaPlugin {


    private World lobby;
    private JPerPlayerScoreboard scoreboard;

    @Override
    public void onEnable() {
        // Plugin startup logic

        Vector blueSpawn = new Vector(-179,75,269);
        BoundingBox blueGoal = new BoundingBox(new Vector(-175,75,269), new Vector(-173,75,271));
        BoundingBox blueIllegalArea = new BoundingBox(new Vector(-177,75,267), new Vector(-171,75,273));

        Vector redSpawn = new Vector(-179,75,253);
        BoundingBox redGoal = new BoundingBox(new Vector(-184,74,249),new Vector(-182,74,251));
        BoundingBox redIllegalArea = new BoundingBox(new Vector(-186,74,247),new Vector(-180,74,253));

        World world = Bukkit.getWorlds().get(0);

        Arena arena = new Arena(
                new BoundingBox[]{redIllegalArea,blueIllegalArea},
                new BoundingBox[]{redGoal,blueGoal},
                new Location[]{
                        new Location(world,redSpawn.getX(),redSpawn.getY(),redSpawn.getZ()),
                        new Location(world,blueSpawn.getX(),blueSpawn.getY(),blueSpawn.getZ())
                },
                world,
                78,
                70,
                "AAA");
        GameManager.getInstance().demoGame(new Game(arena,"AAA",this));


        getCommand("freeze").setExecutor(new FreezeCommand());
        getCommand("skip").setExecutor(new SkipCommand());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yy");
        LocalDateTime now = LocalDateTime.now();

        scoreboard = new JPerPlayerScoreboard(
                (player) -> "&6THE BRIDGE",
                (player) -> {
                    Game game = GameManager.getInstance().getGame(player);

                    if(game==null)
                        return Arrays.asList(
                                "&7"+dtf.format(now),
                                "",
                                "&egithub.com/Acablade"
                        );

                    return Arrays.asList(
                            "&7"+dtf.format(now),
                            "",
                            game.getStateSeries().getCurrentState().getClass().getSimpleName(),
                            "",
                            "&9[M] " + getProgressBar(game.getBlueScore(), 5,5,'●',ChatColor.DARK_BLUE,ChatColor.GRAY),
                            "&c[K] " + getProgressBar(game.getRedScore(), 5,5,'●',ChatColor.RED,ChatColor.GRAY),
                            "",
                            "&egithub.com/Acablade"
                    );

                }, JScoreboardOptions.defaultOptions
        );

        getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onJoin(PlayerJoinEvent event){
                Game game = GameManager.getInstance().findGame();
                game.addPlayer(event.getPlayer());
                scoreboard.addPlayer(event.getPlayer());
            }

            @EventHandler
            public void onQuit(PlayerQuitEvent event){
                Game game = GameManager.getInstance().getGame(event.getPlayer());
                if(game!=null) game.removePlayer(event.getPlayer());
            }

            @EventHandler
            public void onChat(AsyncPlayerChatEvent event){
                Player player = event.getPlayer();
                World world = player.getWorld();

                // modify the recipients, this preserves the chat format set by other plugins
                event.getRecipients().clear();
                event.getRecipients().addAll(world.getPlayers()); //simple, heh
            }
        },this);







    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private String getProgressBar(int current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }

}
