package org.hp.bosses_rise_intro_skipper;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.hp.bosses_rise_intro_skipper.Config;
import org.slf4j.Logger;

@Mod(value="bosses_rise_intro_skipper")
public class BossesRiseIntroSkipper {
    public static final String MODID = "bosses_rise_intro_skipper";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BossesRiseIntroSkipper() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, (IConfigSpec)Config.SPEC);
    }
}
