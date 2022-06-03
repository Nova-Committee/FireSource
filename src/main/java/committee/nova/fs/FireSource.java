package committee.nova.fs;

import committee.nova.firesafety.api.FireSafetyApi;
import committee.nova.firesafety.api.event.FireSafetyExtensionEvent;
import committee.nova.fs.common.tools.Utils;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(FireSource.MODID)
public class FireSource {
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
        event.addFireDanger(MODNAME, (short) 32222, new FireSafetyApi.FireDangerBlock(Utils::isFireSrc, Utils::getDangerousnessByHeat, Utils::getTips));
    }
}
