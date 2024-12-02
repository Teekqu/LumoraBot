package eu.devload.twitch.interfaces;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import com.github.twitch4j.common.events.domain.EventUser;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;

public interface TwitchCommand {

    void execute(TwitchChannel channel, EventUser sender, String command, String[] args);
    default void execute(TwitchChannel channel, EventUser sender, String command) {
        execute(channel, sender, command, new String[0]);
    }

    default void register(SimpleEventHandler eventHandler) {
        eventHandler.onEvent(ChannelMessageEvent.class, e -> {
            if(!e.getMessage().startsWith("!")) return;
            if(e.getChannel().getId().equals(ClientUser.get().id())) return;
            String[] splits = e.getMessage().split(" ");
            String command = splits[0].substring(1);
            String[] args = new String[splits.length - 1];
            System.arraycopy(splits, 1, args, 0, args.length);
            TwitchChannel channel = new TwitchChannel(e.getChannel().getId());
            execute(channel, e.getUser(), command, args);
        });
    }

}
