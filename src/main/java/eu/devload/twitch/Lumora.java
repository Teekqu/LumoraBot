package eu.devload.twitch;

import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import eu.devload.twitch.api.API;
import eu.devload.twitch.manager.CacheManager;
import eu.devload.twitch.manager.CommandTriggerImpl;
import eu.devload.twitch.manager.DefaultCommandManager;
import eu.devload.twitch.manager.ModuleManager;
import eu.devload.twitch.modules.ccommands.CCommandsModule;
import eu.devload.twitch.modules.core.CoreModule;
import eu.devload.twitch.modules.developer.DeveloperModule;
import eu.devload.twitch.modules.stats.StatsModule;
import eu.devload.twitch.modules.stats.utils.StatsManager;
import eu.devload.twitch.objects.ClientUser;
import eu.devload.twitch.utils.SystemAPI;

public class Lumora {

    public static void main(String[] args) {

        TwitchClient client = TwitchClientBuilder.builder()
                .withDefaultAuthToken(new OAuth2Credential("twitch", SystemAPI.get().config().get("twitch.client.oauth").toString()))
                .withEnableHelix(true)
                .withEnablePubSub(true)
                .withChatQueueTimeout(10000)
                .withTimeout(10000)
                .withEnableChat(true)
                .withChatAccount(new OAuth2Credential("twitch", SystemAPI.get().config().get("twitch.client.oauth").toString()))
                .withChatCommandsViaHelix(true)
                .build();

        SimpleEventHandler eventHandler = client.getEventManager().getEventHandler(SimpleEventHandler.class);
        SystemAPI.get().client(client);
        SystemAPI.get().eventHandler(eventHandler);

        DefaultCommandManager.updateDefaultCommandsInDatabase();

        // Modules
        ModuleManager moduleManager = SystemAPI.get().moduleManager();
        moduleManager.registerModule(new CoreModule());
        moduleManager.registerModule(new StatsModule());
        moduleManager.registerModule(new CCommandsModule());
        moduleManager.registerModule(new DeveloperModule());

        // API
        new API();

        SystemAPI.get().twitchManager().joinChannels(SystemAPI.get().twitchManager().registeredChannels());
        client.getChat().joinChannel(ClientUser.get().login());
        client.getChat().connect();

        CommandTriggerImpl.startEventCheck();
        CacheManager.get().initialUserCache();

    }

}
