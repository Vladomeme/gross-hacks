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
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
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
	@Unique boolean apply = false;
	@Unique boolean skipPacket = false;
	@Unique boolean mainHand = false;
	//blocking offhand interactions is required because if the mainhand interactions pass and packet is cancelled,
	//then the limited offhand interaction packet may still go through
	@Unique boolean blockOffhand = false;
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

	//cancelling the block interaction packet
	@WrapOperation(method = "interactBlock", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/network/ClientPlayerInteractionManager;sendSequencedPacket(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/network/SequencedPacketCreator;)V"))
	private void gh$interactBlock(ClientPlayerInteractionManager instance, ClientWorld world, SequencedPacketCreator packetCreator, Operation<Void> original) {
		if (!config.disableBlockInteractions) {
			original.call(instance, world, packetCreator);
			return;
		}

		PlayerInteractBlockC2SPacket packet = (PlayerInteractBlockC2SPacket) packetCreator.predict(1);
        if (skipPacket) {
			skipPacket = false;
			blockOffhand = mainHand;
			mainHand = false;
		}
        else {
            try (PendingUpdateManager pendingUpdateManager = ((ClientWorldAccessor) world).gh$getPendingUpdateManager().incrementSequence()) {
                int i = pendingUpdateManager.getSequence();
                networkHandler.sendPacket(new PlayerInteractBlockC2SPacket(packet.getHand(), packet.getBlockHitResult(), i));
            }
        }
    }

	//at HEAD, check if interaction block should be applied
	@Inject(method = "interactBlockInternal", at = @At(value = "HEAD"), cancellable = true)
	private void gh$interactBlockInternal$head(ClientPlayerEntity player, Hand hand, BlockHitResult hitResult, CallbackInfoReturnable<ActionResult> cir) {
		if (hand == Hand.OFF_HAND && blockOffhand) {
			skipPacket = true;
			cir.setReturnValue(ActionResult.FAIL);
		}
		if (hand == Hand.MAIN_HAND) mainHand = true;

		apply = false;
		if (config.disableBlockInteractions && gameMode == GameMode.SURVIVAL && !player.getMainHandStack().isEmpty()) {
			if (GrossHacks.interactionKey.isPressed() || (config.interactionKeyArmMode && armTimer != 0)) { //bypass keybind
				((KeyBindingAccessor) GrossHacks.interactionKey).gh$reset();
				if (config.interactionKeyArmMode) {
					armTimer = 0;
					client.inGameHud.setOverlayMessage(Text.of(""), false);
				}
			}
			else apply = true;
		}
	}

	//item-block specific interactions
	@WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;onUseWithItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ItemActionResult;"))
	private ItemActionResult gh$interactBlockInternal$onUseWithItem(BlockState blockState, ItemStack stack, World world, PlayerEntity player, Hand hand, BlockHitResult hitResult, Operation<ItemActionResult> original) {
		if (apply) {
			//excluded interactions
			if (blockState.isIn(BlockTags.ALL_SIGNS)
					|| blockState.isIn(BlockTags.CAULDRONS)
					|| blockState.isOf(Blocks.DECORATED_POT)
					|| blockState.isOf(Blocks.CHISELED_BOOKSHELF)) {
				skipPacket = true;
				return ItemActionResult.SKIP_DEFAULT_BLOCK_INTERACTION;
			}
		}
		return original.call(blockState, stack, world, player, hand, hitResult);
	}

	//right click interactions
	@WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/block/BlockState;onUse(Lnet/minecraft/world/World;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/hit/BlockHitResult;)Lnet/minecraft/util/ActionResult;"))
	private ActionResult gh$interactBlockInternal$onUse(BlockState blockState, World world, PlayerEntity player, BlockHitResult hitResult, Operation<ActionResult> original) {
		if (apply) {
			if (skipPacket) return ActionResult.PASS;
			//default exceptions
			if (blockState.isIn(BlockTags.SHULKER_BOXES)
					|| blockState.isOf(Blocks.CHEST)
					|| blockState.isOf(Blocks.TRAPPED_CHEST)
					|| blockState.isOf(Blocks.ENDER_CHEST)) {
				return original.call(blockState, world, player, hitResult);
			}
			//optional exceptions
			if (config.useInteractionExceptions &&
					(blockState.isIn(BlockTags.DOORS)
					|| blockState.isIn(BlockTags.TRAPDOORS)
					|| blockState.isIn(BlockTags.BUTTONS)
					|| blockState.isIn(BlockTags.FENCE_GATES)
					|| blockState.isIn(BlockTags.ANVIL)
					|| blockState.isOf(Blocks.LEVER))) {
				return original.call(blockState, world, player, hitResult);
			}
			//barrel exception
			if (config.barrelInteractions && blockState.isOf(Blocks.BARREL)) {
				return original.call(blockState, world, player, hitResult);
			}
			skipPacket = true;
			return ActionResult.PASS;
		}
		return original.call(blockState, world, player, hitResult);
	}

	//preserve packet if an item is used
	@WrapOperation(method = "interactBlockInternal", at = @At(value = "INVOKE",
			target = "Lnet/minecraft/item/ItemStack;useOnBlock(Lnet/minecraft/item/ItemUsageContext;)Lnet/minecraft/util/ActionResult;"))
	private ActionResult gh$interactBlockInternal$useOnBlock(ItemStack stack, ItemUsageContext context, Operation<ActionResult> original) {
		ActionResult result = original.call(stack, context);
		if (result.isAccepted()) skipPacket = false;
		return result;
	}

	@Inject(method = "attackBlock", at = @At(value = "HEAD"), cancellable = true)
	private void gh$attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
		assert client.player != null;
		if (config.disablePotionMining && client.player.getMainHandStack().isOf(Items.SPLASH_POTION)) {
			cir.setReturnValue(false);
		}
	}
}
