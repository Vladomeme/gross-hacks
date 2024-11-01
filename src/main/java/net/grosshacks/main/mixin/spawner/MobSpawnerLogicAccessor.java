package net.grosshacks.main.mixin.spawner;

import net.minecraft.block.spawner.MobSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MobSpawnerLogic.class)
public interface MobSpawnerLogicAccessor {

    @Accessor("requiredPlayerRange")
    int getRequiredPlayerRange();
}


