package committee.nova.fs.api.event;

import committee.nova.fs.api.FireSourceApi;
import net.minecraftforge.eventbus.api.Event;

import java.util.HashMap;

import static committee.nova.firesafety.FireSafety.LOGGER;

public class FireSourceExtensionEvent extends Event {
    private final HashMap<Short, FireSourceApi.ScorchingBlock> scorchingBlockList;

    public FireSourceExtensionEvent() {
        scorchingBlockList = new HashMap<>();
    }

    public HashMap<Short, FireSourceApi.ScorchingBlock> getScorchingBlockList() {
        return scorchingBlockList;
    }

    public void addScorching(String modName, short priority, FireSourceApi.ScorchingBlock scorching) {
        if (scorchingBlockList.containsKey(priority)) {
            LOGGER.warn("Duplicate priority value {}, new scorching block by {} won't be added!", priority, modName);
            return;
        }
        scorchingBlockList.put(priority, scorching);
        LOGGER.info("Adding new scorching block by {} with priority {}!", modName, priority);
    }
}
