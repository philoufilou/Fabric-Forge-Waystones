package wraith.fwaystones.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import wraith.fwaystones.Waystones;
import wraith.fwaystones.screen.element.Button;
import wraith.fwaystones.screen.element.PageProperties;
import wraith.fwaystones.screen.element.Texture;
import wraith.fwaystones.screen.element.ToggleButton;
import wraith.fwaystones.util.PacketHandler;
import wraith.fwaystones.util.Utils;

import java.util.UUID;

public class WaystoneBlockScreen extends UniversalWaystoneScreen {
	private static final ResourceLocation TEXTURE_A = Utils.ID("textures/gui/waystone_default/a.png");
	private static final ResourceLocation TEXTURE_B = Utils.ID("textures/gui/waystone_default/b.png");
	public WaystoneBlockScreen(AbstractContainerMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
		textureA = TEXTURE_A;
		textureB = TEXTURE_B;
		textPrimaryColor = getTextColor("fwaystones.gui.waystone.text_primary_color.arg_color", 0x161616);
		textSecondaryColor = getTextColor("fwaystones.gui.waystone.text_secondary_color.arg_color", 0x161616);
	}





	@Override
	protected Component updateTitle(){
		if (selectedPage == PageProperties.MAIN) {
			return Component.literal(((WaystoneBlockScreenHandler) menu).getName());
		}
		return super.updateTitle();
	}

	private boolean canEdit() {
		return ((WaystoneBlockScreenHandler) menu).isOwner(inventory.player) || inventory.player.hasPermissions(2);
	}




