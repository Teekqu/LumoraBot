package eu.devload.twitch.modules.ccommands.objects;

import com.github.twitch4j.helix.domain.User;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.modules.ccommands.utils.CCManager;
import eu.devload.twitch.objects.TwitchChannel;
import eu.devload.twitch.utils.SystemAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Collections;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class CustomCommand {

    private String channelId;
    private String type;
    @Setter
    private String permission;
    private String name;
    @Setter
    private String value;

    public String rawValue() {
        return this.value;
    }

    public String value() {
        if(!this.isAlias()) return this.value;
        CustomCommand cmd = CCManager.getCommand(this.channelId, this.value);
        if(cmd == null) return "Command not found!";
        while(cmd.isAlias()) {
            cmd = CCManager.getCommand(cmd.channelId(), cmd.rawValue());
            if(cmd == null) return "Command not found!";
        }
        return cmd.value();
    }

    public TwitchChannel twitchChannel() {
        return CacheManager.get().twitchChannel(this.channelId);
    }

    public boolean isAlias() {
        return type.equalsIgnoreCase("alias");
    }

    public boolean isCustom() {
        return type.equalsIgnoreCase("custom");
    }

    public boolean isDefault() {
        return type.equalsIgnoreCase("default");
    }

    public boolean hasPermission(String userId) {
        return switch (this.permission) {
            case "default" -> true;
            case "moderator" -> this.twitchChannel().isModerator(userId);
            case "editor" -> this.twitchChannel().isEditor(userId);
            case "broadcaster" -> this.twitchChannel().isBroadcaster(userId);
            default -> false;
        };
    }

}
