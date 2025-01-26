package eu.devload.twitch.modules.core.events;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.events.ChannelChangeGameEvent;
import com.github.twitch4j.events.ChannelChangeTitleEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.utils.SystemAPI;

public class ChannelUpdateGameEvent {

    public ChannelUpdateGameEvent() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelChangeGameEvent.class, this::onAction);
    }

    private void onAction(ChannelChangeGameEvent e) {
        LiveObject liveObject = new LiveObject(
                e.getChannel().getId(),
                e.getStream().getTitle(),
                e.getStream().getGameName(),
                e.getStream().getViewerCount()
        );
        CacheManager.get().setLiveChannel(liveObject);
    }

}
