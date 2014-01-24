package model;

/**
 * Displays the "All" option in the lists. 
 * @author Charlie
 */
public class AllArtistsBean extends ArtistBean {
	private static final long serialVersionUID = -4820820224266661435L;
	private int numberOfArtists = 0;
	
	public AllArtistsBean() {
		setName("All Artists");
	}
	
	/**
	 * @return the numberOfArtists
	 */
	public int getNumberOfArtists() {
		return numberOfArtists;
	}

	/**
	 * @param numberOfArtists the numberOfArtists to set
	 */
	public void setNumberOfArtists(int numberOfArtists) {
		this.numberOfArtists = numberOfArtists;
	}

	@Override
	public String toString() {
		return "All (" + numberOfArtists + ")";
	}
}
