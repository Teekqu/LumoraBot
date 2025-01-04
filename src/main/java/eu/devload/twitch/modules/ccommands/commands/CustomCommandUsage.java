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
        User user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), null, Collections.singletonList(userName)).execute().getUsers().getFirst();
        if(user == null) user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        User senderUser = user.getId().equals(sender.getId()) ? user : SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        Stream stream = channel.getStream();
        boolean isLive = stream != null && stream.getStartedAtInstant().getEpochSecond() > System.currentTimeMillis() / 1000 - 60 * 60;

        String value = cmd.value();
        StringBuilder sb = new StringBuilder();
        String[] splitted = value.split(" ");

        for(String s : splitted) {
            if(s.contains("{channel}")) sb.append(s.replaceAll("\\{channel}", channel.getName())).append(" ");
            else if(s.contains("{sender}")) sb.append(s.replaceAll("\\{sender}", sender.getName())).append(" ");
            else if(s.contains("{user}")) sb.append(s.replaceAll("\\{user}", user.getLogin())).append(" ");
            else if(s.contains("{channel.id}")) sb.append(s.replaceAll("\\{channel.id}", channel.getId())).append(" ");
            else if(s.contains("{sender.id}")) sb.append(s.replaceAll("\\{sender.id}", sender.getId())).append(" ");
            else if(s.contains("{user.id}")) sb.append(s.replaceAll("\\{user.id}", user.getId())).append(" ");
            else if(s.contains("{channel.type}")) sb.append(s.replaceAll("\\{channel.type}", channel.getBroadcasterType())).append(" ");
            else if(s.contains("{sender.type}")) sb.append(s.replaceAll("\\{sender.type}", senderUser.getBroadcasterType())).append(" ");
            else if(s.contains("{args}")) sb.append(s.replaceAll("\\{args}", String.join(" ", args))).append(" ");
            else if(s.contains("{channel.game}")) sb.append(s.replaceAll("\\{channel.game}", isLive ? stream.getGameName() : "Unknown")).append(" ");
            else if(s.contains("{channel.title}")) sb.append(s.replaceAll("\\{channel.title}", isLive ? stream.getTitle() : "Unknown")).append(" ");
            else if(s.contains("{channel.viewers}")) sb.append(s.replaceAll("\\{channel.viewers}", isLive ? String.valueOf(stream.getViewerCount()) : "0")).append(" ");
            else if(s.contains("{channel.followers}")) sb.append(s.replaceAll("\\{channel.followers}", String.valueOf(channel.getFollowers()))).append(" ");
            else if(s.contains("{channel.subs}")) sb.append(s.replaceAll("\\{channel.subs}", String.valueOf(channel.getSubCount()))).append(" ");
            else if(s.contains("{channel.description}")) sb.append(s.replaceAll("\\{channel.description}", channel.getDescription())).append(" ");
            else if(s.contains("{sender.followage}")) sb.append(s.replaceAll("\\{sender.followage}", channel.getFollowAge(sender.getId()))).append(" ");
            else if(s.contains("{user.followage}")) sb.append(s.replaceAll("\\{user.followage}", channel.getFollowAge(user.getId()))).append(" ");
            else if(s.contains("{channel.uptime}")) sb.append(s.replaceAll("\\{channel.uptime}", isLive ? channel.getUptime() : "0")).append(" ");
            else if(s.contains("{channel.chatters}")) sb.append(s.replaceAll("\\{channel.chatters}", String.valueOf(channel.getChatters().size()))).append(" ");
            else sb.append(s).append(" ");
        }

        if(!sb.isEmpty()) {
            channel.sendMessage(sb.toString());
        }

    }
}
