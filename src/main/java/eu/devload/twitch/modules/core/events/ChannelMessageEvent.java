package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.UserObject;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class ChannelMessageEvent {

    public ChannelMessageEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(com.github.twitch4j.chat.events.channel.ChannelMessageEvent.class, this::onMessage);
    }

    private void onMessage(com.github.twitch4j.chat.events.channel.ChannelMessageEvent e) {

        EventUser user = e.getUser();
        UserObject userObject = CacheManager.get().getUserById(user.getId());
        if(userObject == null) {
            UserList userList = SystemAPI.get().client().getHelix().getUsers(null, Collections.singletonList(user.getId()), null).execute();
            if(userList.getUsers().isEmpty() || userList.getUsers() == null) return;
            User u = userList.getUsers().getFirst();
            userObject = new UserObject(
                    u.getId(),
                    u.getLogin(),
                    u.getDisplayName(),
                    u.getBroadcasterType(),
                    u.getDescription(),
                    u.getProfileImageUrl(),
                    u.getCreatedAt().getEpochSecond()
            );
            CacheManager.get().setUser(userObject);
        } else {
            userObject.login(user.getName());
            CacheManager.get().setUser(userObject);
        }

    }

}
