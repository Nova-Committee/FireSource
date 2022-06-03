package committee.nova.fs.common.blockEntity.api;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.Random;
import java.util.function.BiFunction;

public interface IFireSource {
    default BiFunction<Level, BlockPos, Integer> getHeat() {
        return (l, p) -> 25;
    }

    default void tickFireSpread(Level level, BlockPos pos, Random random) {
        Utils.tickFireSpread(getHeat(), level, pos, random);
    }
}
