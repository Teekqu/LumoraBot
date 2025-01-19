package eu.devload.twitch.manager;

import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.ArrayList;
import java.util.List;

public class CacheManager {

    private static CacheManager instance;
    public static CacheManager get() {
        if(instance == null) instance = new CacheManager();
        return instance;
    }

    private List<TwitchChannel> channels = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public void twitchChannel(TwitchChannel channel) {
        if(channels.contains(channel)) channels.set(channels.indexOf(channel), channel);
        else channels.add(channel);
    }
    public TwitchChannel twitchChannel(String id) {
        TwitchChannel ch = channels.stream().filter(c -> c.id().equals(String.valueOf(id))).findFirst().orElse(new TwitchChannel(String.valueOf(id)));
        if(ch.getLatestUpdate()-System.currentTimeMillis()/1000 > 600) ch.update();
        return ch;
    }

}
