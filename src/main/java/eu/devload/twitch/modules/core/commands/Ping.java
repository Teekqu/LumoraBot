package eu.devload.twitch.modules.core.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class Ping implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("ping")) return;

        long time = System.currentTimeMillis();
        SystemAPI.get().client().getHelix().getUsers(channel.getOauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();
        long ping = System.currentTimeMillis() - time;
        float sPing = ping/1000f;

        channel.sendMessage("Pong! HelixAPI: " + ping + "ms ("+sPing+"s) | "+sender.getName());

    }
}
