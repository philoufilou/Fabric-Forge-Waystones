package wraith.fwaystones.screen.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import wraith.fwaystones.screen.UniversalWaystoneScreenHandler;

public class EditField extends Texture{
	private boolean init = false;
	private EditBox editBox;
	private int leftEditBox;
	private int topEditBox;
	public EditField(int left, int top, int leftEditBox, int topEditBox, int width, int height, int textureLeft, int textureTop, int textureLeftWidth, int textureTopWidth, int textureRightWidth, int textureBottomWidth, int textureWidth, int textureHeight) {
		super(left, top, width, height, textureLeft, textureTop, textureLeftWidth, textureTopWidth, textureRightWidth, textureBottomWidth, textureWidth, textureHeight);
		this.leftEditBox = leftEditBox;
		this.topEditBox = topEditBox;
	}
	public void init(int leftPos, int topPos){
		this.editBox = new EditBox(Minecraft.getInstance().font, leftPos + this.getLeft()+leftEditBox, topPos + this.getTop() + topEditBox, this.getWidth()-(leftEditBox*2), 9, Component.literal("")) {
			@Override
			public boolean mouseClicked(double mouseX, double mouseY, int button) {
				boolean bl = mouseX >= (double) leftPos+getLeft() && mouseX < (double) (leftPos+getLeft() + getWidth()) && mouseY >= (double) topPos+getTop() && mouseY < (double) (topPos+getTop() + getHeight());
				if (bl && button == 1) {
					this.setValue("");
				}
				return super.mouseClicked(mouseX, mouseY, button);
			}
		};

		//this.editBox.setMaxLength(16);
		//this.editBox.setTextColor(0xFFFFFF);
		//this.editBox.setVisible(true);
		//this.editBox.setBordered(false);
		//this.editBox.setCanLoseFocus(true);
		//this.editBox.setValue(selectedPage.getSearchFieldValue());
		//this.editBox.setResponder((s) -> {
		//	if(selectedPage != PageProperties.CONFIG){
		//		selectedPage.setScrollAmount(0);
		//		selectedPage.setScrollOffset(0);
		//	}
		//	selectedPage.setSearchFieldValue(this.searchField != null ? this.searchField.getValue() : "");
		//	((UniversalWaystoneScreenHandler) menu).setFilter(this.searchField != null ? this.searchField.getValue() : "");
		//	((UniversalWaystoneScreenHandler) menu).filterWaystones();
		//});


	}
	@Override
	public void renderBackground(PoseStack poseStack, float delta, int leftPos, int topPos, int mouseX, int mouseY, boolean mousePressed){
		super.renderBackground(poseStack, delta, leftPos, topPos, mouseX, mouseY, mousePressed);
		this.editBox.render(poseStack, mouseX, mouseY, delta);
	}
}
