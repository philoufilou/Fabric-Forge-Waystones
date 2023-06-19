package wraith.fwaystones.access;

import java.util.ArrayList;

public interface PlayerAccess {

	ArrayList<String> getHashesSorted();
	ArrayList<String> getFavoritedHashesSorted();
	int getDiscoveredCount();

}
