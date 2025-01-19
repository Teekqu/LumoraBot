package eu.devload.twitch.modules.ccommands.utils;

import eu.devload.twitch.manager.DefaultCommandManager;
import eu.devload.twitch.modules.ccommands.objects.CustomCommand;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CCManager {

    public static List<CustomCommand> getCommands(String channelId) {
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Commands WHERE channelId='" + channelId + "' AND type!='default'");
            List<CustomCommand> commands = new ArrayList<>();
            while(rs.next()) {
                commands.add(new CustomCommand(
                        rs.getString("channelId"),
                        rs.getString("type"),
                        rs.getString("permission"),
                        rs.getString("name"),
                        rs.getString("value")
                ));
            }
            try { rs.close(); } catch (Exception ignored) {}
            return commands;
        } catch (Exception err) {
            err.printStackTrace();
            return List.of();
        }
    }

    public static List<CustomCommand> getDefaultCommands() {
        return DefaultCommandManager.defaultCommands;
    }

    public static CustomCommand getCommand(String channelId, String name) {
        try {
            ResultSet rs1 = SystemAPI.get().database().query("SELECT * FROM Commands WHERE channelId='"+channelId+"' AND type!='default' AND name='" + name + "'");
            if(rs1.next()) {
                CustomCommand command = new CustomCommand(
                        rs1.getString("channelId"),
                        rs1.getString("type"),
                        rs1.getString("permission"),
                        rs1.getString("name"),
                        rs1.getString("value")
                );
                try { rs1.close(); } catch (Exception ignored) {}
                return command;
            }
            try { rs1.close(); } catch (Exception ignored) {}
            return null;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public static boolean addCommand(CustomCommand command) {
        if(getCommand(command.channelId(), command.name()) != null) return false;
        try {
            String stm = "INSERT INTO Commands (channelId, type, permission, name, value) VALUES ('" + command.channelId() + "', '" + command.type() + "', '" + command.permission() + "', '" + command.name() + "', '" + command.rawValue() + "');";
            SystemAPI.get().database().execute(stm);
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public static boolean removeCommand(String channelId, String name) {
        if(getCommand(channelId, name) == null) return false;
        try {
            SystemAPI.get().database().execute("DELETE FROM Commands WHERE channelId='"+channelId+"' AND name='" + name + "'");
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    public static boolean updateCommand(CustomCommand command) {
        if(getCommand(command.channelId(), command.name()) == null) return false;
        try {
            SystemAPI.get().database().execute("UPDATE Commands SET permission='" + command.permission() + "', value='" + command.value() + "' WHERE channelId='"+command.channelId()+"' AND name='" + command.name() + "'");
            return true;
        } catch (Exception err) {
            err.printStackTrace();
            return false;
        }
    }

}
