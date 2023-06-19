package wraith.fwaystones.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import wraith.fwaystones.screen.element.PageProperties;
import wraith.fwaystones.screen.element.Texture;
import wraith.fwaystones.util.Utils;

public class AbyssScreen extends UniversalWaystoneScreen {
	private static final ResourceLocation TEXTURE_A = Utils.ID("textures/gui/abyss_watcher/a.png");
	private static final ResourceLocation TEXTURE_B = Utils.ID("textures/gui/abyss_watcher/b.png");
	public AbyssScreen(AbstractContainerMenu handler, Inventory inventory, Component title) {
		super(handler, inventory, title);
		this.textureA = TEXTURE_A;
		this.textureB = TEXTURE_B;
		this.textPrimaryColor = getTextColor("fwaystones.gui.abyss_watcher.text_primary_color.arg_color", 0x7E3483);
		this.textSecondaryColor = getTextColor("fwaystones.gui.abyss_watcher.text_secondary_color.arg_color", 0x161616);
	}



	@Override
	protected void prepareElements(){
		super.prepareElements();

		// DECO MIDDLE
		elements.add(new Texture(60, 162,74, 18, 160, 54){
			@Override
			public boolean isVisible() {
				return selectedPage == PageProperties.CONFIG;
			}
		});
	}
}
