package eu.devload.twitch.modules.stats.commands.chatstats;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class ResetMessages implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("reset-messages")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length < 1) {
            channel.sendMessage("Usage: !reset-messages <user>");
            return;
        }

        String user = args[0];
        UserList userList = SystemAPI.get().client().getHelix().getUsers(null, null, Collections.singletonList(user)).execute();
        if(userList.getUsers().isEmpty() || userList.getUsers() == null) {
            channel.sendMessage("User not found!");
            return;
        }

        User u = userList.getUsers().getFirst();
        StatsManager.resetMessageCount(channel, u);
        channel.sendMessage("Reset messages from "+u.getDisplayName()+"!");

    }
}
