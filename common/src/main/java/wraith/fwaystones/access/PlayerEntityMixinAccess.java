package wraith.fwaystones.access;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import wraith.fwaystones.block.WaystoneBlockEntity;
import wraith.fwaystones.util.SearchType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public interface PlayerEntityMixinAccess extends PlayerAccess {

	void discoverWaystone(WaystoneBlockEntity waystone);

	void discoverWaystone(String hash);

	void discoverWaystone(String hash, boolean sync);
	void addFavoriteWaystone(WaystoneBlockEntity waystone);
	void addFavoriteWaystone(String hash);
	void removeFavoriteWaystone(WaystoneBlockEntity waystone);
	void removeFavoriteWaystone(String hash);

	boolean hasDiscoveredWaystone(WaystoneBlockEntity waystone);

	void forgetWaystone(WaystoneBlockEntity waystone);

	void forgetWaystone(String hash);

	void forgetWaystone(String hash, boolean sync);

	void syncData();

	Set<String> getDiscoveredWaystones();
	Set<String> getFavoritedWaystones();

	ArrayList<String> getWaystonesSorted();

	void learnWaystones(Player player);

	void fromTagW(CompoundTag tag);

	CompoundTag toTagW(CompoundTag tag);

	boolean shouldViewGlobalWaystones();

	boolean shouldViewDiscoveredWaystones();

	void toggleViewGlobalWaystones();

	void toggleViewDiscoveredWaystones();

	boolean hasDiscoveredWaystone(String hash);

	void discoverWaystones(HashSet<String> toLearn);

	void forgetWaystones(HashSet<String> toForget);

	int getTeleportCooldown();

	void setTeleportCooldown(int cooldown);

	void forgetAllWaystones();

	boolean autofocusWaystoneFields();

	void toggleAutofocusWaystoneFields();

	boolean sortByFavoriteWaystones();

	void toggleSortByFavoriteWaystones();

	SearchType getSearchType();

	void setSearchType(SearchType searchType);
}
