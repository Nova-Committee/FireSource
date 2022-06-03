package committee.nova.fs.common.block.api;

import committee.nova.fs.common.tools.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.Level;

/**
 * Implement this to make your block a custom fire source.
 *
 * @author Tapio
 */
public interface IFireSource {
    /**
     * @param level The level the fire source in
     * @param pos   The fire source's pos
     * @return The heat value of the fire source.
     * The greater the value is, the more possibility && larger range the fire spreads
     */
    default int getHeat(Level level, BlockPos pos) {
        return 25;
    }

    /**
     * @param level The level the fire source in
     * @param pos   The fire source's pos
     */
    default void tickFireSpread(Level level, BlockPos pos) {
        Utils.tickFireSpread(this::getHeat, level, pos);
    }

    /**
     * @param level The level the fire source in
     * @param pos   The fire source's pos
     * @return The custom name of the fire source to display as a fire danger when FireSafety is loaded.
     * By default, returns the block's origin name
     */
    default MutableComponent getCustomDisplayNameAsFireDanger(Level level, BlockPos pos) {
        return level.getBlockState(pos).getBlock().getName();
    }
}
