package committee.nova.fs.common.mixin;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

import static committee.nova.fs.FireSource.magmaIsFireSrc;

@Mixin(MagmaBlock.class)
public abstract class MixinMagmaBlock {
    @Inject(method = "randomTick", at = @At("HEAD"))
    public void onRandomTick(BlockState state, ServerLevel level, BlockPos pos, Random random, CallbackInfo ci) {
        if (magmaIsFireSrc.get()) Utils.tickFireSpread((l, p) -> 25, level, pos);
    }
}
