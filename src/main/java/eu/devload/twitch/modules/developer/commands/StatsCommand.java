package eu.devload.twitch.modules.developer.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.developer.manager.DevManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

public class StatsCommand implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("bot-stats")) return;

        if(!DevManager.isDeveloper(sender.getId())) return;

        int channels = SystemAPI.get().client().getChat().getChannels().size()-1;

        channel.sendMessage("Active Channels: " + channels + " | "+sender.getName());

    }
}
