package eu.devload.twitch.modules.ccommands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.ccommands.objects.CustomCommand;
import eu.devload.twitch.modules.ccommands.utils.CCManager;
import eu.devload.twitch.objects.TwitchChannel;

public class Command implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        if(!command.equalsIgnoreCase("command") && !command.equalsIgnoreCase("cmd")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length == 0) {
            channel.sendMessage("Usage: !cmd <add/remove/edit/alias> <command> <value/action> [alias] | "+sender.getName());
            return;
        }
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("add")) {
                channel.sendMessage("Usage: !cmd add <command> <value> | "+sender.getName());
                return;
            } else if(args[0].equalsIgnoreCase("remove")) {
                channel.sendMessage("Usage: !cmd remove <command> | "+sender.getName());
                return;
            } else if(args[0].equalsIgnoreCase("edit")) {
                channel.sendMessage("Usage: !cmd edit <command> <value> | "+sender.getName());
                return;
            } else if(args[0].equalsIgnoreCase("alias")) {
                channel.sendMessage("Usage: !cmd alias <command> <add/remove> <alias> | "+sender.getName());
                return;
            }
        }
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("create")) {
                channel.sendMessage("Usage: !cmd "+args[0].toLowerCase()+" "+args[1]+" <value> | "+sender.getName());
                return;
            } else if(args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("delete")) {
                String name = args[1];
                while(name.startsWith("!")) name = name.substring(1);
                CustomCommand cmd = CCManager.getCommand(channel.id(), name);
                if(cmd == null) {
                    channel.sendMessage("Command not found! | "+sender.getName());
                    return;
                }
                if(!cmd.hasPermission(sender.getId())) {
                    channel.sendMessage("You don't have permission to remove this command! | "+sender.getName());
                    return;
                }
                CCManager.removeCommand(channel.id(), cmd.name());
                if(cmd.isAlias()) {
                    channel.sendMessage("Successfully removed alias "+cmd.name()+" | "+sender.getName());
                    return;
                } else if(cmd.isCustom()) {
                    CCManager.getCommands(channel.id()).forEach(ccmd -> {
                        if(ccmd.isAlias() && ccmd.rawValue().equalsIgnoreCase(cmd.name())) CCManager.removeCommand(channel.id(), ccmd.name());
                    });
                    channel.sendMessage("Successfully removed command "+cmd.name()+" | "+sender.getName());
                    return;
                }
            } else if(args[0].equalsIgnoreCase("edit")) {
                channel.sendMessage("Usage: !cmd edit "+args[1]+" <value> | "+sender.getName());
                return;
            } else if(args[0].equalsIgnoreCase("alias")) {
                channel.sendMessage("Usage: !cmd alias "+args[1]+" <add/remove> <alias> | "+sender.getName());
                return;
            }
        }

        String action = args[0];
        String name = args[1];
        String value = args[2];
        if (!action.equalsIgnoreCase("alias")) value = String.join(" ", args).substring(action.length() + name.length() + 2);
        while(name.startsWith("!")) name = name.substring(1);

        if(action.equalsIgnoreCase("add") || action.equalsIgnoreCase("create")) {

            String finalName = name;
            if(CCManager.getCommand(channel.id(), name) != null || CCManager.getDefaultCommands().stream().anyMatch(cmd -> cmd.name().equalsIgnoreCase(finalName))) {
                channel.sendMessage("Command already exists! | "+sender.getName());
                return;
            }
            CustomCommand cmd = new CustomCommand(channel.id(), "custom", "default", name, value);
            CCManager.addCommand(cmd);
            channel.sendMessage("Successfully added custom command "+name+" | "+sender.getName());
            return;
        } else if(action.equalsIgnoreCase("remove") || action.equalsIgnoreCase("delete")) {
            CustomCommand cmd = CCManager.getCommand(channel.id(), name);
            if(cmd == null) {
                channel.sendMessage("Command not found! | "+sender.getName());
                return;
            }
            if(!cmd.hasPermission(sender.getId())) {
                channel.sendMessage("You don't have permission to remove this command! | "+sender.getName());
                return;
            }
            CCManager.removeCommand(channel.id(), cmd.name());
            if(cmd.isAlias()) {
                channel.sendMessage("Successfully removed alias "+cmd.name()+" | "+sender.getName());
                return;
            } else if(cmd.isCustom()) {
                CCManager.getCommands(channel.id()).forEach(ccmd -> {
                    if(ccmd.isAlias() && ccmd.rawValue().equalsIgnoreCase(cmd.name())) CCManager.removeCommand(channel.id(), ccmd.name());
                });
                channel.sendMessage("Successfully removed command "+cmd.name()+" | "+sender.getName());
                return;
            }
        } else if(action.equalsIgnoreCase("edit")) {
            CustomCommand cmd = CCManager.getCommand(channel.id(), name);
            if(cmd == null) {
                channel.sendMessage("Command not found! | "+sender.getName());
                return;
            }
            while(cmd.isAlias()) {
                cmd = CCManager.getCommand(channel.id(), cmd.rawValue());
                if(cmd == null) {
                    channel.sendMessage("Command not found! | "+sender.getName());
                    return;
                }
            }
            if(!cmd.hasPermission(sender.getId())) {
                channel.sendMessage("You don't have permission to edit this command! | "+sender.getName());
                return;
            }
            cmd.value(value);
            CCManager.updateCommand(cmd);
            channel.sendMessage("Successfully edited command "+cmd.name()+" | "+sender.getName());
            return;
        } else if(action.equalsIgnoreCase("alias")) {
            if(args.length < 4) {
                channel.sendMessage("Usage: !cmd alias "+name.toLowerCase()+" "+ value.toLowerCase()+" <alias> | "+sender.getName());
                return;
            }

            if(value.equalsIgnoreCase("add")) {

                CustomCommand cmd = CCManager.getCommand(channel.id(), name);
                if (cmd == null) {
                    channel.sendMessage("Command not found! | " + sender.getName());
                    return;
                }
                while (cmd.isAlias()) {
                    cmd = CCManager.getCommand(channel.id(), cmd.rawValue());
                    if (cmd == null) {
                        channel.sendMessage("Command not found! | " + sender.getName());
                        return;
                    }
                }
                if (!cmd.hasPermission(sender.getId())) {
                    channel.sendMessage("You don't have permission to add an alias to this command! | " + sender.getName());
                    return;
                }
                String alias = args[3];
                if (CCManager.getCommand(channel.id(), alias) != null) {
                    channel.sendMessage("Alias already exists! | " + sender.getName());
                    return;
                }
                CustomCommand aliasCmd = new CustomCommand(channel.id(), "alias", "default", alias, cmd.name());
                CCManager.addCommand(aliasCmd);
                channel.sendMessage("Successfully added alias " + alias + " to " + cmd.name() + " | " + sender.getName());
                return;

            } else if(value.equalsIgnoreCase("remove")) {

                String alias = args[3];
                CustomCommand cmd = CCManager.getCommand(channel.id(), alias);
                if (cmd == null) {
                    channel.sendMessage("Alias not found! | " + sender.getName());
                    return;
                }
                if (!cmd.hasPermission(sender.getId())) {
                    channel.sendMessage("You don't have permission to remove this alias! | " + sender.getName());
                    return;
                }
                CCManager.removeCommand(channel.id(), cmd.name());
                channel.sendMessage("Successfully removed alias " + cmd.name() + " | " + sender.getName());
                return;

            } else {
                channel.sendMessage("Usage: !cmd alias "+name.toLowerCase()+" "+ value.toLowerCase()+" <alias> | "+sender.getName());
                return;

            }

        } else {
            channel.sendMessage("Usage: !cmd <add/remove/edit/alias> <command> <value/action> [alias] | "+sender.getName());
            return;
        }

    }
}
