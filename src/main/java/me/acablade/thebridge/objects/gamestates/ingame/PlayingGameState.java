package me.acablade.thebridge.objects.gamestates.ingame;

import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.objects.gamestates.GameState;
import me.acablade.thebridge.utils.ArrayHelper;
import me.acablade.thebridge.utils.Colorize;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PlayingGameState extends GameState {
    public PlayingGameState(JavaPlugin plugin, Game game) {
        super(plugin,game);
    }

    @NotNull
    @Override
    public Duration getDuration() {
        return Duration.ofMinutes(5);
    }

    @Override
    protected void onEnd() {
        if(game.getBlueScore() < 5 && game.getRedScore() < 5){
            game.getStateSeries().addNext(new WaitingGameState(plugin,game));
        }
    }

    @Override
    protected void onStart() {

        Player redPlayer = getPlayers().get(0);
        Player bluePlayer = getPlayers().get(1);

        redPlayer.setPlayerListName(ChatColor.DARK_RED + "" + ChatColor.BOLD + "K " + ChatColor.RED + redPlayer.getDisplayName());
        bluePlayer.setPlayerListName(ChatColor.DARK_BLUE + "" + ChatColor.BOLD + "M " + ChatColor.BLUE + bluePlayer.getDisplayName());

        //TELEPORT
        redPlayer.teleport(game.getArena().getSpawnLocations()[0]);
        bluePlayer.teleport(game.getArena().getSpawnLocations()[1]);

        //ITEMS
        redPlayer.getInventory().setContents(game.getItems());
        redPlayer.getInventory().setArmorContents(game.getArmors());

        bluePlayer.getInventory().setContents(game.getItems());
        bluePlayer.getInventory().setArmorContents(game.getArmors());

        redPlayer.updateInventory();
        bluePlayer.updateInventory();

    }

    @Override
    public void onUpdate() {

    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        Block block = event.getBlockPlaced();
        if(game.getArena().getMinY() > block.getY() || block.getY() > game.getArena().getMaxY()){
            event.setCancelled(true);
            return;
        }
        if(game.getArena().getIllegalAreas()[0].contains(block.getLocation().toVector()) || game.getArena().getIllegalAreas()[1].contains(block.getLocation().toVector())){
            event.setCancelled(true);
            return;
        }

        game.getOldBlockStates().add(event.getBlockReplacedState());

    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        Block block = event.getBlock();
        if(block.getType().name().contains("CLAY")){
            event.setCancelled(true);
            return;
        }
        game.getOldBlockStates().add(event.getBlock().getState());

    }

    @EventHandler
    public void onDie(PlayerDeathEvent event){
        Player player = event.getEntity();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        schedule(() -> {
            player.spigot().respawn();
        }, 3);
    }
    @EventHandler
    public void onRespawn(PlayerRespawnEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;

        int team = ArrayHelper.indexOf(game.getPlayers(), player.getUniqueId());

        event.setRespawnLocation(game.getArena().getSpawnLocations()[team]);
        player.setGameMode(GameMode.SPECTATOR);

        player.sendTitle(Colorize.format("&cÖLDÜN!"), Colorize.format("&e5 SANİYE SONRA DOĞACAKSIN"));

        schedule(() -> {
            player.setGameMode(GameMode.SURVIVAL);
            player.teleport(game.getArena().getSpawnLocations()[team]);
        },20*5L);
    }

    @EventHandler
    public void onRegen(EntityRegainHealthEvent event){
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        event.setCancelled(event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(player.getWorld()!=game.getArena().getWorld()) return;
        
        Location to = event.getTo();
        Location from = event.getFrom();
        if(to.getBlockX()==from.getBlockX() && to.getBlockY()==from.getBlockY() && to.getBlockZ()==from.getZ()) return;

        //IF PLAYER IS IN TEAM RED
        if(ArrayHelper.indexOf(getGame().getPlayers(), player.getUniqueId())==0){
            if(getGame().getArena().getGoalAreas()[1].contains(to.toVector())){
                getGame().setRedScore(getGame().getRedScore()+1);
                game.getStateSeries().skip();
                title("&cKIRMIZI","&eskor yaptı!");
            }
        }else{
            if(getGame().getArena().getGoalAreas()[0].contains(to.toVector())){
                getGame().setBlueScore(getGame().getBlueScore()+1);
                game.getStateSeries().skip();
                title("&9MAVİ","&eskor yaptı!");
            }
        }



    }


}
