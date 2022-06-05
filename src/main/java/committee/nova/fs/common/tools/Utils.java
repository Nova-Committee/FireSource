package committee.nova.fs.common.tools;

import committee.nova.fs.api.block.IFireSource;
import committee.nova.fs.api.event.FireSourceSpreadFireEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

import static committee.nova.fs.FireSource.*;
import static net.minecraft.world.level.block.AbstractFurnaceBlock.LIT;
import static net.minecraft.world.level.block.Blocks.*;

public class Utils {
    /**
     * Default function to simulate the process of fire spreading from a fire source, can also be used in your blockEntities' tick functions
     */
    public static void tickFireSpread(BiFunction<Level, BlockPos, Integer> heatGetter, Level level, BlockPos pos) {
        tickFireSpread(heatGetter, null, level, pos);
    }

    /**
     * @param spreadRange The range of fire spreading
     */
    public static void tickFireSpread(BiFunction<Level, BlockPos, Integer> heatGetter, @Nullable BiFunction<Level, BlockPos, Iterable<BlockPos>> spreadRange, Level level, BlockPos pos) {
        final var random = level.random;
        final var heat = heatGetter.apply(level, pos);
        final var possibility = Mth.clamp(heat, 0, 100);
        if (random.nextInt(101) > possibility) return;
        final var range = (int) Math.sqrt(heat);
        final var blocks = spreadRange != null ?
                spreadRange.apply(level, pos) : BlockPos.betweenClosed(pos.offset(-range, -range, -range), pos.offset(range, range, range));
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


    public static boolean isFireSrc(Level level, BlockPos pos) {
        return getFireSrcType(level, pos) != 0;
    }

    public static int getFireSrcType(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        if (torchIsFireSrc.get() && state.getBlock() instanceof TorchBlock) return 3;
        if ((magmaIsFireSrc.get() && state.is(MAGMA_BLOCK)) || (campfireIsFireSrc.get() && (state.is(CAMPFIRE) || state.is(SOUL_CAMPFIRE)) && state.getValue(LIT)))
            return 1;
        if (furnaceIsFireSrc.get() && (state.is(FURNACE) || state.is(BLAST_FURNACE) || state.is(SMOKER)) && state.getValue(LIT))
            return 2;
        if (state.getBlock() instanceof IFireSource) return 4;
        return 0;
    }

    public static int getDangerousnessByHeat(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        final var srcType = getFireSrcType(level, pos);
        return switch (srcType) {
            case 1, 2, 3 -> 4 - srcType;
            case 4 -> Mth.clamp(((IFireSource) state.getBlock()).getHeat(level, pos), 0, 100) / 30 + 1;
            default -> 0;
        };
    }

    public static MutableComponent getTips(Level level, BlockPos pos) {
        final var block = level.getBlockState(pos).getBlock();
        final var srcType = getFireSrcType(level, pos);
        final var heatLevel = getHeatLevel(srcType, level, pos);
        final var customName = (srcType != 4) ? block.getName().getString() : ((IFireSource) block).getCustomDisplayNameAsFireDanger(level, pos).getString();
        return new TranslatableComponent("tips.firesafety.danger.firesource", customName, heatLevel);
    }

    public static int getHeatLevel(int srcType, Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        return switch (srcType) {
            case 1 -> 50;
            case 2 -> 25;
            case 3 -> (!(state.is(REDSTONE_TORCH) || state.is(REDSTONE_WALL_TORCH))) ? 2 : 1;
            case 4 -> ((IFireSource) state.getBlock()).getHeat(level, pos);
            default -> 0;
        };
    }
}
