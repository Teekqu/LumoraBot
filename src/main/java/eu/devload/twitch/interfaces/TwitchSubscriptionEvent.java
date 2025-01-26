package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.objects.TwitchChannel;

import java.util.Objects;

public interface TwitchSubscriptionEvent {

    void onEvent(TwitchChannel channel, SubscriptionEvent event);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, e -> {
            if(!Objects.equals(e.getChannel().getId(), e.getSourceChannelId().orElse(e.getChannel().getId()))) return;
            TwitchChannel channel = CacheManager.get().getChannel(e.getChannel().getId());
            onEvent(channel, e);
        });
    }

}
