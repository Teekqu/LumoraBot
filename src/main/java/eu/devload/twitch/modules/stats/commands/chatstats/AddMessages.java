package eu.devload.twitch.modules.stats.commands.chatstats;

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

public class AddMessages implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        if(!command.equalsIgnoreCase("add-messages")) return;
        if(!channel.isModerator(sender.getId())) return;

        if(args.length < 2) {
            channel.sendMessage("Usage: !add-messages <user> <amount>");
            return;
        }

        String user = args[0];
        int amount = Integer.parseInt(args[1]);

        UserObject tu = CacheManager.get().getUserByName(user);
        if(tu == null) {
            channel.sendMessage("User not found!");
            return;
        }

        StatsManager.addMessageCount(channel, tu, amount);
        channel.sendMessage("Added "+amount+" messages to "+tu.displayName()+"!");

    }
}
