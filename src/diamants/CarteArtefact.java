package diamants;

class CarteArtefact extends Carte {
	static int nbArtefact = 0;
	int numArtefact;
	CarteArtefact() {
		super("Artefact");
		this.numArtefact = ++nbArtefact;
	}
}
