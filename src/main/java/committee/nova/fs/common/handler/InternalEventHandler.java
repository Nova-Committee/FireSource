package committee.nova.fs.common.handler;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static committee.nova.fs.api.FireSourceApi.getScorchingBlock;
import static committee.nova.fs.api.FireSourceApi.getScorchingBlockIndex;

@Mod.EventBusSubscriber
public class InternalEventHandler {
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        if (e.isCanceled()) return;
        final var player = e.getPlayer();
        if (!player.getMainHandItem().isEmpty()) return;
        final var pos = e.getPos();
        final var index = getScorchingBlockIndex(player, pos);
        if (index == Short.MIN_VALUE) return;
        final var scorch = getScorchingBlock(index);
        player.hurt(DamageSource.IN_FIRE, scorch.damage().apply(player, pos));
    }
}
