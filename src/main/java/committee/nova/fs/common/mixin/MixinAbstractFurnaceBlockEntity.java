package committee.nova.fs.common.mixin;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static committee.nova.fs.FireSource.furnaceIsFireSrc;
import static net.minecraft.world.level.block.AbstractFurnaceBlock.LIT;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class MixinAbstractFurnaceBlockEntity {
    @Inject(method = "serverTick", at = @At("HEAD"))
    private static void onServerTick(Level level, BlockPos pos, BlockState state, AbstractFurnaceBlockEntity furnace, CallbackInfo ci) {
        if (!furnaceIsFireSrc.get()) return;
        if (!state.getValue(LIT)) return;
        if (level.random.nextInt(5001) > 77) return;
        Utils.tickFireSpread((l, p) -> 25, level, pos);
    }
}
