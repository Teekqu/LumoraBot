package eu.devload.twitch.modules.stats.commands.chatstats;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;
import java.util.List;

public class Messages implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("messages") && !command.equalsIgnoreCase("msg")) return;

        String userName = sender.getName();
        if(args.length > 0) userName = args[0].toLowerCase();

        String userId = sender.getId();
        if(!userName.equalsIgnoreCase(sender.getName())) {
            User user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), null, Collections.singletonList(userName)).execute().getUsers().getFirst();
            if(user != null) userId = user.getId();
        }

        int amount = StatsManager.getMessageCount(channel, userId);
        channel.sendMessage(userName+" has sent "+amount+" messages in this channel! | "+sender.getName());

    }
}
