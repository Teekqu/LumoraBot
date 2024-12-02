package eu.devload.twitch.modules.ccommands;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class CCCommandsModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "ccommands";
    }

    @Override
    public String name() {
        return "CustomCommands";
    }

    @Override
    public String description() {
        return "Create custom commands for your channel!";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public boolean canDisable() {
        return true;
    }

    @Override
    public List<String> authors() {
        return List.of("Teekqu");
    }
}
