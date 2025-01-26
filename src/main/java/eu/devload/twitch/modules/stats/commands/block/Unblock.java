package eu.devload.twitch.modules.stats.commands.block;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.objects.UserObject;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class Unblock implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("unblock")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length < 1) {
            channel.sendMessage("Usage: !unblock <user> [<chatstats/watchtime>]");
            return;
        }

        String user = args[0];
        String type = args.length > 1 ? args[1] : "all";
        if(!type.equalsIgnoreCase("chatstats") && !type.equalsIgnoreCase("watchtime") && !type.equalsIgnoreCase("all")) {
            channel.sendMessage("Usage: !unblock <user> [<chatstats/watchtime>]");
            return;
        }

        UserObject u = CacheManager.get().getUserByName(user);
        if(u == null) {
            channel.sendMessage("User not found!");
            return;
        }

        if(type.equalsIgnoreCase("chatstats")) {
            StatsManager.unblock(channel, u.id(), "chatstats");
            channel.sendMessage("User " + u.displayName() + " has been unblocked from chatstats!");
        } else if(type.equalsIgnoreCase("watchtime")) {
            StatsManager.unblock(channel, u.id(), "watchtime");
            channel.sendMessage("User " + u.displayName() + " has been unblocked from watchtime!");
        } else if(type.equalsIgnoreCase("all")) {
            StatsManager.unblock(channel, u.id(), "chatstats");
            StatsManager.unblock(channel, u.id(), "watchtime");
            channel.sendMessage("User " + u.displayName() + " has been unblocked from chatstats and watchtime!");
        } else {
            channel.sendMessage("Usage: !unblock <user> [<chatstats/watchtime>]");
        }

    }
}
