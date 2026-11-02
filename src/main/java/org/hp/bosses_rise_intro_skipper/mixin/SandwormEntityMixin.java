package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.unusual.block_factorys_bosses.configuration.ServerConfiguration;
import net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity;
import net.unusual.block_factorys_bosses.entity.boss.sandworm.SandwormEntity;
import net.unusual.block_factorys_bosses.state.ActiveState;
import net.unusual.block_factorys_bosses.state.StateController;
import net.unusual.block_factorys_bosses.state.StateRef;
import org.hp.bosses_rise_intro_skipper.BossesRiseIntroSkipper;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={SandwormEntity.class}, remap=false)
public class SandwormEntityMixin {
    @Shadow(remap=false)
    @Final
    private static StateRef<SandwormEntity> IDLE_STATE;

    @Shadow(remap=false)
    private void snapToGround() {
        throw new AssertionError();
    }

    @Shadow(remap=false)
    private void applyUndergroundEffects() {
        throw new AssertionError();
    }

    @Redirect(method={"startFight"}, at=@At(value="INVOKE", target="Lnet/unusual/block_factorys_bosses/state/StateController;replaceAll(Lnet/unusual/block_factorys_bosses/state/StateRef;)Lnet/unusual/block_factorys_bosses/state/ActiveState;", remap=false), remap=false)
    private ActiveState<SandwormEntity> bossesRiseIntroSkipper$replaceIntroState(StateController<SandwormEntity> controller, StateRef<SandwormEntity> stateRef) {
        if (!Config.shouldSkipSandwormCinematics()) {
            return controller.replaceAll(stateRef);
        }
        this.bossesRiseIntroSkipper$finishIntroWithoutAnimation();
        return controller.replaceAll(IDLE_STATE);
    }

    @Redirect(method={"startFight"}, at=@At(value="INVOKE", target="Lnet/unusual/block_factorys_bosses/state/StateController;push(Lnet/unusual/block_factorys_bosses/state/StateRef;)Lnet/unusual/block_factorys_bosses/state/ActiveState;", remap=false), remap=false)
    private ActiveState<SandwormEntity> bossesRiseIntroSkipper$skipQueuedIntroStates(StateController<SandwormEntity> controller, StateRef<SandwormEntity> stateRef) {
        return Config.shouldSkipSandwormCinematics() ? controller.getActiveState() : controller.push(stateRef);
    }

    @Inject(method={"shouldCancelDeath"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private void bossesRiseIntroSkipper$skipDeathCinematic(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipSandwormCinematics()) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private void bossesRiseIntroSkipper$finishIntroWithoutAnimation() {
        SandwormEntity sandworm = (SandwormEntity)(Object)this;
        this.snapToGround();
        sandworm.setHiddenUnderground(false);
        this.applyUndergroundEffects();
        sandworm.setDeltaMovement(Vec3.ZERO);
        sandworm.getAttribute(Attributes.MAX_HEALTH).setBaseValue(ServerConfiguration.SANDWORM_HEALTH.get());
        sandworm.getAttribute(Attributes.ARMOR).setBaseValue(ServerConfiguration.SANDWORM_ARMOR.get());
        sandworm.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(ServerConfiguration.SANDWORM_ATK.get());
        sandworm.getEntityData().set(AbstractStateBossEntity.DATA_BOSS_PHASE, 0);
        BossesRiseIntroSkipper.LOGGER.debug("Skipped sandworm intro for entity {}, boss phase set to 0", sandworm.getId());
    }
}
