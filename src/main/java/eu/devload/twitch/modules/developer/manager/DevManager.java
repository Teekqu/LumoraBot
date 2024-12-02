package eu.devload.twitch.modules.developer.manager;

import java.util.List;

public class DevManager {

    public static List<String> developers = List.of(
            "474426129"
    );

    public static boolean isDeveloper(String id) {
        return developers.contains(id);
    }

}
