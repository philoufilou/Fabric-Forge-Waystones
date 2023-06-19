package wraith.fwaystones.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Mod;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.phys.Vec3;
import wraith.fwaystones.Waystones;
import wraith.fwaystones.access.PlayerEntityMixinAccess;
import wraith.fwaystones.screen.element.*;
import wraith.fwaystones.util.Config;
import wraith.fwaystones.util.PacketHandler;
import wraith.fwaystones.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class UniversalWaystoneScreen extends AbstractContainerScreen<AbstractContainerMenu> {

	protected final Inventory inventory;
	protected final List<Base> elements = new ArrayList<>();
	///////////////////////////////////////////////////////////
	protected ResourceLocation textureA;
	protected ResourceLocation textureB;
	///////////////////////////////////////////////////////////
	private static final Component titleShare = Component.translatable("container." + Waystones.MOD_ID + ".share");
	private static final Component titleConfig = Component.translatable("container." + Waystones.MOD_ID + ".config");
	private Component dynamicTitle;
	protected int textPrimaryColor;
	protected int textSecondaryColor;
	///////////////////////////////////////////////////////////
	private EditBox searchField;
	private boolean mousePressed;
	private boolean mouseClicked;
	private boolean favoriteSysVisible;
	protected PageProperties selectedPage = PageProperties.MAIN;

	public UniversalWaystoneScreen(AbstractContainerMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
		this.inventory = inventory;
		this.imageWidth = 194;
		this.imageHeight = 194;
		this.favoriteSysVisible = Waystones.CONFIG.client_favorite_system;/*TODO:*/

		PageProperties.resetPages();
		prepareElements();
	}
	/// INIT & PAGE CHANGE /////////////////////////////////////////////////////
	protected void /*TODO:*/changePage(PageProperties page){
		this.selectedPage = page;
		switch (page){
			case MAIN -> {
				((UniversalWaystoneScreenHandler) menu).updateWaystones(inventory.player);
				//searchField.setFocused(((PlayerEntityMixinAccess) inventory.player).autofocusWaystoneFields());
			}
			case SHARE -> {
				((WaystoneBlockScreenHandler) menu).updatePlayers(inventory.player);
				//searchField.setFocused(((PlayerEntityMixinAccess) inventory.player).autofocusWaystoneFields());
			}
			case CONFIG -> {
				// TODO: use it? nameField.setFocused(((PlayerEntityMixinAccess) inventory.player).autofocusWaystoneFields());
			}
		}
		init();
	}
	@Override
	protected void /*TODO:*/init() {
		super.init();

		//this.searchField = new EditBox(this.font, this.leftPos + 50, this.topPos + 24, 105, 14, Component.literal("")) {
		//	@Override
		//	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		//		boolean bl = mouseX >= (double) this.getX() && mouseX < (double) (this.getX() + this.width) && mouseY >= (double) this.getY() && mouseY < (double) (this.getY() + this.height);
		//		if (bl && button == 1) {
		//			this.setValue("");
		//		}
		//		return super.mouseClicked(mouseX, mouseY, button);
		//	}
		//};
		//this.searchField.setMaxLength(16);
		//this.searchField.setTextColor(0xFFFFFF);
		//this.searchField.setVisible(true);
		//this.searchField.setBordered(false);
		//this.searchField.setCanLoseFocus(true);
		//this.searchField.setValue(selectedPage.getSearchFieldValue());
		//this.searchField.setResponder((s) -> {
		//	if(selectedPage != PageProperties.CONFIG){
		//		selectedPage.setScrollAmount(0);
		//		selectedPage.setScrollOffset(0);
		//	}
		//	selectedPage.setSearchFieldValue(this.searchField != null ? this.searchField.getValue() : "");
		//	((UniversalWaystoneScreenHandler) menu).setFilter(this.searchField != null ? this.searchField.getValue() : "");
		//	((UniversalWaystoneScreenHandler) menu).filterWaystones();
		//});
		//this.addWidget(this.searchField);


		this.dynamicTitle = updateTitle();
		this.titleLabelX = updateTitleLeft();
		setupElements();
	}
	protected int updateTitleLeft(){
		return (this.imageWidth-componentWidth(this.dynamicTitle))/2;
	}
	protected Component updateTitle(){
		if (selectedPage == PageProperties.CONFIG){
			return titleConfig;
		} else if (selectedPage == PageProperties.SHARE) {
			return titleShare;
		} else {
			return this.title;
		}
	}
	protected void setupElements() {
		for (Base element : elements) {
			element.setup();
		}
	}
	/// RENDER /////////////////////////////////////////////////////
	//@Override
	//public void render(PoseStack poseStack, int mouseX, int mouseY, float delta) {
	//	super.render(poseStack, mouseX, mouseY, delta);
	//	this.renderTooltip(poseStack, mouseX, mouseY);
	//}
	@Override
	protected void renderBg(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		this.renderBackground(poseStack);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		// Background
		RenderSystem.setShaderTexture(0, textureA);
		UniversalWaystoneScreen.blit(poseStack, this.leftPos-12, this.topPos-12, this.imageWidth+24, this.imageHeight+24, 0, 0, 218, 218, 218, 218);
		RenderSystem.setShaderTexture(0, textureB);
		this.renderBackgrounds(poseStack, delta, mouseX, mouseY);
		this.renderTexts(poseStack, delta, mouseX, mouseY);
		this.searchField.render(poseStack, mouseX, mouseY, delta);
		this.renderTooltips(poseStack, delta, mouseX, mouseY);
	}

	protected void renderBackgrounds(PoseStack poseStack, float delta, int mouseX, int mouseY){
		for (Base element : elements) {
			if (element instanceof Texture texture){
				texture.renderBackground(poseStack, delta, this.leftPos, this.topPos, mouseX, mouseY, this.mousePressed);
			}
		}
	}
	protected void renderTexts(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		for (Base element : elements) {
			if (element instanceof Texture texture){
				if(texture.isVisible()){
					texture.renderText(poseStack, this.leftPos, this.topPos, mouseX, mouseY);
				}
			}
		}
		//if (selectedPage == PageProperties.MAIN){
		//	Component txt = Component.translatable("fwaystones.gui.text.displayed_waystones", getTotalRows());
		//	this.font.draw(poseStack, txt, this.leftPos + (float) (this.imageWidth - componentWidth(txt)) /2, this.topPos + 173, textSecondaryColor);
		//}
	}
	protected void renderTooltips(PoseStack poseStack, float delta, int mouseX, int mouseY) {
		for (Base element : elements) {
			if (element instanceof Button button){
				button.renderTooltips(this, poseStack, this.leftPos, this.topPos, mouseX, mouseY);
			}
		}
	}




	@Override
	protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY) {
		this.font.draw(poseStack, this.dynamicTitle, this.titleLabelX, 9, textPrimaryColor);
	}
	/// Interaction ////////////////////////////////////////////////////////

	private void rowClicked(int id){
		if((this.menu).clickMenuButton(this.minecraft.player, id)){
			CompoundTag tag = new CompoundTag();
			tag.putInt("sync_id", menu.containerId);
			tag.putInt("clicked_slot", id);
			FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer()).writeNbt(tag);
			NetworkManager.sendToServer(PacketHandler.WAYSTONE_GUI_SLOT_CLICK, packet);
		}
	}
	@Override
	public void onClose() {
		super.onClose();
		FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
		packet.writeNbt(((PlayerEntityMixinAccess) inventory.player).toTagW(new CompoundTag()));
		NetworkManager.sendToServer(PacketHandler.SYNC_PLAYER_FROM_CLIENT, packet);
	}
	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
		if (this.shouldScroll()) {
			selectedPage.calcMouseScrolled(amount, this.getMaxScroll());
		}
		return true;
	}
	@Override
	public boolean mouseDragged(double mouseX, double mouseY, int btn, double deltaX, double deltaY) {
		if (this.mouseClicked && this.shouldScroll()) {
			this.selectedPage.calcMouseDragged(this.topPos, mouseY, this.getMaxScroll());
			return true;
		} else {
			return super.mouseDragged(mouseX, mouseY, btn, deltaX, deltaY);
		}
	}
	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int i) {
		this.mousePressed = true;
		if (i != 0) {
			return super.mouseClicked(mouseX, mouseY, i);
		}
		this.mouseClicked = false;
		for (Base element : elements) {
			if (element instanceof Button button){
				if (!button.isVisible() || button.isDisabled() || !button.isInBounds((int) mouseX - this.leftPos, (int) mouseY - this.topPos)) {
					continue;
				}
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
				button.onClick();
			}
		}
		return super.mouseClicked(mouseX, mouseY, i);
	}
	@Override
	public boolean mouseReleased(double mouseX, double mouseY, int button) {
		this.mouseClicked = false;
		this.mousePressed = false;
		return super.mouseReleased(mouseX, mouseY, button);
	}
	/// UTILS ////////////////////////////////////////////////////////
	public int getTextColor(String location, int fallback){
		try {
			return Integer.parseInt(Component.translatable(location).getString().substring(1), 16);
		} catch (NumberFormatException e){
			return fallback;
		}
	}
	public int componentWidth(Component str){
		return ClientTooltipComponent.create(str.getVisualOrderText()).getWidth(this.font);
	}
	/// UTILS Scroll ////////////////////////////////////////////////////////
	protected boolean shouldScroll() {
		return getTotalRows() > this.selectedPage.getMaxRows();
	}
	protected int getMaxScroll() {
		return getTotalRows() - this.selectedPage.getMaxRows();
	}
	protected int getTotalRows() {
		switch (selectedPage){
			case MAIN -> {
				return ((UniversalWaystoneScreenHandler) menu).getWaystonesCount();
			}
			case SHARE -> {
				return ((WaystoneBlockScreenHandler) menu).getPlayersCount();
			}
			case CONFIG -> {
				return 13;// TODO:
			}
		}
		return 0;
	}
	protected String getDiscoveredWaystone(int index) {
		return ((UniversalWaystoneScreenHandler) menu).getSearchedWaystone(index);
	}

	protected void prepareElements(){
		// -1 = overwritten
		/// DECO LEFT ///////////////////////////////////////////////////////////////
		elements.add(new Texture(5, 46, 18, 102, 0, 144));
		/// DECO RIGHT ///////////////////////////////////////////////////////////////
		elements.add(new Texture(171, 46, 18, 102, 18, 144));
		/// Background list buttons ///////////////////////////////////////////////////////////////
		elements.add(new Texture(-1, -1, -1, -1, 76, 96, 15, 15, 15, 15 ,45, 45){
			@Override
			public int getLeft(){
				return selectedPage == PageProperties.MAIN? favoriteSysVisible ?50:37:24;
			}
			@Override
			public int getTop(){
				return selectedPage.getScrollTrackTop()-1;
			}
			@Override
			public int getWidth(){
				return selectedPage == PageProperties.MAIN? favoriteSysVisible ?105:118:131;
			}
			@Override
			public int getHeight(){
				return selectedPage.getScrollTrackHeight()+2;
			}
		});
		/// Scrolltrack background ///////////////////////////////////////////////////////////////
		elements.add(new Texture(156, -1, 14, -1, 121, 96, 14, 15, 14, 15, 14, 45){
			@Override
			public int getTop(){
				return selectedPage.getScrollTrackTop()-1;
			}
			@Override
			public int getHeight(){
				return selectedPage.getScrollTrackHeight()+2;
			}
		});
		for (int i = 0; i < PageProperties.MAIN.getMaxRows(); i++) {
			// Favorite (Forget) btn background
			elements.add(new Texture(23, 42 + (i*18), 14, 14, 121, 141){
				@Override
				public boolean isVisible(){
					return selectedPage == PageProperties.MAIN;
				}
			});
			// Forget btn background
			elements.add(new Texture(36, 42 + (i*18), 14, 14, 121, 141){
				@Override
				public boolean isVisible(){
					return favoriteSysVisible && selectedPage == PageProperties.MAIN;
				}
			});
		}
		/// CONFIG BUTTON ///////////////////////////////////////////////////////////////
		elements.add(new ToggleButton(171, 5, 18, 18, 198, 0){
			@Override
			public void onClick() {
				changePage(selectedPage == PageProperties.CONFIG ? PageProperties.MAIN : PageProperties.CONFIG);
			}
			private void setupTooltip() {
				this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip."+ (selectedPage == PageProperties.CONFIG ? "back" : "open")));
			}

			@Override
			public boolean isToggled(){
				return selectedPage == PageProperties.CONFIG;
			}

			@Override
			public void setup() {
				this.setupTooltip();
			}
		});
		/// SORT FAVORITE BUTTON ///////////////////////////////////////////////////////////////
		elements.add(new ToggleButton(24, 24, 12, 14, 56, 54) {
			@Override
			public void setup() {
				this.setToggle(((PlayerEntityMixinAccess) inventory.player).sortByFavoriteWaystones());
				setupTooltip();
			}
			@Override
			public boolean isVisible() {
				return selectedPage == PageProperties.MAIN && favoriteSysVisible;
			}
			private void setupTooltip() {
				this.setTooltip(Component.translatable("fwaystones.gui.main.tooltip." + (this.isToggled() ? "lock" : "unlock") + "_favorite"));
			}
			@Override
			public void onClick() {
				super.onClick();
				((PlayerEntityMixinAccess) inventory.player).toggleSortByFavoriteWaystones();
				((UniversalWaystoneScreenHandler) menu).updateWaystones(inventory.player);
				setupTooltip();
			}
		});
		/// SEARCH LOCK BUTTON ///////////////////////////////////////////////////////////////
		elements.add(new ToggleButton(-1, 24, 12, 14, 80, 54){
			@Override
			public void setup() {
				this.setToggle(((PlayerEntityMixinAccess) inventory.player).autofocusWaystoneFields());
				setupTooltip();
			}
			@Override
			public int getLeft(){
				return favoriteSysVisible ?37:24;
			}
			@Override
			public boolean isVisible() {
				return selectedPage == PageProperties.MAIN;//|| TODO: ? !(UniversalWaystoneScreen.this instanceof WaystoneBlockScreen waystoneBlockScreen)
			}

			private void setupTooltip() {
				this.setTooltip(Component.translatable("fwaystones.gui.main.tooltip."+ (this.isToggled() ? "lock" : "unlock") + "_search"));
			}
			@Override
			public void onClick() {
				super.onClick();
				((PlayerEntityMixinAccess) inventory.player).toggleAutofocusWaystoneFields();
				setupTooltip();
			}
		});
		/// SEARCH TYPE CYCLE BUTTON TODO: setFocused ///////////////////////////////////////////////////////////////
		elements.add(new Button(156, 24, 14, 14, 104, 54){
			@Override
			public void onClick() {
				((UniversalWaystoneScreenHandler) menu).toggleSearchType();
				//searchField.setFocused(((PlayerEntityMixinAccess) minecraft.player).autofocusWaystoneFields());
				setupTooltip();
			}

			@Override
			public boolean isVisible() {
				return selectedPage != PageProperties.CONFIG;
			}
			private void setupTooltip() {
				this.setTooltip(((UniversalWaystoneScreenHandler) menu).getSearchTypeTooltip());
			}
			@Override
			public void setup() {
				setupTooltip();
			}
		});
		/// SCROLL THUMB ///////////////////////////////////////////////////////////////
		elements.add(new Button(157, -1, 12, 15, 64, 96){
			@Override
			public int getTop(){
				return selectedPage.calcScrollTop();
			}
			@Override
			public boolean isDisabled(){
				return !shouldScroll();
			}
			@Override
			public boolean isInBounds(int left, int top){
				int j = selectedPage.getScrollTrackTop();
				int i = this.getLeft();
				if (left >= (double) i && left < (double) (i + this.getWidth()) && top >= (double) j && top < (double) (j + selectedPage.getScrollTrackHeight())) {
					mouseClicked = true;
				}
				return super.isInBounds(left, top);
			}
		});
		/// WAYSTONE ROWS TODO: ///////////////////////////////////////////////////////////////
		for (int i = 0; i < PageProperties.MAIN.getMaxRows(); i++) {
			int row = i;
			/// FAVORITE BUTTON ///////////////////////////////////////////////////////////////
			elements.add(new ToggleButton(25, 44 + (row * 18), 10, 10, 118, 54){
				@Override
				public void setup() {
					setupTooltip();
				}
				@Override
				public boolean isToggled(){
					if(row < selectedPage.getMaxRows() && row < getTotalRows()-selectedPage.getScrollOffset()){
						return ((UniversalWaystoneScreenHandler) menu).getFavoriteWaystoneStatus(selectedPage.getScrollOffset() + row);
					}
					return false;
				}
				@Override
				public boolean isVisible(){
					if (selectedPage != PageProperties.MAIN || !favoriteSysVisible)
						return false;
					return row < selectedPage.getMaxRows() && row < getTotalRows()-selectedPage.getScrollOffset();
				}

				private void setupTooltip() {
					this.setTooltip(Component.translatable("fwaystones.gui.main.tooltip."+ (this.isToggled() ? "remove" : "add") + "_favorite"));
				}
				@Override
				public void onClick(){
					super.onClick();
					rowClicked((selectedPage.getScrollOffset()+row) * 4);
					setupTooltip();
				}
			});
			/// FORGET BUTTON ///////////////////////////////////////////////////////////////
			elements.add(new Button(-1, 44 + (row * 18), 10, 10, 138, 54){
				@Override
				public boolean isVisible(){
					if (selectedPage != PageProperties.MAIN)
						return false;
					return row < selectedPage.getMaxRows() && row < getTotalRows()-selectedPage.getScrollOffset();
				}
				@Override
				public int getLeft(){
					return favoriteSysVisible?38:25;
				}
				@Override
				public void onClick(){
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ANVIL_BREAK, 1.0F));
					rowClicked((selectedPage.getScrollOffset()+row) * 4 + 2);
					selectedPage.setScrollOffset(Math.max(0, selectedPage.getScrollOffset() - 1));
				}
				@Override
				public void setup() {
					this.setTooltip(Component.translatable("fwaystones.gui.main.tooltip.forget"));
				}
			});
			/// WAYSTONE BUTTON ///////////////////////////////////////////////////////////////
			elements.add(new Button(-1, 40 + (row * 18), -1, 18, 0, 0, 6, 6, 6, 6, 18, 18){
				@Override
				public boolean isVisible(){
					if (selectedPage != PageProperties.MAIN)
						return false;
					return row < selectedPage.getMaxRows() && row < getTotalRows()-selectedPage.getScrollOffset();
				}
				@Override
				public int getLeft(){
					return favoriteSysVisible?51:38;
				}
				@Override
				public int getWidth(){
					return favoriteSysVisible?103:116;
				}
				@Override
				public boolean hasToolTip() {
					var waystoneData = Waystones.STORAGE.getWaystoneData(getDiscoveredWaystone(selectedPage.getScrollOffset() + row));
					if (waystoneData == null || this.isDisabled()) {
						return false;
					}
					var startDim = Utils.getDimensionName(minecraft.player.getLevel());
					var endDim = waystoneData.getWorldName();
					List<Component> tooltip = new ArrayList<>();
					tooltip.add(Component.translatable("fwaystones.gui.cost_tooltip", Utils.getCost(Vec3.atCenterOf(waystoneData.way_getPos()), minecraft.player.position(), startDim, endDim)));
					if (hasShiftDown()) {
						tooltip.add(Component.translatable("fwaystones.gui.dimension_tooltip", waystoneData.getWorldName()));
					}
					this.setTooltip(tooltip);

					return true;
				}
				@Override
				public boolean isDisabled(){
					return menu instanceof WaystoneBlockScreenHandler waystoneMenu && waystoneMenu.getWaystone().equals(getDiscoveredWaystone(selectedPage.getScrollOffset()+row));
				}
				@Override
				public void onClick(){
					rowClicked((selectedPage.getScrollOffset()+row) * 4 + 3);
				}

				@Override
				public void renderText(PoseStack poseStack, int leftPos, int topPos, int mouseX, int mouseY){
					String name = Waystones.STORAGE.getName(getDiscoveredWaystone(selectedPage.getScrollOffset() + row));
					font.draw(poseStack, Component.literal(name), leftPos+this.getLeft()+4, topPos+this.getTop() + 5, textSecondaryColor);
				}
			});
		}
		/// SAVE CONFIG TODO: ALL ///////////////////////////////////////////////////////////////
		elements.add(new Button(154, 155, 16, 16, 32, 96) {
			@Override
			public boolean isVisible() {
				return selectedPage == PageProperties.CONFIG;
			}

			@Override
			public boolean isDisabled(){
				return false; // TODO: only when config changed
			}

			@Override
			public void onClick(){
				Waystones.CONFIG.save();
			}
			@Override
			public void setup() {
				this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.save_config"));
			}
		});

		//elements.add(new EditField(-1, -1, -1, -1){
		//})






		/// TODO: DEV TOGGLE FAVORITE ///////////////////////////////////////////////////////////////
		elements.add(new Button(this.imageWidth+10, 0, 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - toggle favorite system: "+ (favoriteSysVisible?"ON":"OFF")));
			}

			@Override
			public void onClick(){
				Waystones.CONFIG.client_favorite_system = favoriteSysVisible = !favoriteSysVisible;
				((UniversalWaystoneScreenHandler) menu).updateWaystones(inventory.player);
				this.setup();
			}
		});

		/// TODO: DEV TOGGLE JOURNEYMAP DIFF ICONS ///////////////////////////////////////////////////////////////
		elements.add(new Button(this.imageWidth+10, (18+4), 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - toggle Journeymap diff Icons: "+ (Waystones.CONFIG.journeymap_waypoint_same_icon?"ON":"OFF")));
			}

			@Override
			public void onClick(){
				Waystones.CONFIG.journeymap_waypoint_same_icon = !Waystones.CONFIG.journeymap_waypoint_same_icon;
				this.setup();
			}
		});




















		/*

		*/

		/*elements.put("DEV_TOGGLE_TEXTURE", new Button(this.imageWidth+10, 18+4, 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - TODO: toggle texture"));
			}

			@Override
			public void onClick(){
			}
		});
		elements.put("DEV_TOGGLE_client_share_menu_playerhead_type", new Button(this.imageWidth+10, (18*5)+4, 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - toggle client_share_menu_playerhead_type: "+ (Waystones.CONFIG.client_share_menu_playerhead_type == Config.PlayerHeadType.SKIN?"SKIN":"SKULL")));
			}

			@Override
			public void onClick(){
				if (Waystones.CONFIG.client_share_menu_playerhead_type == Config.PlayerHeadType.SKIN){
					Waystones.CONFIG.client_share_menu_playerhead_type = Config.PlayerHeadType.SKULL;
				}else{
					Waystones.CONFIG.client_share_menu_playerhead_type = Config.PlayerHeadType.SKIN;
				}
				this.setup();
			}
		});*/


	}
}
