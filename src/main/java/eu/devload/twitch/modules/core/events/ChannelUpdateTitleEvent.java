package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import com.github.twitch4j.events.ChannelViewerCountUpdateEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.utils.SystemAPI;

public class ChannelUpdateTitleEvent {

    public ChannelUpdateTitleEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelChangeTitleEvent.class, this::onAction);
    }

    private void onAction(ChannelChangeTitleEvent e) {
        LiveObject liveObject = new LiveObject(
                e.getChannel().getId(),
                e.getStream().getTitle(),
                e.getStream().getGameName(),
                e.getStream().getViewerCount()
        );
        CacheManager.get().setLiveChannel(liveObject);
    }

}
