package committee.nova.fs;

import committee.nova.firesafety.api.FireSafetyApi;
import committee.nova.firesafety.api.event.FireSafetyExtensionEvent;
import committee.nova.fs.common.blockEntity.api.IFireSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.world.level.block.Blocks.*;

@Mod(FireSource.MODID)
public class FireSource {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "firesource";
    public static final String MODNAME = "FireSource";
    public static final ForgeConfigSpec COMMON_CONFIG;
    public static final ForgeConfigSpec.BooleanValue furnaceIsFireSrc;
    public static final ForgeConfigSpec.BooleanValue campfireIsFireSrc;

    static {
        final var builder = new ForgeConfigSpec.Builder();
        builder.comment("FireSource Configuration");
        furnaceIsFireSrc = builder.comment("Fire Source Vanilla Overrides", "Is furnace block a fire source?")
                .define("furnace_fs", true);
        campfireIsFireSrc = builder.comment("Is campfire a fire source?")
                .define("campfire_fs", true);
        COMMON_CONFIG = builder.build();
    }

    public FireSource() {
        if (ModList.get().isLoaded("firesafety")) {
            try {
                MinecraftForge.EVENT_BUS.addListener(this::onExtension);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void onExtension(FireSafetyExtensionEvent event) {
        event.addFireDanger(MODNAME, (short) 32222, new FireSafetyApi.FireDangerBlock(this::isFireSrc, this::getDangerousnessByHeat, this::getTips));
    }

    public boolean isFireSrc(Level level, BlockPos pos) {
        return getFireSrcType(level, pos) != 0;
    }

    public int getFireSrcType(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        if (campfireIsFireSrc.get() && (state.is(CAMPFIRE) || state.is(SOUL_CAMPFIRE))) return 1;
        if (furnaceIsFireSrc.get() && (state.is(FURNACE) || state.is(BLAST_FURNACE) || state.is(SMOKER))) return 2;
        if (state.getBlock() instanceof IFireSource) return 3;
        return 0;
    }

    public int getDangerousnessByHeat(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        final var srcType = getFireSrcType(level, pos);
        return switch (srcType) {
            case 1 -> 3;
            case 2 -> 2;
            case 3 -> Mth.clamp(((IFireSource) state.getBlock()).getHeat().apply(level, pos), 0, 100) / 30 + 1;
            default -> 0;
        };
    }

    public MutableComponent getTips(Level level, BlockPos pos) {
        return new TranslatableComponent("tips.firesafety.danger.firesource");
    }
}
