package wraith.fwaystones.screen.element;

public class Base {
	private int left;
	private int top;
	private int height;
	private int width;
	///////////////////////////////////////////////////////////
	public Base(int left, int top, int width, int height) {
		this.left = left;
		this.top = top;
		this.height = height;
		this.width = width;
	}
	///////////////////////////////////////////////////////////
	public int getLeft() {
		return this.left;
	}
	public void setLeft(int left) {
		this.left = left;
	}
	public int getTop() {
		return this.top;
	}
	public void setTop(int top) {
		this.top = top;
	}
	///////////////////////////////////////////////////////////
	public int getHeight() {
		return this.height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public int getWidth() {
		return this.width;
	}
	public void setWidth(int width) {
		this.width = width;
	}

	///////////////////////////////////////////////////////////
	public void setup() {}
	public boolean isVisible() {
		return true;
	}
}
