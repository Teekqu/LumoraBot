package eu.devload.twitch.modules.core.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.utils.SystemAPI;

public class Support {

    public Support() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelMessageEvent.class, this::onMessage);
    }

    public void onMessage(ChannelMessageEvent e) {
        if(!e.getChannel().getId().equalsIgnoreCase(ClientUser.get().id())) return;
        if(!e.getMessage().equalsIgnoreCase("!support")) return;

        e.getTwitchChat().sendMessage(e.getChannel().getName(), "Discord: https://go.devload.eu/discord | Mail: support@devload.eu | "+e.getUser().getName());

    }

}
