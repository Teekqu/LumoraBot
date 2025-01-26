package eu.devload.twitch.modules.stats.commands.watchtime;

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

public class ResetMinutes implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("reset-minutes")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length < 1) {
            channel.sendMessage("Usage: !reset-minutes <user>");
            return;
        }

        String user = args[0];
        UserObject u = CacheManager.get().getUserByName(user);
        if(u == null) {
            channel.sendMessage("User not found!");
            return;
        }

        StatsManager.resetWatchtime(channel, u.id());
        channel.sendMessage("Resetted watchtime from "+u.displayName()+"!");

    }
}
