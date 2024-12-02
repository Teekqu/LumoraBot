package eu.devload.twitch.modules.stats;

import eu.devload.twitch.interfaces.ModuleInfo;

import java.util.List;

public class StatsModuleInfo implements ModuleInfo {
    @Override
    public String identifier() {
        return "stats";
    }

    @Override
    public String name() {
        return "Statistics";
    }

    @Override
    public String description() {
        return "Channel statistics like chat messages, watchtime, sub records and more!";
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
