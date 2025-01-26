package eu.devload.twitch.modules.ccommands.commands;

import com.github.twitch4j.common.events.domain.EventUser;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.interfaces.TwitchCommand;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.modules.ccommands.objects.CustomCommand;
import eu.devload.twitch.modules.ccommands.utils.CCManager;
import eu.devload.twitch.objects.LiveObject;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;

import java.util.Collections;

public class CustomCommandUsage implements TwitchCommand {
    @Override
    public void execute(TwitchChannel channel, EventUser sender, String command, String[] args) {
        CustomCommand cmd = CCManager.getCommand(channel.id(), command);
        if(cmd == null || cmd.isDefault()) return;
        if(!cmd.hasPermission(sender.getId())) return;

        String userName = sender.getName();
        if(args.length > 0) {
            userName = args[0];
        }
        
        User user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), null, Collections.singletonList(userName)).execute().getUsers().getFirst();
        if(user == null) user = SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        //User senderUser = user.getId().equals(sender.getId()) ? user : SystemAPI.get().client().getHelix().getUsers(channel.oauth2(), Collections.singletonList(sender.getId()), null).execute().getUsers().getFirst();

        LiveObject stream = CacheManager.get().getLiveChannel(channel.id());
        if(stream == null) stream = new LiveObject(channel.id(), "-", "-", 0);

        String value = cmd.value();
        StringBuilder sb = new StringBuilder();
        String[] splitted = value.split(" ");

        for(String s : splitted) {
            if(!s.contains("{") && !s.contains("}")) sb.append(s).append(" ");
            else if(s.contains("{channel}")) sb.append(s.replaceFirst("\\{channel}", channel.getName())).append(" ");
            else if(s.contains("{sender}")) sb.append(s.replaceFirst("\\{sender}", sender.getName())).append(" ");
            else if(s.contains("{user}")) sb.append(s.replaceFirst("\\{user}", user.getLogin())).append(" ");
            else if(s.contains("{channel.id}")) sb.append(s.replaceFirst("\\{channel.id}", channel.id())).append(" ");
            else if(s.contains("{sender.id}")) sb.append(s.replaceFirst("\\{sender.id}", sender.getId())).append(" ");
            else if(s.contains("{user.id}")) sb.append(s.replaceFirst("\\{user.id}", user.getId())).append(" ");
            else if(s.contains("{channel.type}")) sb.append(s.replaceFirst("\\{channel.type}", channel.getBroadcasterType())).append(" ");
            else if(s.contains("{args}")) sb.append(s.replaceFirst("\\{args}", String.join(" ", args))).append(" ");
            else if(s.contains("{channel.game}")) sb.append(s.replaceFirst("\\{channel.game}", stream.game())).append(" ");
            else if(s.contains("{channel.title}")) sb.append(s.replaceFirst("\\{channel.title}", stream.title())).append(" ");
            else if(s.contains("{channel.viewers}")) sb.append(s.replaceFirst("\\{channel.viewers}", String.valueOf(stream.viewer()))).append(" ");
            else if(s.contains("{channel.followers}")) sb.append(s.replaceFirst("\\{channel.followers}", String.valueOf(channel.getFollowers()))).append(" ");
            else if(s.contains("{channel.subs}")) sb.append(s.replaceFirst("\\{channel.subs}", String.valueOf(channel.getSubCount()))).append(" ");
            else if(s.contains("{channel.description}")) sb.append(s.replaceFirst("\\{channel.description}", channel.getDescription())).append(" ");
            else if(s.contains("{sender.followage}")) sb.append(s.replaceFirst("\\{sender.followage}", channel.getFollowAge(sender.getId()))).append(" ");
            else if(s.contains("{user.followage}")) sb.append(s.replaceFirst("\\{user.followage}", channel.getFollowAge(user.getId()))).append(" ");
            else if(s.contains("{channel.uptime}")) sb.append(s.replaceFirst("\\{channel.uptime}", channel.getUptime())).append(" ");
            else if(s.contains("{channel.chatters}")) sb.append(s.replaceFirst("\\{channel.chatters}", String.valueOf(channel.getChatters().size()))).append(" ");
            else sb.append(s).append(" ");
        }

        if(!sb.isEmpty()) {
            channel.sendMessage(sb.toString());
        }

    }
}
