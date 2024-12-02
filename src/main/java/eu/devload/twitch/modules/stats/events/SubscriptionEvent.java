package eu.devload.twitch.modules.stats.events;

import eu.devload.twitch.interfaces.TwitchSubscriptionEvent;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.TwitchChannel;

public class SubscriptionEvent implements TwitchSubscriptionEvent {
    @Override
    public void onEvent(TwitchChannel channel, com.github.twitch4j.chat.events.channel.SubscriptionEvent event) {

        int subCount = channel.getSubCount();
        StatsManager.updateHighestSubCount(channel, subCount, true);

    }
}
