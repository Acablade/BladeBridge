package me.acablade.thebridge.commands;

import me.acablade.thebridge.manager.GameManager;
import me.acablade.thebridge.objects.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkipCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("thebridge.skip")) return false;
        if(!(sender instanceof Player)) return false;

        Player player = (Player) sender;

        Game game = GameManager.getInstance().getGame(player);
        game.getStateSeries().skip();

        player.sendMessage("sa");
        player.sendMessage(game.getStateSeries().getCurrentState().getClass().getSimpleName());

        return true;
    }
}
