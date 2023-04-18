package io.github.yuazer.zslotdrop.Commands;

import io.github.yuazer.zslotdrop.Main;
import io.github.yuazer.zslotdrop.Utils.YamlUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("zslotdrop")) {
            if (args.length == 0 || ("reload".equalsIgnoreCase(args[0]) && sender.hasPermission("zslotdrop.admin"))) {
                Main.getInstance().reloadConfig();
                sender.sendMessage(YamlUtils.getConfigMessage("Message.reload"));
                return true;
            }
        }
        return false;
    }
}
