package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.utils.SystemAPI;

public class ChannelOfflineEvent {

    public ChannelOfflineEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelGoOfflineEvent.class, this::onChannelGoesOffline);
    }

    private void onChannelGoesOffline(ChannelGoOfflineEvent e) {
        CacheManager.get().removeLiveChannel(e.getChannel().getId());
    }

}
