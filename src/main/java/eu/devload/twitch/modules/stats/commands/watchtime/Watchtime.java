package eu.devload.twitch.modules.stats.commands.watchtime;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.Convert;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class Watchtime implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("watchtime") && !command.equalsIgnoreCase("wt")) return;

        String userName = sender.getName();
        if(args.length > 0) userName = args[0].toLowerCase();

        String userId = sender.getId();
        if(!userName.equalsIgnoreCase(sender.getName())) {
            User user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), null, Collections.singletonList(userName)).execute().getUsers().getFirst();
            if(user != null) userId = user.getId();
        }

        long minutes = StatsManager.getWatchtime(channel, userId);
        channel.sendMessage(userName+" has watched "+ Convert.secondsToFormat(minutes*60L)+" in this channel! | "+sender.getName());

    }
}
