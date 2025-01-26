package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.utils.SystemAPI;

public class ChannelUpdateViewerEvent {

    public ChannelUpdateViewerEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelViewerCountUpdateEvent.class, this::onChannelViewerCountUpdate);
    }

    private void onChannelViewerCountUpdate(ChannelViewerCountUpdateEvent e) {
        LiveObject liveObject = new LiveObject(
                e.getChannel().getId(),
                e.getStream().getTitle(),
                e.getStream().getGameName(),
                e.getStream().getViewerCount()
        );
        CacheManager.get().setLiveChannel(liveObject);
    }

}
