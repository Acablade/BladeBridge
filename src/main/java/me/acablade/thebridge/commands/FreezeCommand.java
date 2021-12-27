package me.acablade.thebridge.commands;

import me.acablade.thebridge.manager.GameManager;
import me.acablade.thebridge.objects.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("thebridge.freeze")) return false;
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        Game game = GameManager.getInstance().getGame(player);

        game.getStateSeries().setFrozen(true);
        return true;
    }
}
