package eu.devload.twitch.modules.stats.events;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.objects.UserObject;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Watchtime {

    private static final int interval = 5;

    public Watchtime() {
        Timer timer = new Timer();

        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                try {

                    for(TwitchChannel ch : SystemAPI.get().twitchManager().registeredChannels()) {
                        if(!ch.isLive()) continue;

                        List<String> chatters = ch.getChatters();
                        if(chatters == null) continue;

                        for(String o : chatters) {
                            if(o == null) continue;

                            UserObject us = CacheManager.get().getUserById(o);
                            if(us == null) {
                                HystrixCommand<UserList> userrequest = SystemAPI.get().client().getHelix().getUsers(null, Collections.singletonList(o), null);
                                if(userrequest == null) continue;
                                UserList users = userrequest.execute();
                                if(users == null || users.getUsers().isEmpty() || userrequest.isResponseTimedOut()) continue;
                                User user = users.getUsers().getFirst();
                                us = updateUserCache(user);
                            }

                            if(StatsManager.isBlocked(ch, us, "watchtime")) continue;
                            StatsManager.addWatchtime(ch, us, 5);
                        }

                        StatsManager.updateMaximalWatchtime(ch, 5);
                    }

                } catch (Exception ignored) {
                }
            }

        };

        timer.scheduleAtFixedRate(task, 0, ((interval)*60)*1000);
    }


    private UserObject updateUserCache(User user) {
        UserObject userObject = CacheManager.get().getUserById(user.getId());
        if(userObject == null) {
            UserList userList = SystemAPI.get().client().getHelix().getUsers(null, Collections.singletonList(user.getId()), null).execute();
            if(userList.getUsers().isEmpty() || userList.getUsers() == null) return null;
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
            userObject.login(user.getLogin());
            userObject.displayName(user.getDisplayName());
            userObject.broadcasterType(user.getBroadcasterType());
            userObject.description(user.getDescription());
            userObject.profileImageUrl(user.getProfileImageUrl());
            userObject.createdAt(user.getCreatedAt().getEpochSecond());
            CacheManager.get().setUser(userObject);
        }
        return userObject;
    }

}
