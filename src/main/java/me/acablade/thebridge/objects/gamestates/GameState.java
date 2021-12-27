package me.acablade.thebridge.objects.gamestates;

import me.acablade.thebridge.objects.Game;
import me.acablade.thebridge.utils.Colorize;
import me.acablade.thebridge.utils.TitleUtil;
import net.minikloon.fsmgasm.State;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public abstract class GameState extends State implements Listener {

    protected final JavaPlugin plugin;
    protected final Game game;

    protected final Set<Listener> listeners = new HashSet<>();
    protected final Set<BukkitTask> tasks = new HashSet<>();

    public GameState(JavaPlugin plugin, Game game) {
        this.plugin = plugin;
        this.game = game;
    }

    @Override
    public final void start() {
        super.start();
        register(this);
    }

    @Override
    public final void end() {
        super.end();
        if(! super.getEnded())
            return;
        listeners.forEach(HandlerList::unregisterAll);
        tasks.forEach(BukkitTask::cancel);
        listeners.clear();
        tasks.clear();
    }

    protected final Game getGame() {
        return game;
    }

    protected final List<Player> getPlayers() {
        return (List<Player>) Arrays.stream(game.getPlayers()).map(Bukkit::getPlayer).filter(Objects::nonNull).collect(Collectors.toList());
    }

    protected final void broadcast(String message) {
        getPlayers().forEach(p -> p.sendMessage(message));
    }

    protected final void title(String message){
        title(message,null);
    }

    protected final void title(String title, String subtitle){
        getPlayers().forEach(p -> TitleUtil.sendTitle(p,Colorize.format(title),Colorize.format(subtitle),5,10,5, ChatColor.WHITE));
    }

    protected void register(Listener listener) {
        listeners.add(listener);
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    protected void debug(String message){
        System.out.println(message);
    }

    protected void schedule(Runnable runnable, long delay) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskLater(plugin, runnable, delay);
        tasks.add(task);
    }

    protected void scheduleRepeating(Runnable runnable, long delay, long interval) {
        BukkitTask task = plugin.getServer().getScheduler().runTaskTimer(plugin, runnable, delay, interval);
        tasks.add(task);
    }
}