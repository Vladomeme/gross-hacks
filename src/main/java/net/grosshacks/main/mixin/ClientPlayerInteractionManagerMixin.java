package net.grosshacks.main.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.grosshacks.main.GrossHacks;
import net.grosshacks.main.GrossHacksConfig;
import net.grosshacks.main.util.MixinUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//config -> disableBlockInteractions, disablePotionMining
@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin implements MixinUtil {

	@Shadow private GameMode gameMode;
	@Shadow @Final private MinecraftClient client;
	@Shadow @Final private ClientPlayNetworkHandler networkHandler;

	@Unique private final GrossHacksConfig config = GrossHacksConfig.INSTANCE;
	@Unique boolean skipped = false;
	@Unique int armTimer = 0;

	@Unique
	public void gh$tick() {
		if (config.interactionKeyArmMode) {
			if (GrossHacks.interactionKey.wasPressed()) armTimer = 100;
			if (armTimer > 0) {
				armTimer--;
				if (armTimer != 0)
					client.inGameHud.setOverlayMessage(Text.of("§eBlock interactions enabled: " + (armTimer / 20 + 1) + " s."), false);
				else
					client.inGameHud.setOverlayMessage(Text.of(""), false);
			}
		}
	}

	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"))
	private void gh$interactBlock(ClientPlayerInteractionManager instance, ClientWorld world, SequencedPacketCreator packetCreator, Operation<Void> original) {
		if (!config.disableBlockInteractions) {
			original.call(instance, world, packetCreator);
			return;
		}

		PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket) packetCreator.predict(1);
		if (!skipped) {
			try (PendingUpdateManager pendingUpdateManager = ((ClientWorldAccessor) world).gh$getPendingUpdateManager().incrementSequence()) {
				int i = pendingUpdateManager.getSequence();
				networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(packet.getHand(), packet.getBlockHitResult(), i));
			}
		}
	}

	@WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;"))
	private ItemActionResult gh$interactBlockInternal(BlockState blockState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ItemActionResult> original) {
		if (config.disableBlockInteractions && gameMode != GameMode.ADVENTURE && !player.getMainHandStack().isEmpty()) {
			skipped = false;
			//bypass keybind
			if (GrossHacks.interactionKey.isPressed() || (config.interactionKeyArmMode && armTimer != 0)) {
				((KeyBindingAccessor) GrossHacks.interactionKey).gh$reset();
				if (armTimer != 0) {
					armTimer = 0;
					client.inGameHud.setOverlayMessage(Text.of(""), false);
				}
				return original.call(blockState, stack, world, player, hand, hitResult);
			}
			//default exceptions
			if (blockState.isIn(BlockTags.SHULKER_BOXES) || blockState.isOf(Blocks.CHEST)
					|| blockState.isOf(Blocks.TRAPPED_CHEST)
					|| blockState.isOf(Blocks.ENDER_CHEST)) {
				return original.call(blockState, stack, world, player, hand, hitResult);
			}
			//optional exceptions
			if (config.useInteractionExceptions &&
					(blockState.isIn(BlockTags.DOORS)
					|| blockState.isIn(BlockTags.TRAPDOORS)
					|| blockState.isIn(BlockTags.BUTTONS)
					|| blockState.isIn(BlockTags.FENCE_GATES)
					|| blockState.isIn(BlockTags.ANVIL)
					|| blockState.isOf(Blocks.LEVER))) {
				return original.call(blockState, stack, world, player, hand, hitResult);
			}
			//barrel exception
			if (config.barrelInteractions && blockState.isOf(Blocks.BARREL)) {
				return original.call(blockState, stack, world, player, hand, hitResult);
			}
			skipped = true;
			return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
		}
		skipped = false;
		return original.call(blockState, stack, world, player, hand, hitResult);
	}

	@Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
	private void gh$attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		assert client.player != null;
		if (config.disablePotionMining && client.player.getMainHandStack().isOf(Items.SPLASH_POTION)) {
			cir.setReturnValue(false);
		}
	}
}
