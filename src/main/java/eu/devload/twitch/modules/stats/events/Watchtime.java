package eu.devload.twitch.modules.stats.events;

import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;
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
                            HystrixCommand<UserList> userrequest = SystemAPI.get().client().getHelix().getUsers(null, Collections.singletonList(o), null);
                            if(userrequest == null) continue;
                            UserList users = userrequest.execute();
                            if(users == null || users.getUsers().isEmpty() || userrequest.isResponseTimedOut()) continue;
                            User user = users.getUsers().getFirst();

                            if(StatsManager.isBlocked(ch, user, "watchtime")) continue;
                            StatsManager.addWatchtime(ch, user, 5);
                        }

                        StatsManager.updateMaximalWatchtime(ch, 5);
                    }

                } catch (Exception ignored) {
                }
            }

        };

        timer.scheduleAtFixedRate(task, 0, ((interval)*60)*1000);
    }

}
