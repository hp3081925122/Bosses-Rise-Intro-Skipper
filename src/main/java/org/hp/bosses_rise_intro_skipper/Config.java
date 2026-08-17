package org.hp.bosses_rise_intro_skipper;

import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue SKIP_BOSS_INTRO_ANIMATIONS = BUILDER
            .comment("跳过 Bosses Rise Boss 过场动画的总开关。")
            .define("skipBossIntroAnimations", true);

    private static final ModConfigSpec.BooleanValue SKIP_YETI_INTRO = BUILDER
            .comment("跳过斯科尔，霜冻雪怪的入场、激怒转阶段和死亡动画。")
            .define("skipYetiIntro", true);

    private static final ModConfigSpec.BooleanValue SKIP_DRAGON_INTRO = BUILDER
            .comment("跳过阿什洛德，炼狱飞龙的入场、转阶段和死亡动画。")
            .define("skipDragonIntro", true);

    private static final ModConfigSpec.BooleanValue SKIP_KNIGHT_INTRO = BUILDER
            .comment("跳过赫尔瓦，冥界骑士的入场、假死、复活和最终死亡过场。")
            .define("skipKnightIntro", true);

    private static final ModConfigSpec.BooleanValue SKIP_SANDWORM_INTRO = BUILDER
            .comment("跳过斯洛克，巨型沙虫的入场和死亡过场。")
            .define("skipSandwormIntro", true);

    private static final ModConfigSpec.BooleanValue SKIP_KRAKEN_INTRO = BUILDER
            .comment("跳过 Nerakyss, The Kraken 的入场和二阶段过场。")
            .define("skipKrakenIntro", true);

    public static final ModConfigSpec SPEC = BUILDER.build();

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
        return shouldSkipYetiIntro();
    }

    public static boolean shouldSkipDragonIntro() {
        return skipBossIntroAnimations && skipDragonIntro;
    }

    public static boolean shouldSkipDragonCinematics() {
        return shouldSkipDragonIntro();
    }

    public static boolean shouldSkipKnightIntro() {
        return skipBossIntroAnimations && skipKnightIntro;
    }

    public static boolean shouldSkipKnightCinematics() {
        return shouldSkipKnightIntro();
    }

    public static boolean shouldSkipSandwormIntro() {
        return skipBossIntroAnimations && skipSandwormIntro;
    }

    public static boolean shouldSkipSandwormCinematics() {
        return shouldSkipSandwormIntro();
    }

    public static boolean shouldSkipKrakenIntro() {
        return skipBossIntroAnimations && skipKrakenIntro;
    }

    static void onLoad(final ModConfigEvent event) {
        if (event.getConfig().getType() != ModConfig.Type.COMMON) {
            return;
        }

        skipBossIntroAnimations = SKIP_BOSS_INTRO_ANIMATIONS.get();
        skipYetiIntro = SKIP_YETI_INTRO.get();
        skipDragonIntro = SKIP_DRAGON_INTRO.get();
        skipKnightIntro = SKIP_KNIGHT_INTRO.get();
        skipSandwormIntro = SKIP_SANDWORM_INTRO.get();
        skipKrakenIntro = SKIP_KRAKEN_INTRO.get();
    }
}
