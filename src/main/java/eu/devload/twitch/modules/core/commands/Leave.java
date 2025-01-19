package eu.devload.twitch.modules.core.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

public class Leave {

    public Leave() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelMessageEvent.class, this::onMessage);
    }

    public void onMessage(ChannelMessageEvent e) {
        if(!e.getChannel().getId().equals(ClientUser.get().id())) return;
        if(!e.getMessage().equalsIgnoreCase("!leave") && !e.getMessage().equalsIgnoreCase("!part")) return;

        boolean ch = SystemAPI.get().twitchManager().registeredChannels().stream().anyMatch(c -> c.id().equals(e.getUser().getId()));
        if(!ch) {
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "You are not registered! | "+e.getUser().getName());
            return;
        }

        try {
            boolean success = new TwitchChannel(e.getUser().getId()).leave();
            if(!success) {
                e.getTwitchChat().sendMessage(e.getChannel().getName(), "An error occurred while trying to leave the channel! | "+e.getUser().getName());
                return;
            }
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "Successfully leaved the channel! | "+e.getUser().getName());
        } catch (Exception err) {
            err.printStackTrace();
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "An error occurred while trying to leave the channel! | "+e.getUser().getName());
        }
    }
}
