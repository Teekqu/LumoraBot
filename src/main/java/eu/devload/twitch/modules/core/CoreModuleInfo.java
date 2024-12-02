package eu.devload.twitch.modules.core;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class CoreModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "core";
    }

    @Override
    public String name() {
        return "Core";
    }

    @Override
    public String description() {
        return "The core module of the bot. Contains the basic commands and events.";
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
