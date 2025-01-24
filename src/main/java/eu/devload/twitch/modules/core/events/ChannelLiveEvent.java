package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.utils.SystemAPI;

public class ChannelLiveEvent {

    public ChannelLiveEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelGoLiveEvent.class, this::onChannelGoesLive);
    }

    private void onChannelGoesLive(ChannelGoLiveEvent e) {
        CacheManager.get().liveChannel(e.getChannel().getId());
    }

}
