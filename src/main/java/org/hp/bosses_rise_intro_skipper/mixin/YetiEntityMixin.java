package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.unusual.block_factorys_bosses.capability.entity.PlayerVariablesCap;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.yeti.YetiEntity;
import org.hp.bosses_rise_intro_skipper.BossesRiseIntroSkipper;
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

@Mixin(value={YetiEntity.class}, remap=false)
public class YetiEntityMixin {
    @Shadow(remap=false)
    @Final
    public static EntityDataAccessor<Integer> DATA_IS_ENRAGED;

    @ModifyVariable(method={"setState"}, at=@At(value="HEAD"), argsOnly=true, remap=false)
    private String bossesRiseIntroSkipper$skipCinematicState(String state) {
        if (!Config.shouldSkipYetiCinematics() || !YetiEntityMixin.bossesRiseIntroSkipper$isCinematicState(state)) {
            return state;
        }
        if (YetiEntityMixin.bossesRiseIntroSkipper$isIntroState(state)) {
            this.bossesRiseIntroSkipper$finishIntroWithoutAnimation();
        }
        return "IDLE";
    }

    @Inject(method={"baseTick"}, at={@At(value="HEAD")}, remap=true)
    private void bossesRiseIntroSkipper$finishEnrageWithoutAnimation(CallbackInfo ci) {
        if (!Config.shouldSkipYetiCinematics()) {
            return;
        }
        YetiEntity yeti = (YetiEntity)(Object)this;
        SynchedEntityData entityData = yeti.getEntityData();
        int enrageState = (Integer)entityData.get(DATA_IS_ENRAGED);
        if (enrageState == 3 || enrageState == 4) {
            entityData.set(DATA_IS_ENRAGED, 5);
        }
        AbstractBossEntity.State state = yeti.getState();
        if (enrageState == 1 && state != null && "IDLE".equals(state.name())) {
            this.bossesRiseIntroSkipper$finishIntroWithoutAnimation();
        }
        if (state != null && YetiEntityMixin.bossesRiseIntroSkipper$isEnragedState(state.name())) {
            yeti.setState("IDLE");
        }
    }

    @Inject(method={"isCinematic"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void bossesRiseIntroSkipper$disableCinematicFlag(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipYetiCinematics()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method={"hurt"}, at={@At(value="RETURN")}, remap=true)
    private void bossesRiseIntroSkipper$finishDeathAfterHurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (!Config.shouldSkipYetiCinematics()) {
            return;
        }
        YetiEntity yeti = (YetiEntity)(Object)this;
        if (yeti.level().isClientSide()) {
            return;
        }
        this.bossesRiseIntroSkipper$finishDeathWithoutAnimation((Boolean)cir.getReturnValue());
    }

    @Redirect(method={"lambda$hurt$23"}, at=@At(value="INVOKE", target="Lnet/minecraft/world/entity/LivingEntity;addEffect(Lnet/minecraft/world/effect/MobEffectInstance;)Z", remap=true), remap=false)
    private static boolean bossesRiseIntroSkipper$skipEnrageEffect(LivingEntity livingEntity, MobEffectInstance effect) {
        return !Config.shouldSkipYetiCinematics() && livingEntity.addEffect(effect);
    }

    @Unique
    private static boolean bossesRiseIntroSkipper$isCinematicState(String state) {
        return YetiEntityMixin.bossesRiseIntroSkipper$isIntroState(state) || YetiEntityMixin.bossesRiseIntroSkipper$isEnragedState(state);
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
        YetiEntity yeti = (YetiEntity)(Object)this;
        SynchedEntityData entityData = yeti.getEntityData();
        int enrageState = (Integer)entityData.get(DATA_IS_ENRAGED);
        if (enrageState < 2) {
            this.bossesRiseIntroSkipper$clearIntroIce();
            this.bossesRiseIntroSkipper$resetBossNoHit();
            entityData.set(DATA_IS_ENRAGED, 2);
            ((AbstractBossEntity)(Object)this).setBarVisible(true);
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$resetBossNoHit() {
        YetiEntity yeti = (YetiEntity)(Object)this;
        if (!(yeti.level() instanceof ServerLevel)) {
            return;
        }
        yeti.forEachNearbyPlayer(32.0, player -> PlayerVariablesCap.fromPlayer(player).ifPresent(playerVars -> {
            playerVars.setBossNoHit(true);
            playerVars.syncPlayerVariables(player);
        }));
        BossesRiseIntroSkipper.LOGGER.debug("Skipped yeti intro for entity {}, reset boss no-hit for nearby players", yeti.getId());
    }

    @Unique
    private void bossesRiseIntroSkipper$clearIntroIce() {
        YetiEntity yeti = (YetiEntity)(Object)this;
        Level level = yeti.level();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        BlockPos origin = yeti.blockPosition();
        for (int x = -4; x <= 4; ++x) {
            for (int y = 0; y <= 6; ++y) {
                for (int z = -4; z <= 4; ++z) {
                    BlockPos blockPos = origin.offset(x, y, z);
                    if (!serverLevel.getBlockState(blockPos).is(BlockTags.ICE)) continue;
                    serverLevel.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$finishDeathWithoutAnimation(boolean hurtResult) {
        if (!hurtResult) {
            return;
        }
        YetiEntity yeti = (YetiEntity)(Object)this;
        SynchedEntityData entityData = yeti.getEntityData();
        AbstractBossEntity.State state = yeti.getState();
        if (state == null || !"DEATH".equals(state.name()) || yeti.getHealth() > 0.1f || (Integer)entityData.get(DATA_IS_ENRAGED) == 6) {
            return;
        }
        entityData.set(DATA_IS_ENRAGED, 6);
        entityData.set(AbstractBossEntity.DATA_DIE_ANIMTIME, 0);
        entityData.set(AbstractBossEntity.DATA_ATTACK_ANIMTIME, 0);
        entityData.set(AbstractBossEntity.DATA_HIT_ANIMTIME, 0);
        yeti.setNoAi(true);
        ((AbstractBossEntity)(Object)this).setBarVisible(false);
        yeti.discard();
    }
}
