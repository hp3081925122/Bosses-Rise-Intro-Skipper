package org.hp.bosses_rise_intro_skipper.mixin;

import net.minecraft.server.level.ServerBossEvent;
import net.unusual.block_factorys_bosses.entity.boss.AbstractBossEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value={AbstractBossEntity.class}, remap=false)
public interface AbstractBossEntityAccessor {
    @Accessor(value="bossEvent", remap=false)
    public ServerBossEvent bossesRiseIntroSkipper$getBossEvent();
}
