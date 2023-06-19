package wraith.fwaystones.screen;

import com.mojang.authlib.GameProfile;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import wraith.fwaystones.Waystones;
import wraith.fwaystones.access.PlayerEntityMixinAccess;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.registry.MenuRegister;
import wraith.fwaystones.util.PacketHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.UUID;
import java.util.function.Function;

public class WaystoneBlockScreenHandler extends UniversalWaystoneScreenHandler {

	private final boolean isClient;
	private String name;
	private String hash;
	private UUID owner;
	private boolean isGlobal;
	private Function<Player, Boolean> canUse = null;
	private String ownerName = "";

	public WaystoneBlockScreenHandler(int syncId, WaystoneBlockEntity waystoneEntity, Player player) {
		super(MenuRegister.WAYSTONE_MENU.get(), syncId, player);
		this.hash = waystoneEntity.getHash();
		this.name = waystoneEntity.getWaystoneName();
		this.owner = waystoneEntity.getOwner();
		this.isGlobal = waystoneEntity.isGlobal();
		this.canUse = waystoneEntity::canAccess;
		this.isClient = player.getLevel().isClientSide;
		this.ownerName = waystoneEntity.getOwnerName();
		updateWaystones(player);
	}

	public WaystoneBlockScreenHandler(int syncId, Inventory playerInventory, FriendlyByteBuf buf) {
		super(MenuRegister.WAYSTONE_MENU.get(), syncId, playerInventory.player);
		this.isClient = playerInventory.player.getLevel().isClientSide;
		CompoundTag tag = buf.readNbt();
		if (tag != null) {
			this.hash = tag.getString("waystone_hash");
			this.name = tag.getString("waystone_name");
			if (tag.contains("waystone_owner")) {
				this.owner = tag.getUUID("waystone_owner");
			}
			if (tag.contains("waystone_owner_name")) {
				this.ownerName = tag.getString("waystone_owner_name");
			}
			this.isGlobal = tag.getBoolean("waystone_is_global");
		}
		updateWaystones(player);
	}

	@Override
	public void onForget(String waystone) {
		if (this.hash.equals(waystone)) {
			closeScreen();
		}
	}

	@Override
	public void updateWaystones(Player player) {
		super.updateWaystones(player);
		if (!player.getLevel().isClientSide) {
			return;
		}
		if (!Waystones.STORAGE.containsHash(this.hash)) {
			closeScreen();
		}
	}

	@Override
	public boolean stillValid(Player player) {
		return canUse != null ? canUse.apply(player) : true;
	}

	public String getWaystone() {
		return this.hash;
	}

	public void toggleGlobal() {
		if (!isClient) {
			return;
		}
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
		CompoundTag tag = new CompoundTag();
		tag.putString("waystone_hash", this.hash);
		if (this.owner != null) {
			tag.putUUID("waystone_owner", this.owner);
		}
		packet.writeNbt(tag);
		NetworkManager.sendToServer(PacketHandler.TOGGLE_GLOBAL_WAYSTONE, packet);
		this.isGlobal = !this.isGlobal;
	}

	public boolean isOwner(Player player) {
		return this.owner != null && this.owner.equals(player.getUUID());
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isGlobal() {
		return this.isGlobal;
	}

	public UUID getOwner() {
		return this.owner;
	}

	public String getOwnerName() {
		return this.ownerName == null ? "" : this.ownerName;
	}

	public void removeOwner() {
		this.owner = null;
		this.ownerName = null;
	}

	public boolean hasOwner() {
		return this.owner != null;
	}












	protected ArrayList<PlayerData> sortedPlayers = new ArrayList<>();
	protected ArrayList<PlayerData> filteredOnlinePlayers = new ArrayList<>();
	protected String filterPlayers = "";

	public int getPlayersCount() {// TODO:
		return this.filteredOnlinePlayers.size();
	}
	public void updatePlayers(Player player){
		if (!player.getLevel().isClientSide) {
			return;
		}
		UUID self = player.getUUID();
		this.sortedPlayers = new ArrayList<>();
		Collection<PlayerInfo> playerInfos = Minecraft.getInstance().getConnection().getOnlinePlayers();
		for (PlayerInfo playerInfo : playerInfos){


			GameProfile gameProfile = playerInfo.getProfile();
			if (gameProfile.isComplete() && playerInfo != null && gameProfile.getId() != self) {
				sortedPlayers.add(new PlayerData(
						gameProfile.getName(),
						playerInfo.getSkinLocation(),
						gameProfile.getId()
				));
			}
		}
		this.sortedPlayers.sort(Comparator.comparing(PlayerData::name));
		filterPlayers();
	}

	public void filterPlayers() {
		this.filteredOnlinePlayers.clear();
		var searchType = ((PlayerEntityMixinAccess) player).getSearchType();
		for (PlayerData player : this.sortedPlayers) {
			String name = player.name().toLowerCase();
			if ("".equals(this.filterPlayers) || searchType.match(name, filterPlayers)) {
				filteredOnlinePlayers.add(player);
			}
		}
	}
	public ResourceLocation getSkin(int index){
		return this.filteredOnlinePlayers.get(index).skin();
	}
	public String getPlayerName(int index){
		return this.filteredOnlinePlayers.get(index).name();
	}



	public record PlayerData(String name, ResourceLocation skin, UUID id) {}
}
