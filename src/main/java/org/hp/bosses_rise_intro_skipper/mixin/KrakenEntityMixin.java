package org.hp.bosses_rise_intro_skipper.mixin;

import net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenCinematicEntity;
import net.unusual.block_factorys_bosses.entity.boss.kraken.KrakenEntity;
import net.unusual.block_factorys_bosses.util.BossHandling;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = KrakenEntity.class, remap = false)
public abstract class KrakenEntityMixin {
    @Shadow(remap = false) private boolean bossPhaseInitialized;
    @Shadow(remap = false) private KrakenCinematicEntity cinematicEntity;
    @Shadow(remap = false) private java.util.UUID cinematicEntityUUID;

    @Shadow(remap = false) public abstract void setBossPhase(int bossPhase);

    @Shadow(remap = false) protected abstract int getBossType();

    @Invoker(value = "initializeFirstPhase", remap = false)
    protected abstract void bossesRiseIntroSkipper$initializeFirstPhase();

    @Invoker(value = "initializeSecondPhase", remap = false)
    protected abstract void bossesRiseIntroSkipper$initializeSecondPhase();

    @Inject(method = "tryInitializeBossPhase", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipCinematicPhases(CallbackInfo ci) {
        if (!Config.shouldSkipKrakenIntro() || this.bossPhaseInitialized) {
            return;
        }

        int bossPhase = ((AbstractStateBossEntity) (Object) this).getBossPhase();
        if (bossPhase == -1) {
            this.bossesRiseIntroSkipper$removeCinematicEntity();
            this.setBossPhase(0);
            this.bossPhaseInitialized = true;
            this.bossesRiseIntroSkipper$initializeFirstPhase();
            BossHandling.updateBossType(((AbstractStateBossEntityAccessor) this).bossesRiseIntroSkipper$getBossEvent(), this.getBossType());
            ci.cancel();
            return;
        }

        if (bossPhase == 1) {
            this.bossesRiseIntroSkipper$removeCinematicEntity();
            this.setBossPhase(2);
            this.bossPhaseInitialized = true;
            this.bossesRiseIntroSkipper$initializeSecondPhase();
            BossHandling.updateBossType(((AbstractStateBossEntityAccessor) this).bossesRiseIntroSkipper$getBossEvent(), this.getBossType());
            ci.cancel();
        }
    }

    private void bossesRiseIntroSkipper$removeCinematicEntity() {
        if (this.cinematicEntity == null) {
            return;
        }

        this.cinematicEntity.discard();
        this.cinematicEntity = null;
        this.cinematicEntityUUID = null;
    }
}
