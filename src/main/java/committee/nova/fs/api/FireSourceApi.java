package committee.nova.fs.api;

import committee.nova.fs.api.event.FireSourceExtensionEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

@Mod.EventBusSubscriber
public class FireSourceApi {
    private static final HashMap<Short, ScorchingBlock> scorchingBlockList = new HashMap<>();

    @SubscribeEvent
    public static void onStarted(ServerStartedEvent v) {
        final var event = new FireSourceExtensionEvent();
        MinecraftForge.EVENT_BUS.post(event);
        scorchingBlockList.putAll(event.getScorchingBlockList());
    }

    /**
     * @param blockCondition The block's breaker and position
     * @param damage         The damage caused
     */
    public record ScorchingBlock(
            BiPredicate<Player, BlockPos> blockCondition,
            BiFunction<Player, BlockPos, Float> damage
    ) {
    }

    public static short getScorchingBlockIndex(Player player, BlockPos pos) {
        final short[] s = {Short.MIN_VALUE};
        scorchingBlockList.forEach((p, b) -> {
            if (p > s[0] && b.blockCondition.test(player, pos)) s[0] = p;
        });
        return s[0];
    }

    public static ScorchingBlock getScorchingBlock(short index) {
        if (index == Short.MIN_VALUE) throw new NumberFormatException("Priority value should be greater than -32768");
        return scorchingBlockList.get(index);
    }
}
