package eu.devload.twitch.modules.core.commands;

import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.sql.ResultSet;

public class Join {

    public Join() {
        SimpleEventHandler eventHandler = SystemAPI.get().eventHandler();
        eventHandler.onEvent(ChannelMessageEvent.class, this::onMessage);
    }

    public void onMessage(ChannelMessageEvent e) {
        if(!e.getChannel().getId().equalsIgnoreCase(ClientUser.get().id())) return;
        if(!e.getMessage().equalsIgnoreCase("!join")) return;

        boolean ch = SystemAPI.get().twitchManager().registeredChannels().stream().anyMatch(c -> c.id().equals(e.getUser().getId()));
        if(ch) {
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "You are already registered! | !leave to leave the channel. | "+e.getUser().getName());
            return;
        }

        try {
            ResultSet rs = SystemAPI.get().database().query("SELECT * FROM OauthTokens WHERE id='" + e.getUser().getId() + "'");
            if (!rs.next()) {
                try { rs.close(); } catch (Exception ignored) { }
                e.getTwitchChat().sendMessage(e.getChannel().getName(), "Register to add the bot to your channel: https://lumora.devload.eu/register | "+e.getUser().getName());
                return;
            }
            try { rs.close(); } catch (Exception ignored) { }
            boolean success = SystemAPI.get().twitchManager().addChannel(new TwitchChannel(e.getUser().getId()));
            if(!success) {
                e.getTwitchChat().sendMessage(e.getChannel().getName(), "An error occurred while trying to join the channel! | "+e.getUser().getName());
                return;
            }
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "Successfully joined the channel! | "+e.getUser().getName());
        } catch (Exception err) {
            err.printStackTrace();
            e.getTwitchChat().sendMessage(e.getChannel().getName(), "An error occurred while trying to join the channel! | "+e.getUser().getName());
        }
    }
}
