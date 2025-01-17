package wraith.fwaystones.util;

import dev.architectury.networking.NetworkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import wraith.fwaystones.Waystones;
import wraith.fwaystones.access.PlayerEntityMixinAccess;
import wraith.fwaystones.block.WaystoneBlock;
import wraith.fwaystones.registry.ItemRegister;
import wraith.fwaystones.screen.UniversalWaystoneScreenHandler;

import java.util.HashSet;
import java.util.UUID;

public final class PacketHandler {
	public static final ResourceLocation FORGET_WAYSTONE = Utils.ID("forget_waystone");
	public static final ResourceLocation REMOVE_WAYSTONE_OWNER = Utils.ID("remove_waystone_owner");
	public static final ResourceLocation RENAME_WAYSTONE = Utils.ID("rename_waystone");
	public static final ResourceLocation REQUEST_PLAYER_SYNC = Utils.ID("request_player_waystone_update");
	public static final ResourceLocation SYNC_PLAYER = Utils.ID("sync_player");
	public static final ResourceLocation SYNC_PLAYER_FROM_CLIENT = Utils.ID("sync_player_from_client");
	public static final ResourceLocation TELEPORT_TO_WAYSTONE = Utils.ID("teleport_to_waystone");
	public static final ResourceLocation TOGGLE_GLOBAL_WAYSTONE = Utils.ID("toggle_global_waystone");
	public static final ResourceLocation VOID_REVIVE = Utils.ID("void_totem_revive");
	public static final ResourceLocation WAYSTONE_GUI_SLOT_CLICK = Utils.ID("waystone_gui_slot_click");
	public static final ResourceLocation WAYSTONE_PACKET = Utils.ID("waystone_packet");

