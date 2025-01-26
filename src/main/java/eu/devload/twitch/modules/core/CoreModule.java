package eu.devload.twitch.modules.core;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.core.commands.*;
import eu.devload.twitch.modules.core.events.*;

public class CoreModule implements TwitchModule {
    @Override
    public void onEnable() {

        new Join();
        new Leave();
        new Support();

        new ChannelLiveEvent();
        new ChannelOfflineEvent();
        new ChannelUpdateTitleEvent();
        new ChannelUpdateGameEvent();
        new ChannelUpdateViewerEvent();

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new CoreModuleInfo();
    }
}
