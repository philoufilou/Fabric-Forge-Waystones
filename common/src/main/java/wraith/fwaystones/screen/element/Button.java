package wraith.fwaystones.screen.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import wraith.fwaystones.screen.UniversalWaystoneScreen;

import java.util.Collections;
import java.util.List;

public class Button extends Texture{
	private List<Component> tooltip = null;
	private boolean disabled = false;
	///////////////////////////////////////////////////////////
	public Button(int left, int top, int width, int height, int textureLeft, int textureTop, int textureLeftWidth, int textureTopWidth, int textureRightWidth, int textureBottomWidth, int textureWidth, int textureHeight) {
		super(left, top, width, height, textureLeft, textureTop, textureLeftWidth, textureTopWidth, textureRightWidth, textureBottomWidth, textureWidth, textureHeight);
	}
	public Button(int left, int top, int width, int height, int textureLeft, int textureTop) {
		super(left, top, width, height, textureLeft, textureTop);
	}

	///////////////////////////////////////////////////////////
	public boolean isInBounds(int left, int top) {
		return left >= this.getLeft() && left < this.getLeft() + this.getWidth() && top >= this.getTop() && top < this.getTop() + this.getHeight();
	}
	///////////////////////////////////////////////////////////
	public boolean hasToolTip() {return this.tooltip != null;}
	public void setTooltip(Component tooltip) {
		this.tooltip = Collections.singletonList(tooltip);
	}
	public void setTooltip(List<Component> tooltip) {
		this.tooltip = tooltip;
	}
	public void addTooltip(Component tooltip){
		this.tooltip.add(tooltip);
	}
	public List<Component> getTooltip() {return this.tooltip;}
	///////////////////////////////////////////////////////////
	public boolean isDisabled(){
		return this.disabled;
	}
	public void setDisabled(boolean disable){
		this.disabled = disable;
	}

	///////////////////////////////////////////////////////////
	public void renderTooltips(UniversalWaystoneScreen handler, PoseStack poseStack, int leftPos, int topPos, int mouseX, int mouseY){
		if(!this.isVisible() || !this.hasToolTip() || !this.isInBounds(mouseX-leftPos, mouseY-topPos))
			return;
		handler.renderComponentTooltip(poseStack, this.getTooltip(), mouseX, mouseY);

	}
	///////////////////////////////////////////////////////////
	@Override
	public void renderBackground(PoseStack poseStack, float delta, int leftPos, int topPos, int mouseX, int mouseY, boolean mousePressed){
		if(!this.isVisible())
			return;
		int textureLeft = this.getTextureLeft();
		int textureTop = this.getTextureTop();
		if (this.isDisabled()){
			textureTop += this.getHeight();
		}else if (this.isInBounds(mouseX-leftPos, mouseY-topPos)) {
			textureTop += this.getHeight() * (mousePressed ? 1 : 2);
		}

		if(this.isNineSliced()){
			GuiComponent.blitNineSliced(poseStack,
					this.getLeft()+leftPos, this.getTop()+topPos,
					this.getWidth(), this.getHeight(),
					this.getTextureLeftWidth(), this.getTextureTopWidth(),
					this.getTextureRightWidth(), this.getTextureBottomWidth(),
					this.getTextureWidth(), this.getTextureHeight(),
					textureLeft, textureTop
			);
		} else {
			GuiComponent.blit(poseStack, this.getLeft()+leftPos, this.getTop()+topPos, textureLeft, textureTop, this.getWidth(), this.getHeight());
		}
	}
	public void onClick() {}
}