	@Override
	protected void prepareElements(){
		super.prepareElements();
		/// SAHRE BUTTON ///////////////////////////////////////////////////////////////
		elements.add(new ToggleButton(5, 5, 18, 18, 162, 0){
			public void onClick() {
				changePage(selectedPage == PageProperties.SHARE ? PageProperties.MAIN : PageProperties.SHARE);
			}
			private void setupTooltip() {
				this.setTooltip(Component.translatable("fwaystones.gui.share.tooltip."+ (selectedPage == PageProperties.SHARE ? "back" : "open")));
			}

			@Override
			public boolean isToggled(){
				return selectedPage == PageProperties.SHARE;
			}
			@Override
			public void setup() {
				this.setupTooltip();
			}

			@Override
			public boolean isVisible(){
				return !minecraft.isSingleplayer() && (Waystones.CONFIG.client_share_menu || selectedPage == PageProperties.SHARE);
			}
		});


		for (int i = 0; i < PageProperties.SHARE.getMaxRows(); i++) {
			int row = i;
			elements.add(new Button(25, 40 + (row * 18), 129, 18, 0, 0, 6, 6, 6, 6, 18, 18) {
				@Override
				public boolean isVisible() {
					if (!minecraft.isSingleplayer() && selectedPage == PageProperties.SHARE)
						return row < selectedPage.getMaxRows() && row < getTotalRows() - selectedPage.getScrollOffset();
					return false;
				}

				@Override
				public void onClick() {
					super.onClick();
					Waystones.LOGGER.warn("Clicked on row: " + (selectedPage.getScrollOffset() + row));
					// call WaystoneButtonClick(page.getScrollOffset()+row)
				}

				@Override
				public void renderBackground(PoseStack poseStack, float delta, int leftPos, int topPos, int mouseX, int mouseY, boolean mousePressed) {
					if(!this.isVisible()){
						return;
					}
					int textureLeft = this.getTextureLeft();
					int textureTop = this.getTextureTop();
					if (this.isDisabled()) {
						textureTop += this.getHeight();
					} else if (this.isInBounds(mouseX - leftPos, mouseY - topPos)) {
						textureTop += this.getHeight() * (mousePressed ? 1 : 2);
					}

					GuiComponent.blitNineSliced(poseStack,
							leftPos+this.getLeft()+(Waystones.CONFIG.client_share_menu_show_playerhead?18:0), topPos+this.getTop(),
							this.getWidth()-(Waystones.CONFIG.client_share_menu_show_playerhead?18:0), this.getHeight(),
							this.getTextureLeftWidth(), this.getTextureTopWidth(),
							this.getTextureRightWidth(), this.getTextureBottomWidth(),
							this.getTextureWidth(), this.getTextureHeight(),
							textureLeft, textureTop
					);
					if(Waystones.CONFIG.client_share_menu_show_playerhead){
						UniversalWaystoneScreen.blit(poseStack, leftPos+this.getLeft(), topPos+this.getTop(), 0, textureTop, 18, 18);

						RenderSystem.setShaderTexture(0, ((WaystoneBlockScreenHandler) menu).getSkin(selectedPage.getScrollOffset()+row));
						UniversalWaystoneScreen.blit(poseStack, leftPos + 27, topPos+this.getTop()+2, 14, 14, 8, 8, 8, 8, 64, 64);
						RenderSystem.enableBlend();
						UniversalWaystoneScreen.blit(poseStack, leftPos + 27, topPos+this.getTop()+2, 14, 14, 40, 8, 8, 8, 64, 64);
						RenderSystem.disableBlend();
						RenderSystem.setShaderTexture(0, textureB);

					}
				}

				@Override
				public void renderText(PoseStack poseStack, int leftPos, int topPos, int mouseX, int mouseY) {
					String name = ((WaystoneBlockScreenHandler) menu).getPlayerName(selectedPage.getScrollOffset()+row);
					font.draw(poseStack, Component.literal(name), leftPos+this.getLeft() +4+(Waystones.CONFIG.client_share_menu_show_playerhead?18:0), topPos+this.getTop() + 5, textSecondaryColor);
				}
			});
		}





		// DECO MIDDLE
		elements.add(new Texture(60, -1,74, 18, 160, 54){
			@Override
			public boolean isVisible() {
				return selectedPage == PageProperties.CONFIG;
			}
			@Override
			public int getTop(){
				return canEdit()?154:162;
			}
		});
		if(canEdit()){
			/// RESET NAME ///////////////////////////////////////////////////////////////
			elements.add(new Button(24, 174, 14, 14, 14, 54) {
				@Override
				public void onClick() {
					super.onClick();
					//searchField.setValue("");
				}

				@Override
				public boolean isVisible() {
					return selectedPage == PageProperties.CONFIG;
				}

				@Override
				public void setup() {
					this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.delete_name"));
				}
			});
			/// SET NAME ///////////////////////////////////////////////////////////////
			elements.add(new ToggleButton(139, 174, 14, 14, 28, 54) {
				@Override
				public void setup() {
					this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.set_name"));
					//boolean settable = !((WaystoneBlockScreenHandler) menu).getName().equals(searchField.getValue());
					//if (this.isToggled() == settable) {
					//	this.toggle();
					//}
				}

				@Override
				public boolean isVisible() {
					return selectedPage == PageProperties.CONFIG;
				}

				@Override
				public void onClick() {
					super.onClick();
					//rename();
					//boolean settable = !((WaystoneBlockScreenHandler) menu).getName().equals(searchField.getValue());
					//if (this.isToggled() == settable) {
					//	this.toggle();
					//}
				}

			});
			/// RANDOMIZE NAME ///////////////////////////////////////////////////////////////
			elements.add(new Button(156, 174, 14, 14, 0, 54) {
				@Override
				public void onClick() {
					//searchField.setValue(Utils.generateWaystoneName(""));
				}

				@Override
				public boolean isVisible() {
					return selectedPage == PageProperties.CONFIG;
				}

				@Override
				public void setup() {
					this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.randomize_name"));
				}
			});
			/// GLOBAL TOGGLE ///////////////////////////////////////////////////////////////
			elements.add(new ToggleButton(24, 155, 16, 16, 0, 96){

				@Override
				public void setup() {
					this.setToggle(((WaystoneBlockScreenHandler) menu).isGlobal());
					setupTooltip();
				}

				@Override
				public void onClick() {
					super.onClick();
					((WaystoneBlockScreenHandler) menu).toggleGlobal();
					setupTooltip();
				}

				private void setupTooltip() {
					this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.make"+(this.isToggled() ? "_non": "")+"_global"));
				}
				@Override
				public boolean isVisible() {
					return selectedPage == PageProperties.CONFIG;
				}
			});
			/// REVOKE OWNERSHIP ///////////////////////////////////////////////////////////////
			elements.add(new Button(43, 155, 16, 16, 48, 96){
				@Override
				public void onClick() {
					super.onClick();
					FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
					CompoundTag tag = new CompoundTag();
					tag.putString("waystone_hash", ((WaystoneBlockScreenHandler) menu).getWaystone());
					UUID owner = ((WaystoneBlockScreenHandler) menu).getOwner();
					tag.putUUID("waystone_owner", owner);
					packet.writeNbt(tag);
					NetworkManager.sendToServer(PacketHandler.REMOVE_WAYSTONE_OWNER, packet);
					((WaystoneBlockScreenHandler) menu).removeOwner();
				}

				@Override
				public boolean isVisible() {
					return selectedPage == PageProperties.CONFIG && ((WaystoneBlockScreenHandler) menu).hasOwner();
				}

				@Override
				public void setup() {
					this.setTooltip(Component.translatable("fwaystones.gui.config.tooltip.revoke_ownership"));
				}
			});
		}








		/// TODO: DEV SHARE MENU VISIBLE CLIENT SIDE ///////////////////////////////////////////////////////////////
		elements.add(new Button(this.imageWidth+32, 0, 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - Show Client Menu (Client): "+ (Waystones.CONFIG.client_share_menu?"ON":"OFF")));
			}

			@Override
			public void onClick(){
				Waystones.CONFIG.client_share_menu = !Waystones.CONFIG.client_share_menu;
				this.setup();
			}
		});
		/// TODO: DEV TOGGLE SHOW PLAYERHEAD ///////////////////////////////////////////////////////////////
		elements.add(new Button(this.imageWidth+32, 18+4, 18, 18, 144, 0) {

			@Override
			public void setup() {
				this.setTooltip(Component.literal("DEV - Share Menu - Show PlayerHead: "+ (Waystones.CONFIG.client_share_menu_show_playerhead?"SHOW":"HIDDEN")));
			}

			@Override
			public void onClick(){
				Waystones.CONFIG.client_share_menu_show_playerhead = !Waystones.CONFIG.client_share_menu_show_playerhead;
				this.setup();
			}
		});


