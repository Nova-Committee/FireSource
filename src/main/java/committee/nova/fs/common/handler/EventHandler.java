package committee.nova.fs.common.handler;

import committee.nova.fs.FireSource;
import committee.nova.fs.api.FireSourceApi;
import committee.nova.fs.api.event.FireSourceExtensionEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static committee.nova.fs.common.tools.Utils.getFireSrcType;
import static committee.nova.fs.common.tools.Utils.getHeatLevel;

@Mod.EventBusSubscriber
public class EventHandler {
    @SubscribeEvent
    public static void onExtension(FireSourceExtensionEvent event) {
        event.addScorching(FireSource.MODNAME, (short) 32766, new FireSourceApi.ScorchingBlock(
                (p, b) -> getFireSrcType(p.level, b) != 0,
                (p, b) -> getHeatLevel(getFireSrcType(p.level, b), p.level, b) / 10F
        ));
    }
}
