package eu.devload.twitch.manager;

import eu.devload.twitch.modules.ccommands.objects.CustomCommand;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DefaultCommandManager {


    public static List<CustomCommand> defaultCommands = List.of(
            new CustomCommand("0", "default", "default", "command", "null"),
            new CustomCommand("0", "default", "default", "cmd", "null"),
            new CustomCommand("0", "default", "default", "watchtime", "null"),
            new CustomCommand("0", "default", "default", "wt", "null"),
            new CustomCommand("0", "default", "default", "messages", "null"),
            new CustomCommand("0", "default", "default", "msg", "null"),
            new CustomCommand("0", "default", "default", "add-minutes", "null"),
            new CustomCommand("0", "default", "default", "remove-minutes", "null"),
            new CustomCommand("0", "default", "default", "reset-minutes", "null"),
            new CustomCommand("0", "default", "default", "add-messages", "null"),
            new CustomCommand("0", "default", "default", "remove-messages", "null"),
            new CustomCommand("0", "default", "default", "reset-messages", "null"),
            new CustomCommand("0", "default", "default", "block", "null"),
            new CustomCommand("0", "default", "default", "unblock", "null"),
            new CustomCommand("0", "default", "default", "ping", "null"),
            new CustomCommand("0", "default", "default", "bot-stats", "null")
    );


    public static void updateDefaultCommandsInDatabase() {
        try {

            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Commands WHERE channelId='0' AND type='default'");
            List<String> existingCommands = new ArrayList<>();
            while(rs.next()) {
                String name = rs.getString("name");
                boolean exists = false;
                for(CustomCommand cmd : defaultCommands) {
                    if(cmd.name().equalsIgnoreCase(name)) {
                        exists = true;
                        existingCommands.add(name);
                        break;
                    }
                }
                if(!exists) {
                    SystemAPI.get().database().execute("DELETE FROM Commands WHERE channelId='0' AND type='default' AND name='" + name + "'");
                }
            }
            try { rs.close(); } catch (Exception ignored) {}

            for(CustomCommand cmd : defaultCommands) {
                if(existingCommands.contains(cmd.name())) continue;
                SystemAPI.get().database().execute("INSERT INTO Commands (channelId, type, permission, name, value) VALUES ('0', 'default', 'default', '" + cmd.name() + "', '" + cmd.value() + "')");
            }

        } catch (Exception err) {
            err.printStackTrace();
            return;
        }
    }

}
