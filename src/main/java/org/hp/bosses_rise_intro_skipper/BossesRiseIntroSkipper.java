package org.hp.bosses_rise_intro_skipper;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;

@Mod(BossesRiseIntroSkipper.MODID)
public class BossesRiseIntroSkipper {
    public static final String MODID = "bosses_rise_intro_skipper";
    public static final Logger LOGGER = LogUtils.getLogger();

    public BossesRiseIntroSkipper(IEventBus modEventBus, ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        modEventBus.addListener(Config::onLoad);
    }
}
