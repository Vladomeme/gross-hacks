package net.grosshacks.main.mixin;

import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.feature.Misc;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//config -> nightmareTimer
@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

	@Inject(method = "onDeath", at = @At(value = "TAIL"))
	private void gh$onDeath(DamageSource damageSource, CallbackInfo ci) {
		if (GrossHacksConfig.INSTANCE.nightmareTimer)
			Misc.NightmareTimer.setTicks(1200);
	}
}
