package wraith.fwaystones.screen.element;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiComponent;

public class Texture extends Base{
	private int textureLeft;
	private int textureTop;
	private int textureLeftWidth;
	private int textureTopWidth;
	private int textureRightWidth;
	private int textureBottomWidth;
	private int textureWidth;
	private int textureHeight;
	private boolean isNineSliced = false;

	public Texture(int left, int top, int width, int height, int textureLeft, int textureTop, int textureLeftWidth, int textureTopWidth, int textureRightWidth, int textureBottomWidth, int textureWidth, int textureHeight) {
		this(left, top, width, height, textureLeft, textureTop);
		this.textureLeftWidth = textureLeftWidth;
		this.textureTopWidth = textureTopWidth;
		this.textureRightWidth = textureRightWidth;
		this.textureBottomWidth = textureBottomWidth;
		this.textureWidth = textureWidth;
		this.textureHeight = textureHeight;
		this.isNineSliced = true;
	}
	public Texture(int left, int top, int width, int height, int textureLeft, int textureTop) {
		super(left, top, width, height);
		this.textureLeft = textureLeft;
		this.textureTop = textureTop;
	}
	///////////////////////////////////////////////////////////
	public int getTextureLeft() {
		return textureLeft;
	}
	public void setTextureLeft(int textureLeft) {
		this.textureLeft = textureLeft;
	}
	public int getTextureTop() {
		return textureTop;
	}
	public void setTextureTop(int textureTop) {
		this.textureTop = textureTop;
	}
	///////////////////////////////////////////////////////////
	public int getTextureLeftWidth() {
		return this.textureLeftWidth;
	}
	public int getTextureTopWidth() {
		return this.textureTopWidth;
	}
	public int getTextureRightWidth() {
		return this.textureRightWidth;
	}
	public int getTextureBottomWidth() {
		return this.textureBottomWidth;
	}
	public int getTextureWidth() {
		return this.textureWidth;
	}
	public int getTextureHeight() {
		return this.textureHeight;
	}
	///////////////////////////////////////////////////////////
	public boolean isNineSliced(){
		return this.isNineSliced;
	}
	public void renderBackground(PoseStack poseStack, float delta, int leftPos, int topPos, int mouseX, int mouseY, boolean mousePressed){
		if(!this.isVisible())
			return;
		if(this.isNineSliced()){
			GuiComponent.blitNineSliced(poseStack,
					this.getLeft()+leftPos, this.getTop()+topPos,
					this.getWidth(), this.getHeight(),
					this.getTextureLeftWidth(), this.getTextureTopWidth(),
					this.getTextureRightWidth(), this.getTextureBottomWidth(),
					this.getTextureWidth(), this.getTextureHeight(),
					this.getTextureLeft(), this.getTextureTop()
			);
		} else {
			GuiComponent.blit(poseStack, this.getLeft()+leftPos, this.getTop()+topPos, this.getTextureLeft(), this.getTextureTop(), this.getWidth(), this.getHeight());
		}
	}
	public void renderText(PoseStack poseStack, int leftPos, int topPos, int mouseX, int mouseY){}

}
