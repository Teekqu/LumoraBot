package eu.devload.twitch.modules.stats;

import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.stats.commands.block.Block;
import eu.devload.twitch.modules.stats.commands.block.Unblock;
import eu.devload.twitch.modules.stats.commands.chatstats.AddMessages;
import eu.devload.twitch.modules.stats.commands.chatstats.Messages;
import eu.devload.twitch.modules.stats.commands.chatstats.RemoveMessages;
import eu.devload.twitch.modules.stats.commands.chatstats.ResetMessages;
import eu.devload.twitch.modules.stats.commands.watchtime.AddMinutes;
import eu.devload.twitch.modules.stats.commands.watchtime.RemoveMinutes;
import eu.devload.twitch.modules.stats.commands.watchtime.ResetMinutes;
import eu.devload.twitch.modules.stats.events.MessageCount;
import eu.devload.twitch.modules.stats.events.SubscriptionEvent;
import eu.devload.twitch.modules.stats.events.Watchtime;

public class StatsModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerCommand(new Block());
        registerCommand(new Unblock());

        registerCommand(new AddMessages());
        registerCommand(new RemoveMessages());
        registerCommand(new ResetMessages());

        registerCommand(new AddMinutes());
        registerCommand(new RemoveMinutes());
        registerCommand(new ResetMinutes());

        registerCommand(new eu.devload.twitch.modules.stats.commands.watchtime.Watchtime());
        registerCommand(new Messages());


        registerEvent(new MessageCount());
        registerEvent(new SubscriptionEvent());


        new Watchtime();

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new StatsModuleInfo();
    }
}