		/*
		// View discovered
		buttons.add(new ToggleButton(8, 26, 13, 13, 177, 54, 190, 54) {
			@Override
			public void setup() {
				this.toggled = ((PlayerEntityMixinAccess) inventory.player).shouldViewDiscoveredWaystones();
			}

			@Override
			public void onClick() {
				if (!isVisible()) {
					return;
				}
				super.onClick();
				((PlayerEntityMixinAccess) inventory.player).toggleViewDiscoveredWaystones();
				((UniversalWaystoneScreenHandler) handler).updateWaystones(inventory.player);
				FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
				packet.writeNbt(((PlayerEntityMixinAccess) inventory.player).toTagW(new CompoundTag()));
				NetworkManager.sendToServer(PacketHandler.SYNC_PLAYER_FROM_CLIENT, packet);
			}

			@Override
			public boolean isVisible() {
				return page == Page.CONFIG;
			}
		});

		//View global
		buttons.add(new ToggleButton(8, 42, 13, 13, 177, 54, 190, 54) {
			@Override
			public void setup() {
				this.toggled = ((PlayerEntityMixinAccess) inventory.player).shouldViewGlobalWaystones();
			}

			@Override
			public void onClick() {
				if (!isVisible()) {
					return;
				}
				super.onClick();
				((PlayerEntityMixinAccess) inventory.player).toggleViewGlobalWaystones();
				((UniversalWaystoneScreenHandler) handler).updateWaystones(inventory.player);
				FriendlyByteBuf packet = new FriendlyByteBuf(Unpooled.buffer());
				packet.writeNbt(((PlayerEntityMixinAccess) inventory.player).toTagW(new CompoundTag()));
				NetworkManager.sendToServer(PacketHandler.SYNC_PLAYER_FROM_CLIENT, packet);
			}

			@Override
			public boolean isVisible() {
				return page == Page.CONFIG;
			}
		});

		*/
	}
}
