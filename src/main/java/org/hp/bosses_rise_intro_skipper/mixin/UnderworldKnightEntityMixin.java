package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.knight.UnderworldKnightEntity;
import net.unusual.block_factorys_bosses.util.BossHandling;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = UnderworldKnightEntity.class, remap = false)
public abstract class UnderworldKnightEntityMixin {
    @Shadow(remap = false) @Final public static EntityDataAccessor<Integer> DATA_UNDEAD_CINEMATIC;

    @Shadow(remap = false) @Final public static EntityDataAccessor<Boolean> DATA_CINEMATIC;

    @Shadow(remap = false) @Final public static EntityDataAccessor<Boolean> DATA_TRANSFORMED;

    @Shadow(remap = false) @Final public static EntityDataAccessor<Integer> DATA_INTRO_ATTACK;

    @Shadow(remap = false) private boolean hpGate75;

    @Shadow(remap = false) private boolean hpGate50;

    @Shadow(remap = false) private boolean hpGate25;

    @Unique private boolean bossesRiseIntroSkipper$pendingSkippedFakeDeath;

    @Unique private boolean bossesRiseIntroSkipper$pendingSkippedFinalDeath;

    @Unique private int bossesRiseIntroSkipper$fakeDeathSkipDelay;

    @Shadow(remap = false) public abstract void setImmuneStacks(int stacks);

    @Shadow(remap = false) public abstract void setState(String state);

    @ModifyVariable(method = "setState", at = @At("HEAD"), argsOnly = true, remap = false)
    private String bossesRiseIntroSkipper$skipIntroState(String state) {
        if (!Config.shouldSkipKnightCinematics()) {
            return state;
        }

        if ("intro".equals(state)) {
            bossesRiseIntroSkipper$finishIntroWithoutAnimation();
            return "idle";
        }

        if ("resurrect".equals(state)) {
            bossesRiseIntroSkipper$finishResurrectionWithoutAnimation();
            return "idle";
        }

        if ("fake_dead".equals(state)) {
            bossesRiseIntroSkipper$finishFakeDeathWithoutAnimation();
            return "idle";
        }

        if ("dead".equals(state) && bossesRiseIntroSkipper$isBossPhase(3)) {
            bossesRiseIntroSkipper$pendingSkippedFinalDeath = true;
        }

        return state;
    }

    @Inject(method = "shouldCancelDeath", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipFinalDeathAnimation(CallbackInfoReturnable<Boolean> cir) {
        if (!Config.shouldSkipKnightCinematics()) {
            return;
        }

        if (bossesRiseIntroSkipper$isCurrentState("dead")) {
            bossesRiseIntroSkipper$clearCinematicFlags();
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "hurt", at = @At("RETURN"), remap = false)
    private void bossesRiseIntroSkipper$finishSkippedTransitionsAfterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.shouldSkipKnightCinematics()) {
            return;
        }

        if (bossesRiseIntroSkipper$pendingSkippedFinalDeath) {
            bossesRiseIntroSkipper$pendingSkippedFinalDeath = false;
            bossesRiseIntroSkipper$finishFinalDeathWithoutAnimation();
        }
    }

    @Inject(method = "lambda$tick$4", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bossesRiseIntroSkipper$skipIntroPlayerVariables(Player player, CallbackInfo ci) {
        if (Config.shouldSkipKnightCinematics()) {
            ci.cancel();
        }
    }

    @Inject(method = "isCinematic", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$disableCinematicFlag(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipKnightCinematics()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("RETURN"), remap = false)
    private void bossesRiseIntroSkipper$clearQueuedCinematicFlag(CallbackInfo ci) {
        if (!Config.shouldSkipKnightCinematics()) {
            return;
        }

        SynchedEntityData entityData = ((Entity) (Object) this).getEntityData();
        if (entityData.get(DATA_CINEMATIC)) {
            bossesRiseIntroSkipper$clearCinematicFlags();
        }

        if (bossesRiseIntroSkipper$pendingSkippedFakeDeath) {
            if (bossesRiseIntroSkipper$fakeDeathSkipDelay > 0) {
                bossesRiseIntroSkipper$fakeDeathSkipDelay--;
                return;
            }

            bossesRiseIntroSkipper$pendingSkippedFakeDeath = false;
            bossesRiseIntroSkipper$finishResurrectionWithoutAnimation();
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$finishIntroWithoutAnimation() {
        SynchedEntityData entityData = ((Entity) (Object) this).getEntityData();
        entityData.set(AbstractBossEntity.DATA_BOSS_PHASE, 0);
        entityData.set(DATA_INTRO_ATTACK, 0);
        bossesRiseIntroSkipper$clearCinematicFlags();
        ((AbstractBossEntity) (Object) this).setCinematicBlackScreen(false);
        ((AbstractBossEntity) (Object) this).setBarVisible(true);
    }

    @Unique
    private void bossesRiseIntroSkipper$finishResurrectionWithoutAnimation() {
        UnderworldKnightEntity knight = (UnderworldKnightEntity) (Object) this;
        SynchedEntityData entityData = knight.getEntityData();
        entityData.set(AbstractBossEntity.DATA_BOSS_PHASE, 2);
        entityData.set(DATA_TRANSFORMED, true);
        BossHandling.updateBossType(((AbstractBossEntityAccessor) this).bossesRiseIntroSkipper$getBossEvent(), 7);
        this.hpGate75 = true;
        this.hpGate50 = true;
        this.hpGate25 = true;
        this.setImmuneStacks(1);
        knight.setHealth(knight.getMaxHealth());
        bossesRiseIntroSkipper$clearCinematicFlags();
        this.setState("idle");
    }

    @Unique
    private void bossesRiseIntroSkipper$finishFakeDeathWithoutAnimation() {
        SynchedEntityData entityData = ((Entity) (Object) this).getEntityData();
        entityData.set(AbstractBossEntity.DATA_BOSS_PHASE, 1);
        this.setImmuneStacks(1);
        bossesRiseIntroSkipper$pendingSkippedFakeDeath = true;
        bossesRiseIntroSkipper$fakeDeathSkipDelay = 1;
    }

    @Unique
    private void bossesRiseIntroSkipper$clearCinematicFlags() {
        SynchedEntityData entityData = ((Entity) (Object) this).getEntityData();
        entityData.set(DATA_UNDEAD_CINEMATIC, 0);
        entityData.set(DATA_CINEMATIC, false);
        entityData.set(DATA_INTRO_ATTACK, 0);
        ((AbstractBossEntity) (Object) this).setCinematicBlackScreen(false);
    }

    @Unique
    private void bossesRiseIntroSkipper$finishFinalDeathWithoutAnimation() {
        UnderworldKnightEntity knight = (UnderworldKnightEntity) (Object) this;
        bossesRiseIntroSkipper$clearCinematicFlags();
        LivingEntity sourceEntity = knight.getSourceEntity();
        DamageSource damageSource = sourceEntity instanceof Player player
                ? knight.damageSources().playerAttack(player)
                : knight.damageSources().fellOutOfWorld();
        knight.hurt(damageSource, 999.0F);
    }

    @Unique
    private boolean bossesRiseIntroSkipper$isBossPhase(int phase) {
        return ((Entity) (Object) this).getEntityData().get(AbstractBossEntity.DATA_BOSS_PHASE) == phase;
    }

    @Unique
    private boolean bossesRiseIntroSkipper$isCurrentState(String stateName) {
        AbstractBossEntity.State state = ((UnderworldKnightEntity) (Object) this).getState();
        return state != null && stateName.equals(state.name());
    }
}
