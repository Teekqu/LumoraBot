package eu.devload.twitch.modules.stats.events;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.interfaces.TwitchMessageEvent;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class MessageCount implements TwitchMessageEvent {
    @Override
    public void onMessage(TwitchChannel channel, EventUser user, String message) {
        if(StatsManager.isBlocked(channel, user.getId(), "chatstats")) return;
        UserList userList = SystemAPI.get().client().getHelix().getUsers(null, Collections.singletonList(user.getId()), null).execute();
        if(userList.getUsers().isEmpty() || userList.getUsers() == null) return;
        User u = userList.getUsers().getFirst();
        StatsManager.addMessageCount(channel, u, 1);
    }
}
