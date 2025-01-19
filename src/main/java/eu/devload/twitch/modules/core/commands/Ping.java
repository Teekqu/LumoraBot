package eu.devload.twitch.modules.core.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;
import java.util.Collections;

public class Ping implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("ping")) return;

        long time = System.currentTimeMillis();
        SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();
        long ping = System.currentTimeMillis() - time;
        float sPing = ping/1000f;
        String helixPing = "HelixAPI: " + ping + "ms ("+sPing+"s)";

        time = System.currentTimeMillis();
        channel.sendMessage("Checking irc ping...");
        ping = System.currentTimeMillis() - time;
        sPing = ping/1000f;
        String ircPing = "IRC: " + ping + "ms ("+sPing+"s)";

        time = System.currentTimeMillis();
        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM Channels WHERE channelId=" + channel.id());
            try { rs.close(); } catch (Exception err) { }
        } catch (Exception err) {
            err.printStackTrace();
        }
        ping = System.currentTimeMillis() - time;
        sPing = ping/1000f;
        String dbPing = "Database: " + ping + "ms ("+sPing+"s)";

        String message = "Pong! "+helixPing+" | "+ircPing+" | "+dbPing+" | "+sender.getName();
        channel.sendMessage(message);

    }
}
