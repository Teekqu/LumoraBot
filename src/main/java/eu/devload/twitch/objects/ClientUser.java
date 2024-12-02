package eu.devload.twitch.objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter(value = AccessLevel.PUBLIC)
@Accessors(fluent = true)
public class ClientUser {

    public static ClientUser get() {
        return new ClientUser();
    }

    private final String id = "1210324229";
    private final String displayName = "LumoraBot";
    private final String login = displayName.toLowerCase();

}
