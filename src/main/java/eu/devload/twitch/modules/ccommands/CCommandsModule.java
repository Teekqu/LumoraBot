package eu.devload.twitch.modules.ccommands;

import eu.devload.twitch.interfaces.ModuleInfo;
import eu.devload.twitch.interfaces.TwitchModule;
import eu.devload.twitch.modules.ccommands.commands.Command;
import eu.devload.twitch.modules.ccommands.commands.CustomCommandUsage;
import eu.devload.twitch.modules.ccommands.commands.TestCommand;

public class CCommandsModule implements TwitchModule {
    @Override
    public void onEnable() {

        registerCommand(new Command());
        registerCommand(new CustomCommandUsage());

    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onReset() {

    }

    @Override
    public ModuleInfo info() {
        return new CCCommandsModuleInfo();
    }
}
