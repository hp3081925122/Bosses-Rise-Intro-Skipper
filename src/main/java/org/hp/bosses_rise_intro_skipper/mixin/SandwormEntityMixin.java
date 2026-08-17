package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.world.entity.player.Player;
import net.unusual.block_factorys_bosses.entity.boss.sandworm.SandwormEntity;
import net.unusual.block_factorys_bosses.state.ActiveState;
import net.unusual.block_factorys_bosses.state.StateController;
import net.unusual.block_factorys_bosses.state.StateRef;
import org.hp.bosses_rise_intro_skipper.Config;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SandwormEntity.class, remap = false)
public class SandwormEntityMixin {
    @Shadow(remap = false) @Final private static StateRef<SandwormEntity> IDLE_STATE;

    @Redirect(method = "startFight", at = @At(value = "INVOKE", target = "Lnet/unusual/block_factorys_bosses/state/StateController;replaceAll(Lnet/unusual/block_factorys_bosses/state/StateRef;)Lnet/unusual/block_factorys_bosses/state/ActiveState;", remap = false), remap = false)
    private ActiveState<SandwormEntity> bossesRiseIntroSkipper$replaceIntroState(StateController<SandwormEntity> controller, StateRef<SandwormEntity> stateRef) {
        return controller.replaceAll(Config.shouldSkipSandwormCinematics() ? IDLE_STATE : stateRef);
    }

    @Redirect(method = "startFight", at = @At(value = "INVOKE", target = "Lnet/unusual/block_factorys_bosses/state/StateController;push(Lnet/unusual/block_factorys_bosses/state/StateRef;)Lnet/unusual/block_factorys_bosses/state/ActiveState;", remap = false), remap = false)
    private ActiveState<SandwormEntity> bossesRiseIntroSkipper$skipQueuedIntroStates(StateController<SandwormEntity> controller, StateRef<SandwormEntity> stateRef) {
        return Config.shouldSkipSandwormCinematics() ? controller.getActiveState() : controller.push(stateRef);
    }

    @Inject(method = "lambda$startFight$8", at = @At("HEAD"), cancellable = true, remap = false)
    private static void bossesRiseIntroSkipper$skipIntroPlayerVariables(Player player, CallbackInfo ci) {
        if (Config.shouldSkipSandwormCinematics()) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldCancelDeath", at = @At("HEAD"), cancellable = true, remap = false)
    private void bossesRiseIntroSkipper$skipDeathCinematic(CallbackInfoReturnable<Boolean> cir) {
        if (Config.shouldSkipSandwormCinematics()) {
            cir.setReturnValue(false);
        }
    }
}
