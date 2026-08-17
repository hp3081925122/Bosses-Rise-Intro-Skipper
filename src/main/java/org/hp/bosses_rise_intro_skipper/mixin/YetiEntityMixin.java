package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = YetiEntity.class, remap = false)
public class YetiEntityMixin {
    @Shadow(remap = false) @Final public static EntityDataAccessor<Integer> DATA_IS_ENRAGED;

    @ModifyVariable(method = "setState", at = @At("HEAD"), argsOnly = true, remap = false)
    private String bossesRiseIntroSkipper$skipCinematicState(String state) {
        if (!Config.shouldSkipYetiCinematics() || !bossesRiseIntroSkipper$isCinematicState(state)) {
            return state;
        }

        if (bossesRiseIntroSkipper$isIntroState(state)) {
            bossesRiseIntroSkipper$finishIntroWithoutAnimation();
        }

        return "IDLE";
    }

    @Inject(method = "baseTick", at = @At("HEAD"), remap = false)
    private void bossesRiseIntroSkipper$finishEnrageWithoutAnimation(CallbackInfo ci) {
        if (!Config.shouldSkipYetiCinematics()) {
            return;
        }

        YetiEntity yeti = (YetiEntity) (Object) this;
        SynchedEntityData entityData = yeti.getEntityData();
        int enrageState = entityData.get(DATA_IS_ENRAGED);
        if (enrageState == 3 || enrageState == 4) {
            entityData.set(DATA_IS_ENRAGED, 5);
        }

        AbstractBossEntity.State state = yeti.getState();
        if (enrageState < 2 && state != null && "IDLE".equals(state.name())) {
            bossesRiseIntroSkipper$finishIntroWithoutAnimation();
        }

        if (state != null && bossesRiseIntroSkipper$isEnragedState(state.name())) {
            yeti.setState("IDLE");
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"), remap = false)
    private void bossesRiseIntroSkipper$finishDeathAfterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.shouldSkipYetiCinematics()) {
            return;
        }

        YetiEntity yeti = (YetiEntity) (Object) this;
        if (yeti.level().isClientSide()) {
            return;
        }

        bossesRiseIntroSkipper$finishDeathWithoutAnimation(cir.getReturnValue());
    }

    @Redirect(method = "lambda$hurt$21", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", remap = false), remap = false)
    private static boolean bossesRiseIntroSkipper$skipEnrageEffect(LivingEntity livingEntity, MobEffectInstance effect) {
        return !Config.shouldSkipYetiCinematics() && livingEntity.addEffect(effect);
    }

    @Unique
    private static boolean bossesRiseIntroSkipper$isCinematicState(String state) {
        return bossesRiseIntroSkipper$isIntroState(state)
                || bossesRiseIntroSkipper$isEnragedState(state);
    }

    @Unique
    private static boolean bossesRiseIntroSkipper$isIntroState(String state) {
        return "INTRO".equals(state) || "intro".equals(state);
    }

    @Unique
    private static boolean bossesRiseIntroSkipper$isEnragedState(String state) {
        return "ENRAGED".equals(state) || "enraged".equals(state);
    }

    @Unique
    private void bossesRiseIntroSkipper$finishIntroWithoutAnimation() {
        YetiEntity yeti = (YetiEntity) (Object) this;
        SynchedEntityData entityData = yeti.getEntityData();
        int enrageState = entityData.get(DATA_IS_ENRAGED);
        if (enrageState < 2) {
            bossesRiseIntroSkipper$clearIntroIce();
            entityData.set(DATA_IS_ENRAGED, 2);
            ((AbstractBossEntity) (Object) this).setBarVisible(true);
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$clearIntroIce() {
        YetiEntity yeti = (YetiEntity) (Object) this;
        if (!(yeti.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockPos origin = yeti.blockPosition();
        for (int x = -4; x <= 4; x++) {
            for (int y = 0; y <= 6; y++) {
                for (int z = -4; z <= 4; z++) {
                    BlockPos blockPos = origin.offset(x, y, z);
                    if (serverLevel.getBlockState(blockPos).is(BlockTags.ICE)) {
                        serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                    }
                }
            }
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$finishDeathWithoutAnimation(boolean hurtResult) {
        if (!hurtResult) {
            return;
        }

        YetiEntity yeti = (YetiEntity) (Object) this;
        SynchedEntityData entityData = yeti.getEntityData();
        AbstractBossEntity.State state = yeti.getState();
        if (state == null || !"DEATH".equals(state.name()) || yeti.getHealth() > 0.1F || entityData.get(DATA_IS_ENRAGED) == 6) {
            return;
        }

        entityData.set(DATA_IS_ENRAGED, 6);
        entityData.set(AbstractBossEntity.DATA_DIE_ANIMTIME, 0);
        entityData.set(AbstractBossEntity.DATA_ATTACK_ANIMTIME, 0);
        entityData.set(AbstractBossEntity.DATA_HIT_ANIMTIME, 0);
        yeti.setNoAi(true);
        ((AbstractBossEntity) (Object) this).setBarVisible(false);
        yeti.discard();
    }
}