	@Environment(EnvType.CLIENT)
	public static void registerS2CListeners() {
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, WAYSTONE_PACKET, (buffer, context) -> {
			var nbt = buffer.readNbt();
			Player player = context.getPlayer();
			context.queue(()->{
				if (Waystones.STORAGE == null) {
					Waystones.STORAGE = new WaystoneStorage(null);
				}
				Waystones.STORAGE.fromTag(nbt);
				if (player == null) {
					return;
				}
				HashSet<String> toForget = new HashSet<>();
				for (String hash : ((PlayerEntityMixinAccess) player).getDiscoveredWaystones()) {
					if (!Waystones.STORAGE.containsHash(hash)) {
						toForget.add(hash);
					}
				}
				((PlayerEntityMixinAccess) player).forgetWaystones(toForget);
				if (player.containerMenu instanceof UniversalWaystoneScreenHandler) {
					((UniversalWaystoneScreenHandler) player.containerMenu).updateWaystones(player);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_PLAYER, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			Player player = context.getPlayer();
			context.queue(()->{
				if (player != null) {
					((PlayerEntityMixinAccess) player).fromTagW(tag);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.S2C, VOID_REVIVE, (buffer, context) -> {
			Player player = context.getPlayer();
			if (player == null) {
				return;
			}
			context.queue(()->{
				Minecraft.getInstance().particleEngine.createTrackingEmitter(player, ParticleTypes.TOTEM_OF_UNDYING, 30);
				player.getLevel().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.TOTEM_USE, player.getSoundSource(), 1.0F, 1.0F);
				for (int i = 0; i < player.getInventory().getContainerSize(); ++i) {
					ItemStack playerStack = player.getInventory().getItem(i);
					if (playerStack.getItem() == ItemRegister.VOID_TOTEM.get()) {
						Minecraft.getInstance().gameRenderer.displayItemActivation(playerStack);
						break;
					}
				}
			});
		});
	}

	public static void registerC2SListeners() {
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, REMOVE_WAYSTONE_OWNER, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			Player player = context.getPlayer();
			context.queue(()->{
				if (tag == null || !tag.contains("waystone_hash") || !tag.contains("waystone_owner")) {
					return;
				}
				String hash = tag.getString("waystone_hash");
				UUID owner = tag.getUUID("waystone_owner");
				if ((player.getUUID().equals(owner) || player.hasPermissions(2))) {
					Waystones.STORAGE.setOwner(hash, null);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, WAYSTONE_GUI_SLOT_CLICK, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			Player player = context.getPlayer();
			context.queue(()->{
				if (tag == null || !tag.contains("sync_id") || !tag.contains("clicked_slot")) {
					return;
				}
				int syncId = tag.getInt("sync_id");
				int button = tag.getInt("clicked_slot");
				if (player.containerMenu.containerId == syncId) {
					player.containerMenu.clickMenuButton(player, button);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, RENAME_WAYSTONE, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			if (tag == null || !tag.contains("waystone_name") || !tag.contains("waystone_hash") || !tag.contains("waystone_owner")) {
				return;
			}
			String name = tag.getString("waystone_name");
			String hash = tag.getString("waystone_hash");
			UUID owner = tag.getUUID("waystone_owner");
			Player player = context.getPlayer();
			context.queue(()->{
				if (Waystones.STORAGE.containsHash(hash) &&
						((player.getUUID().equals(owner) &&
								owner.equals(Waystones.STORAGE.getWaystoneEntity(hash).getOwner())) ||
								player.hasPermissions(2))) {
					Waystones.STORAGE.renameWaystone(hash, name);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, FORGET_WAYSTONE, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			if (tag == null || !tag.contains("waystone_hash")) {
				return;
			}
			String hash = tag.getString("waystone_hash");
			context.queue(()->{
				((PlayerEntityMixinAccess) context.getPlayer()).forgetWaystone(hash);
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, REQUEST_PLAYER_SYNC, (buffer, context) -> {
			context.queue(()->{
				((PlayerEntityMixinAccess) context.getPlayer()).syncData();
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, TOGGLE_GLOBAL_WAYSTONE, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			if (tag == null || !tag.contains("waystone_hash") || !tag.contains("waystone_owner")) {
				return;
			}
			Player player = context.getPlayer();
			context.queue(()->{
				var permissionLevel = Waystones.CONFIG.global_mode_toggle_permission_levels;
				UUID owner = tag.getUUID("waystone_owner");
				String hash = tag.getString("waystone_hash");
				switch (permissionLevel) {
					case NONE -> {
						return;
					}
					case OP -> {
						if (!player.hasPermissions(2)) {
							return;
						}
					}
					case OWNER -> {
						if (!player.getUUID().equals(owner) || !owner.equals(Waystones.STORAGE.getWaystoneEntity(hash).getOwner())) {
							return;
						}
					}
				}
				if (Waystones.STORAGE.containsHash(hash)) {
					Waystones.STORAGE.toggleGlobal(hash);
				}
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, SYNC_PLAYER_FROM_CLIENT, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			context.queue(()->{
				((PlayerEntityMixinAccess) context.getPlayer()).fromTagW(tag);
			});
		});
		NetworkManager.registerReceiver(NetworkManager.Side.C2S, TELEPORT_TO_WAYSTONE, (buffer, context) -> {
			CompoundTag tag = buffer.readNbt();
			if (tag == null || !tag.contains("waystone_hash")) {
				return;
			}
			context.queue(()->{
				if (!tag.contains("waystone_hash")) {
					return;
				}
				String hash = tag.getString("waystone_hash");
				var waystone = Waystones.STORAGE.getWaystoneEntity(hash);
				if (waystone == null) {
					return;
				}
				if (waystone.getLevel() != null && !(waystone.getLevel().getBlockState(waystone.getBlockPos()).getBlock() instanceof WaystoneBlock)) {
					Waystones.STORAGE.removeWaystone(hash);
					waystone.getLevel().removeBlockEntity(waystone.getBlockPos());
				} else {
					waystone.teleportPlayer(context.getPlayer(), true);
				}
			});
		});
	}
}
