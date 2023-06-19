package wraith.fwaystones.screen.element;

import java.util.Optional;

public class ToggleButton extends Button {
	private boolean toggled = false;
	private int toggleTextureLeft;
	private int toggleTextureTop;
	///////////////////////////////////////////////////////////
	public ToggleButton(int left, int top, int width, int height, int textureLeft, int textureTop, int textureLeftWidth, int textureTopWidth, int textureRightWidth, int textureBottomWidth, int textureWidth, int textureHeight) {
		super(left, top, width, height, textureLeft, textureTop, textureLeftWidth, textureTopWidth, textureRightWidth, textureBottomWidth, textureWidth, textureHeight);
		this.toggleTextureLeft = textureLeft+width;
		this.toggleTextureTop = textureTop;
	}
	public ToggleButton(int left, int top, int width, int height, int textureLeft, int textureTop) {
		super(left, top, width, height, textureLeft, textureTop);
		this.toggleTextureLeft = textureLeft+width;
		this.toggleTextureTop = textureTop;
	}
	///////////////////////////////////////////////////////////
	@Override
	public int getTextureLeft() {
		if (this.isToggled()) {
			return this.toggleTextureLeft;
		}
		return super.getTextureLeft();
	}
	@Override
	public int getTextureTop() {
		if (this.isToggled()) {
			return this.toggleTextureTop;
		}
		return super.getTextureTop();
	}
	///////////////////////////////////////////////////////////
	public void setToggle(boolean toggle){
		this.toggled = toggle;
	}
	public void toggle() {
		this.toggled = !this.toggled;
	}
	public boolean isToggled() {
		return this.toggled;
	}
	///////////////////////////////////////////////////////////
	@Override
	public void onClick() {
		this.toggle();
	}
}
