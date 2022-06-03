package committee.nova.fs.common.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Cancelable;

/**
 * The event of fire sources' fire spreading.
 * Can be cancelled.
 * Very similar to the FluidPlaceBlockEvent
 *
 * @see net.minecraftforge.event.world.BlockEvent.FluidPlaceBlockEvent
 * FluidPlaceBlockEvent
 */
@Cancelable
public class FireSourceSpreadFireEvent extends BlockEvent {
    private final BlockPos sourcePos;
    private BlockState newState;
    private final BlockState originState;

    public FireSourceSpreadFireEvent(LevelAccessor world, BlockPos pos, BlockPos sourcePos, BlockState state) {
        super(world, pos, state);
        this.sourcePos = sourcePos;
        this.newState = state;
        this.originState = world.getBlockState(pos);
    }

    /**
     * @return The position of the fire source this event originated from. This may be the same as {@link #getPos()}.
     */
    public BlockPos getSourcePos() {
        return sourcePos;
    }

    /**
     * @return The block state that will be placed after this event resolves.
     */
    public BlockState getNewState() {
        return newState;
    }

    public void setNewState(BlockState state) {
        this.newState = state;
    }

    /**
     * @return The state of the block to be changed before the event was fired.
     */
    public BlockState getOriginalState() {
        return originState;
    }
}
