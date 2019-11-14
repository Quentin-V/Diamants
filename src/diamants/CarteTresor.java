package diamants;

class CarteTresor extends Carte {
	int nbDiamants;

	CarteTresor(int nbDiamants) {
		super("Tresor");
		this.nbDiamants = nbDiamants;
	}

	@Override
	public String toString() {
		return this.nbDiamants + " diamants";
	}
}
