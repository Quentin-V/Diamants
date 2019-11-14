package diamants;

class CarteArtefacat extends Carte {
	static int nbArtefact = 0;
	int numArtefact;
	CarteArtefacat() {
		super("Artefact");
		this.numArtefact = ++nbArtefact;
	}
}
