package eu.devload.twitch.modules.stats.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.List;

public class Stats implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        List<String> triggers = List.of("stats", "top", "chatstats");
        if(!triggers.contains(command.toLowerCase())) return;

        channel.sendMessage("https://lumora.devload.eu/stats/" + channel.getName() + " | "+sender.getName());

    }
}
