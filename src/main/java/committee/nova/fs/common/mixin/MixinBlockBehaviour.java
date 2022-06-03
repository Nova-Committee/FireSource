package committee.nova.fs.common.mixin;

import committee.nova.fs.common.blockEntity.api.IFireSource;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class MixinBlockBehaviour {
    @Shadow
    public abstract Block getBlock();

    @Shadow
    protected abstract BlockState asState();

    @Inject(method = "isRandomlyTicking", at = @At("RETURN"), cancellable = true)
    public void randomlyTicking(CallbackInfoReturnable<Boolean> cir) {
        final var block = getBlock();
        cir.setReturnValue(block instanceof IFireSource || block.isRandomlyTicking(asState()));
    }

    @Inject(method = "randomTick", at = @At("HEAD"))
    public void onRandomTick(ServerLevel world, BlockPos pos, Random random, CallbackInfo ci) {
        if (getBlock() instanceof IFireSource s) s.tickFireSpread(world, pos, random);
    }
}
