package eu.devload.twitch.modules.stats.commands.block;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
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

        UserList userList = SystemAPI.get().client().getHelix().getUsers(null, null, Collections.singletonList(user)).execute();
        if(userList.getUsers().isEmpty() || userList.getUsers() == null) {
            channel.sendMessage("User not found!");
            return;
        }

        User u = userList.getUsers().getFirst();

        if(type.equalsIgnoreCase("chatstats")) {
            StatsManager.unblock(channel, u, "chatstats");
            channel.sendMessage("User " + u.getDisplayName() + " has been unblocked from chatstats!");
        } else if(type.equalsIgnoreCase("watchtime")) {
            StatsManager.unblock(channel, u, "watchtime");
            channel.sendMessage("User " + u.getDisplayName() + " has been unblocked from watchtime!");
        } else if(type.equalsIgnoreCase("all")) {
            StatsManager.unblock(channel, u, "chatstats");
            StatsManager.unblock(channel, u, "watchtime");
            channel.sendMessage("User " + u.getDisplayName() + " has been unblocked from chatstats and watchtime!");
        } else {
            channel.sendMessage("Usage: !unblock <user> [<chatstats/watchtime>]");
        }

    }
}
