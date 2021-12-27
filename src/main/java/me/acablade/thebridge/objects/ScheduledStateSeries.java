package me.acablade.thebridge.objects;

import me.acablade.thebridge.TheBridgePlugin;
import net.minikloon.fsmgasm.State;
import net.minikloon.fsmgasm.StateSeries;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.List;

public class ScheduledStateSeries extends StateSeries {
    private final Plugin plugin;
    private final long interval;
    protected BukkitTask scheduledTask;

    protected List<Runnable> onUpdate = new LinkedList<>();

    public ScheduledStateSeries(Plugin plugin) {
        this(plugin, 1);
    }

    public ScheduledStateSeries(Plugin plugin, long interval) {
        this.plugin = plugin;
        this.interval = interval;
    }

    @Override
    public final void onStart() {
        super.onStart();
        scheduledTask = plugin.getServer().getScheduler().runTaskTimer(plugin, () -> {
            update();
            onUpdate.forEach(Runnable::run);
        }, 0L, interval);
    }

    @Override
    public final void onEnd() {
        super.onEnd();
        scheduledTask.cancel();
    }


    @Override
    public void onUpdate() {
        super.onUpdate();
        ((TheBridgePlugin)plugin).getScoreboard().updateScoreboard();
    }

    public final void addOnUpdate(Runnable runnable) {
        onUpdate.add(runnable);
    }

    public final State getCurrentState(){
        return getStates().get(getCurrent());
    }
}