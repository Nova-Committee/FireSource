package committee.nova.fs.common.mixin;

import committee.nova.fs.api.block.IFireSource;
import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

import static committee.nova.fs.FireSource.torchIsFireSrc;
import static net.minecraft.world.level.block.Blocks.REDSTONE_TORCH;
import static net.minecraft.world.level.block.Blocks.REDSTONE_WALL_TORCH;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockBehaviour {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "isRandomlyTicking", at = @At("RETURN"), cancellable = true)
    public void randomlyTicking(CallbackInfoReturnable<Boolean> cir) {
        final var block = getBlock();
        cir.setReturnValue(block instanceof IFireSource || (torchIsFireSrc.get() && (block instanceof TorchBlock)) || block.isRandomlyTicking(asState()));
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    public void onRandomTick(ServerLevel world, BlockPos pos, Random random, CallbackInfo ci) {
        final var block = getBlock();
        if (torchIsFireSrc.get() && block instanceof TorchBlock) Utils.tickFireSpread((l, p) -> {
            final var state = l.getBlockState(p);
            return (!(state.is(REDSTONE_TORCH) || state.is(REDSTONE_WALL_TORCH))) ? 2 : 1;
        }, world, pos);
        if (block instanceof IFireSource s) s.tickFireSpread(world, pos);
    }
}
