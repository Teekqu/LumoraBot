package eu.devload.twitch.modules.stats.commands.watchtime;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class RemoveMinutes implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("remove-minutes")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length < 2) {
            channel.sendMessage("Usage: !remove-minutes <user> <amount>");
            return;
        }

        String user = args[0];
        int amount = Integer.parseInt(args[1]);

        UserList userList = SystemAPI.get().client().getHelix().getUsers(null, null, Collections.singletonList(user)).execute();
        if(userList.getUsers().isEmpty() || userList.getUsers() == null) {
            channel.sendMessage("User not found!");
            return;
        }

        User u = userList.getUsers().getFirst();
        if(StatsManager.getWatchtime(channel, u.getId()) < amount) {
            StatsManager.resetWatchtime(channel, u.getId());
            channel.sendMessage("Resetted watchtime from "+u.getDisplayName()+"!");
            return;
        }

        StatsManager.removeWatchtime(channel, u, amount);
        channel.sendMessage("Removed "+amount+" minutes from "+u.getDisplayName()+"!");

    }
}
