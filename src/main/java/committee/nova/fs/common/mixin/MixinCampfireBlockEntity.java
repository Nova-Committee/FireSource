package committee.nova.fs.common.mixin;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.CampfireBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static committee.nova.fs.FireSource.campfireIsFireSrc;
import static net.minecraft.world.level.block.AbstractFurnaceBlock.LIT;

@Mixin(CampfireBlockEntity.class)
public abstract class MixinCampfireBlockEntity {
    @Inject(method = "cookTick", at = @At("HEAD"))
    private static void onCookTick(Level level, BlockPos pos, BlockState state, CampfireBlockEntity campfire, CallbackInfo ci) {
        if (!campfireIsFireSrc.get()) return;
        if (!state.getValue(LIT)) return;
        if (level.random.nextInt(5001) > 77) return;
        Utils.tickFireSpread((l, p) -> 50, level, pos);
    }
}
