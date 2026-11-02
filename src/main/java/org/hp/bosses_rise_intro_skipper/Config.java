package org.hp.bosses_rise_intro_skipper;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

@Mod.EventBusSubscriber(modid="bosses_rise_intro_skipper", bus=Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.BooleanValue SKIP_BOSS_INTRO_ANIMATIONS = BUILDER.comment("\u8df3\u8fc7 Bosses Rise Boss \u8fc7\u573a\u52a8\u753b\u7684\u603b\u5f00\u5173\u3002").define("skipBossIntroAnimations", true);
    private static final ForgeConfigSpec.BooleanValue SKIP_YETI_INTRO = BUILDER.comment("\u8df3\u8fc7\u65af\u79d1\u5c14\uff0c\u971c\u51bb\u96ea\u602a\u7684\u5165\u573a\u3001\u6fc0\u6012\u8f6c\u9636\u6bb5\u548c\u6b7b\u4ea1\u52a8\u753b\u3002").define("skipYetiIntro", true);
    private static final ForgeConfigSpec.BooleanValue SKIP_DRAGON_INTRO = BUILDER.comment("\u8df3\u8fc7\u963f\u4ec0\u6d1b\u5fb7\uff0c\u70bc\u72f1\u98de\u9f99\u7684\u5165\u573a\u3001\u8f6c\u9636\u6bb5\u548c\u6b7b\u4ea1\u52a8\u753b\u3002").define("skipDragonIntro", true);
    private static final ForgeConfigSpec.BooleanValue SKIP_KNIGHT_INTRO = BUILDER.comment("\u8df3\u8fc7\u8d6b\u5c14\u74e6\uff0c\u51a5\u754c\u9a91\u58eb\u7684\u5165\u573a\u3001\u5047\u6b7b\u3001\u590d\u6d3b\u548c\u6700\u7ec8\u6b7b\u4ea1\u8fc7\u573a\u3002").define("skipKnightIntro", true);
    private static final ForgeConfigSpec.BooleanValue SKIP_SANDWORM_INTRO = BUILDER.comment("\u8df3\u8fc7\u65af\u6d1b\u514b\uff0c\u5de8\u578b\u6c99\u866b\u7684\u5165\u573a\u548c\u6b7b\u4ea1\u8fc7\u573a\u3002").define("skipSandwormIntro", true);
    private static final ForgeConfigSpec.BooleanValue SKIP_KRAKEN_INTRO = BUILDER.comment("\u8df3\u8fc7 Nerakyss, The Kraken \u7684\u5165\u573a\u548c\u4e8c\u9636\u6bb5\u8fc7\u573a\u3002").define("skipKrakenIntro", true);
    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static boolean skipBossIntroAnimations;
    public static boolean skipYetiIntro;
    public static boolean skipDragonIntro;
    public static boolean skipKnightIntro;
    public static boolean skipSandwormIntro;
    public static boolean skipKrakenIntro;

    public static boolean shouldSkipYetiIntro() {
        return skipBossIntroAnimations && skipYetiIntro;
    }

    public static boolean shouldSkipYetiCinematics() {
        return Config.shouldSkipYetiIntro();
    }

    public static boolean shouldSkipDragonIntro() {
        return skipBossIntroAnimations && skipDragonIntro;
    }

    public static boolean shouldSkipDragonCinematics() {
        return Config.shouldSkipDragonIntro();
    }

    public static boolean shouldSkipKnightIntro() {
        return skipBossIntroAnimations && skipKnightIntro;
    }

    public static boolean shouldSkipKnightCinematics() {
        return Config.shouldSkipKnightIntro();
    }

    public static boolean shouldSkipSandwormIntro() {
        return skipBossIntroAnimations && skipSandwormIntro;
    }

    public static boolean shouldSkipSandwormCinematics() {
        return Config.shouldSkipSandwormIntro();
    }

    public static boolean shouldSkipKrakenIntro() {
        return skipBossIntroAnimations && skipKrakenIntro;
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        skipBossIntroAnimations = (Boolean)SKIP_BOSS_INTRO_ANIMATIONS.get();
        skipYetiIntro = (Boolean)SKIP_YETI_INTRO.get();
        skipDragonIntro = (Boolean)SKIP_DRAGON_INTRO.get();
        skipKnightIntro = (Boolean)SKIP_KNIGHT_INTRO.get();
        skipSandwormIntro = (Boolean)SKIP_SANDWORM_INTRO.get();
        skipKrakenIntro = (Boolean)SKIP_KRAKEN_INTRO.get();
    }
}
