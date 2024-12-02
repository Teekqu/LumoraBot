package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.chat.events.channel.SubscriptionEvent;
import eu.devload.twitch.objects.TwitchChannel;

public interface TwitchSubscriptionEvent {

    void onEvent(TwitchChannel channel, SubscriptionEvent event);

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(SubscriptionEvent.class, e -> {
            TwitchChannel channel = new TwitchChannel(e.getChannel().getId());
            onEvent(channel, e);
        });
    }

}
