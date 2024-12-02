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
                String prefix = s.startsWith("${") ? "${" : s.startsWith("$(") ? "$(" : "{";
                String suffix = s.endsWith("}") ? "}" : ")";
                String key = s.substring(1, s.length() - 1);
                value = switch (key) {
                    case "channel" -> value.replaceAll(prefix+"channel"+suffix, channel.getName());
                    case "sender" -> value.replaceAll(prefix+"sender"+suffix, sender.getName());
                    case "user" -> value.replaceAll(prefix+"user"+suffix, user.getLogin());
                    case "channel.id" -> value.replaceAll(prefix+"channel.id"+suffix, channel.getId());
                    case "sender.id" -> value.replaceAll(prefix+"sender.id"+suffix, sender.getId());
                    case "user.id" -> value.replaceAll(prefix+"user.id"+suffix, user.getId());
                    case "channel.type" -> value.replaceAll(prefix+"channel.type"+suffix, channel.getBroadcasterType());
                    case "sender.type" -> value.replaceAll(prefix+"sender.type"+suffix, senderUser.getBroadcasterType());
                    case "args" -> value.replaceAll(prefix+"args"+suffix, String.join(" ", args));
                    case "channel.game" -> value.replaceAll(prefix+"channel.game"+suffix, isLive ? stream.getGameName() : "Unknown");
                    case "channel.title" -> value.replaceAll(prefix+"channel.title"+suffix, isLive ? stream.getTitle() : "Unknown");
                    case "channel.viewers" -> value.replaceAll(prefix+"channel.viewers"+suffix, isLive ? String.valueOf(stream.getViewerCount()) : "0");
                    case "channel.followers" -> value.replaceAll(prefix+"channel.followers"+suffix, String.valueOf(channel.getFollowers()));
                    case "channel.subs" -> value.replaceAll(prefix+"channel.subs"+suffix, String.valueOf(channel.getSubCount()));
                    case "channel.description" -> value.replaceAll(prefix+"channel.description"+suffix, channel.getDescription());
                    case "sender.followage" -> value.replaceAll(prefix+"sender.followage"+suffix, channel.getFollowAge(sender.getId()));
                    case "user.followage" -> value.replaceAll(prefix+"user.followage"+suffix, channel.getFollowAge(user.getId()));
                    case "channel.uptime" -> value.replaceAll(prefix+"channel.uptime"+suffix, isLive ? channel.getUptime() : "0");
                    case "channel.chatters" -> value.replaceAll(prefix+"channel.chatters"+suffix, String.valueOf(channel.getChatters().size()));
                    default -> value;
                };
            }
        }

        channel.sendMessage(value);

    }
}
