package eu.devload.twitch.modules.developer;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.developer.commands.StatsCommand;

public class DeveloperModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerCommand(new StatsCommand());

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new DeveloperModuleInfo();
    }
}
