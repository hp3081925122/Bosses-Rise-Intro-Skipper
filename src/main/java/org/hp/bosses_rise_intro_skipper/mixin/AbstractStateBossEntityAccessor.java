package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.server.level.ServerBossEvent;
import net.unusual.block_factorys_bosses.entity.boss.AbstractStateBossEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractStateBossEntity.class, remap = false)
public interface AbstractStateBossEntityAccessor {
    @Accessor(value = "bossEvent", remap = false)
    ServerBossEvent bossesRiseIntroSkipper$getBossEvent();
}
