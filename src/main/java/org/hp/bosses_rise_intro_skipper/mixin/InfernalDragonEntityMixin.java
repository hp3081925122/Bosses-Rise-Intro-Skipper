package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.network.syncher.SynchedEntityData;
import net.unusual.block_factorys_bosses.block.entity.BossSpawnerBlockEntity;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.dragon.boss.InfernalDragonEntity;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = InfernalDragonEntity.class, remap = false)
public abstract class InfernalDragonEntityMixin {
    @Shadow(remap = false) public abstract void setState(String state);

    @ModifyVariable(method = "setState", at = @At("HEAD"), argsOnly = true, remap = false)
    private String bossesRiseIntroSkipper$skipCinematicState(String state) {
        return Config.shouldSkipDragonCinematics() && bossesRiseIntroSkipper$isCinematicState(state) ? "idle" : state;
    }

    @Inject(method = "onBossSpawnerSpawn", at = @At("RETURN"), remap = false)
    private void bossesRiseIntroSkipper$finishSpawnerIntro(BossSpawnerBlockEntity spawner, CallbackInfo ci) {
        if (!Config.shouldSkipDragonCinematics()) {
            return;
        }

        bossesRiseIntroSkipper$disableCinematicBlackScreen();
        ((InfernalDragonEntity) (Object) this).setPos(spawner.getBlockPos().getCenter());
        ((AbstractBossEntity) (Object) this).setBossPhase(2);
        this.setState("idle");
    }

    @Inject(method = "performPhaseTransition", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipPhaseTransition(CallbackInfo ci) {
        if (!Config.shouldSkipDragonCinematics()) {
            return;
        }

        SynchedEntityData entityData = ((InfernalDragonEntity) (Object) this).getEntityData();
        entityData.set(AbstractBossEntity.DATA_BOSS_PHASE, 5);
        ((AbstractBossEntity) (Object) this).setCinematicBlackScreen(false);
        this.setState("idle");
        ci.cancel();
    }

    @Inject(method = "onCinematicStarted", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipCinematicPlayerEffects(int duration, CallbackInfo ci) {
        if (Config.shouldSkipDragonCinematics()) {
            ci.cancel();
        }
    }

    @Inject(method = "isCinematic", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$disableCinematicFlag(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipDragonCinematics()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldCancelDeath", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipDeathAnimation(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipDragonCinematics()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean bossesRiseIntroSkipper$isCinematicState(String state) {
        return "intro".equals(state) || "phase_transition".equals(state);
    }

    @Unique
    private void bossesRiseIntroSkipper$disableCinematicBlackScreen() {
        ((AbstractBossEntity) (Object) this).setCinematicBlackScreen(false);
    }
}
