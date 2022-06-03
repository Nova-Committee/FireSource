package committee.nova.fs.common.block.api;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

/**
 * Implement this to make your block a custom fire source.
 *
 * @author Tapio
 */
public interface IFireSource {
    default BiFunction<Level, BlockPos, Integer> getHeat() {
        return (l, p) -> 25;
    }

    /**
     * @param level The level the fire source in
     * @param pos   The fire source's pos
     */
    default void tickFireSpread(Level level, BlockPos pos) {
        Utils.tickFireSpread(getHeat(), level, pos);
    }
}
