package committee.nova.fs;

import committee.nova.firesafety.api.FireSafetyApi;
import committee.nova.firesafety.api.event.FireSafetyExtensionEvent;
import committee.nova.fs.common.block.api.IFireSource;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
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
    public static final ForgeConfigSpec.BooleanValue torchIsFireSrc;

    static {
        final var builder = new ForgeConfigSpec.Builder();
        builder.comment("FireSource Configuration");
        furnaceIsFireSrc = builder.comment("Fire Source Vanilla Overrides", "Is furnace block a fire source?")
                .define("furnace_fs", true);
        campfireIsFireSrc = builder.comment("Is campfire a fire source?")
                .define("campfire_fs", true);
        torchIsFireSrc = builder.comment("Is torch a fire source?")
                .define("torch_fs", false);
        COMMON_CONFIG = builder.build();
    }

    public FireSource() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON_CONFIG);
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
        if (torchIsFireSrc.get() && (state.getBlock() instanceof TorchBlock)) return 3;
        if (state.getBlock() instanceof IFireSource) return 4;
        return 0;
    }

    public int getDangerousnessByHeat(Level level, BlockPos pos) {
        final var state = level.getBlockState(pos);
        final var srcType = getFireSrcType(level, pos);
        return switch (srcType) {
            case 1, 2, 3 -> 4 - srcType;
            case 4 -> Mth.clamp(((IFireSource) state.getBlock()).getHeat().apply(level, pos), 0, 100) / 30 + 1;
            default -> 0;
        };
    }

    public MutableComponent getTips(Level level, BlockPos pos) {
        final var block = level.getBlockState(pos).getBlock();
        final var heatLevel = switch (getFireSrcType(level, pos)) {
            case 1 -> 50;
            case 2 -> 25;
            case 3 -> 2;
            case 4 -> ((IFireSource) block).getHeat().apply(level, pos);
            default -> 0;
        };
        return new TranslatableComponent("tips.firesafety.danger.firesource", block.getName().getString(), heatLevel);
    }
}
