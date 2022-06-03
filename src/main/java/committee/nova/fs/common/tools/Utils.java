package committee.nova.fs.common.tools;

import committee.nova.fs.common.event.FireSourceSpreadFireEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import java.util.Random;
import java.util.function.BiFunction;

public class Utils {
    public static void tickFireSpread(BiFunction<Level, BlockPos, Integer> heatGetter, Level level, BlockPos pos, Random random) {
        final var heat = heatGetter.apply(level, pos);
        final var possibility = Mth.clamp(heat, 0, 100);
        if (random.nextInt(101) > possibility) return;
        final var range = (int) Math.sqrt(heat);
        final var blocks = BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range));
        for (final var p : blocks) {
            if (random.nextInt(101) > 50) continue;
            if (level.getBlockState(p).isAir() && hasFlammableNeighbours(level, p)) {
                level.setBlockAndUpdate(p, fireSourceSpreadFire(level, p, pos, Blocks.FIRE.defaultBlockState()));
                return;
            }
        }
    }

    private static boolean hasFlammableNeighbours(LevelReader level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (isFlammable(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFlammable(LevelReader level, BlockPos pos, Direction face) {
        return (pos.getY() < level.getMinBuildHeight() || pos.getY() >= level.getMaxBuildHeight() || level.hasChunkAt(pos)) && level.getBlockState(pos).isFlammable(level, pos, face);
    }

    private static BlockState fireSourceSpreadFire(LevelAccessor level, BlockPos pos, BlockPos sourcePos, BlockState state) {
        final var event = new FireSourceSpreadFireEvent(level, pos, sourcePos, state);
        MinecraftForge.EVENT_BUS.post(event);
        return event.isCanceled() ? event.getOriginalState() : event.getNewState();
    }
}
