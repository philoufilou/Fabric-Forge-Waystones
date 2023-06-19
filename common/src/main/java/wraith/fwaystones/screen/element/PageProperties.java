package wraith.fwaystones.screen.element;

import net.minecraft.util.Mth;

public enum PageProperties {
	MAIN(0, 0.0f, 40, 108, 6, 55, 27, 101, 9, ""),
	CONFIG(0, 0.0f, 25, 126, 7, 43, 176, 94, 9, ""),
	SHARE(0, 0.0f, 40, 108, 6, 29, 27, 127, 9, "");
	///////////////////////////////////////////////////////////
	private int scrollOffset;
	private float scrollAmount;
	private final int scrollTrackTop;
	private final int scrollTrackHeight;
	private final int maxRows;
	private final int searchFieldLeft;
	private final int searchFieldTop;
	private final int searchFieldWidth;
	private final int searchFieldHeight;
	private String searchFieldValue;
	///////////////////////////////////////////////////////////
	PageProperties(
			int scrollOffset,
			float scrollAmount,
			int scrollTrackTop,
			int scrollTrackHeight,
			int maxRows,
			int searchFieldLeft,
			int searchFieldTop,
			int searchFieldWidth,
			int searchFieldHeight,
			String searchFieldValue
	)
	{
		this.scrollOffset = scrollOffset;
		this.scrollAmount = scrollAmount;
		this.scrollTrackTop = scrollTrackTop;
		this.scrollTrackHeight = scrollTrackHeight;
		this.maxRows = maxRows;
		this.searchFieldLeft = searchFieldLeft;
		this.searchFieldTop = searchFieldTop;
		this.searchFieldWidth = searchFieldWidth;
		this.searchFieldHeight = searchFieldHeight;
		this.searchFieldValue = searchFieldValue;
	}
	///////////////////////////////////////////////////////////
	public int getScrollOffset(){
		return this.scrollOffset;
	}
	public void setScrollOffset(int scrollOffset){
		this.scrollOffset = scrollOffset;
	}
	public float getScrollAmount(){
		return this.scrollAmount;
	}
	public void setScrollAmount(float scrollAmount){
		this.scrollAmount = scrollAmount;
	}
	///////////////////////////////////////////////////////////
	public int getScrollTrackTop(){
		return this.scrollTrackTop;
	}
	public int getScrollTrackHeight(){
		return this.scrollTrackHeight;
	}
	public int getMaxRows(){
		return this.maxRows;
	}
	///////////////////////////////////////////////////////////
	public int calcScrollTop(){
		return (int) ((this.scrollTrackHeight - 15.0F) * this.scrollAmount) + this.scrollTrackTop;
	}
	public void calcMouseScrolled(double amount, int maxScroll){
		this.scrollAmount = Mth.clamp( (float) ((double) this.scrollAmount - amount / (double) maxScroll) , 0.0F, 1.0F);
		this.scrollOffset = (int) ((double) (this.scrollAmount * (float) maxScroll) + 0.5D);
	}
	public void calcMouseDragged(int topPos, double mouseY, int maxScroll){
		int i = topPos + this.scrollTrackTop;
		int j = i + this.scrollTrackHeight;
		this.scrollAmount = Mth.clamp(((float) mouseY - (float) i - 7.5F) / ((float) (j - i) - 15.0F), 0.0F, 1.0F);
		this.scrollOffset = (int) ((double) (this.scrollAmount * (float) maxScroll) + 0.5D);
	}
	///////////////////////////////////////////////////////////
	public int getSearchFieldLeft(){
		return this.searchFieldLeft;
	}
	public int getSearchFieldTop(){
		return this.searchFieldTop;
	}
	public int getSearchFieldWidth(){
		return this.searchFieldWidth;
	}
	public int getSearchFieldHeight(){
		return this.searchFieldHeight;
	}
	public String getSearchFieldValue(){
		return this.searchFieldValue;
	}
	public void setSearchFieldValue(String searchFieldValue){
		this.searchFieldValue = searchFieldValue;
	}







	///////////////////////////////////////////////////////////
	public static void resetPages() {
		MAIN.resetPage();
		SHARE.resetPage();
		CONFIG.resetPage();
	}
	private void resetPage() {
		this.scrollOffset = 0;
		this.scrollAmount = 0.0F;
		this.searchFieldValue = "";
	}
}
