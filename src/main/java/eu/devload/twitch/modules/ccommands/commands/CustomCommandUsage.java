package eu.devload.twitch.modules.ccommands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.modules.ccommands.objects.CustomCommand;
import eu.devload.twitch.modules.ccommands.utils.CCManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class CustomCommandUsage implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {

        CustomCommand cmd = CCManager.getCommand(channel.getId(), command);
        if(cmd == null || cmd.isDefault()) return;
        if(!cmd.hasPermission(sender.getId())) return;

        String userName = sender.getName();
        if(args.length > 0) {
            userName = args[0];
        }
        User user = SystemAPI.get().client().getHelix().getUsers(channel.getOauth2(), null, Collections.singletonList(userName)).execute().getUsers().getFirst();
        if(user == null) user = SystemAPI.get().client().getHelix().getUsers(channel.getOauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        User senderUser = user.getId().equals(sender.getId()) ? user : SystemAPI.get().client().getHelix().getUsers(channel.getOauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        Stream stream = channel.getStream();
        boolean isLive = stream != null && stream.getStartedAtInstant().getEpochSecond() > System.currentTimeMillis() / 1000 - 60 * 60;

        String value = cmd.value();
        String[] splitted = value.split(" ");

        for(String s : splitted) {
            if((s.startsWith("{") && s.endsWith("}")) || (s.startsWith("$(") && s.endsWith(")")) || (s.startsWith("${") && s.endsWith("}"))) {
                String key = s.substring(1, s.length() - 1);
                value = switch (key) {
                    case "channel" -> value.replaceAll("\\{channel}", channel.getName());
                    case "sender" -> value.replaceAll("\\{sender}", sender.getName());
                    case "user" -> value.replaceAll("\\{user}", user.getLogin());
                    case "channel.id" -> value.replaceAll("\\{channel.id}", channel.getId());
                    case "sender.id" -> value.replaceAll("\\{sender.id}", sender.getId());
                    case "user.id" -> value.replaceAll("\\{user.id}", user.getId());
                    case "channel.type" -> value.replaceAll("\\{channel.type}", channel.getBroadcasterType());
                    case "sender.type" -> value.replaceAll("\\{sender.type}", senderUser.getBroadcasterType());
                    case "args" -> value.replaceAll("\\{args}", String.join(" ", args));
                    case "channel.game" -> value.replaceAll("\\{channel.game}", isLive ? stream.getGameName() : "Unknown");
                    case "channel.title" -> value.replaceAll("\\{channel.title}", isLive ? stream.getTitle() : "Unknown");
                    case "channel.viewers" -> value.replaceAll("\\{channel.viewers}", isLive ? String.valueOf(stream.getViewerCount()) : "0");
                    case "channel.followers" -> value.replaceAll("\\{channel.followers}", String.valueOf(channel.getFollowers()));
                    case "channel.subs" -> value.replaceAll("\\{channel.subs}", String.valueOf(channel.getSubCount()));
                    case "channel.description" -> value.replaceAll("\\{channel.description}", channel.getDescription());
                    case "sender.followage" -> value.replaceAll("\\{sender.followage}", channel.getFollowAge(sender.getId()));
                    case "user.followage" -> value.replaceAll("\\{user.followage}", channel.getFollowAge(user.getId()));
                    case "channel.uptime" -> value.replaceAll("\\{channel.uptime}", isLive ? channel.getUptime() : "0");
                    case "channel.chatters" -> value.replaceAll("\\{channel.chatters}", String.valueOf(channel.getChatters().size()));
                    default -> value;
                };
            }
        }

        channel.sendMessage(value);

    }
}
