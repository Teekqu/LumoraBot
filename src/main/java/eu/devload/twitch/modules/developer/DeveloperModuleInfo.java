package eu.devload.twitch.modules.developer;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class DeveloperModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "developer";
    }

    @Override
    public String name() {
        return "Developer";
    }

    @Override
    public String description() {
        return "Commands for the bot developers.";
    }

    @Override
    public String version() {
        return "1.0.0";
    }

    @Override
    public boolean canDisable() {
        return false;
    }

    @Override
    public List<String> authors() {
        return List.of("Teekqu");
    }
}
