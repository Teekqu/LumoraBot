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
        if(args.length > 0) userName = args[0];

        System.out.println("1");
        TwitchClient client = SystemAPI.get().client();
        System.out.println("2");
        TwitchHelix helix = client.getHelix();
        System.out.println("3");
        HystrixCommand<UserList> users = helix.getUsers(channel.oauth2(), null, Collections.singletonList(userName));
        System.out.println("4");
        System.out.println(users.isResponseRejected());
        System.out.println("5");
        UserList userList = users.execute();
        System.out.println("6");
        if(userList.getUsers().isEmpty() || userList.getUsers() == null) {
            channel.sendMessage("User not found!");
            return;
        }
        User user = userList.getUsers().getFirst();
        System.out.println("7");
        if(user == null) user = helix.getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        int amount = StatsManager.getMessageCount(channel, user);
        channel.sendMessage(user.getDisplayName()+" has sent "+amount+" messages in this channel! | "+sender.getName());

    }
}
