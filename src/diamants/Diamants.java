package diamants;

class Diamants {

	FrameJeu frJeu;
	Client client;

	private Diamants() {
		this.frJeu = new FrameJeu(this);
	}

	public static void main(String[] args) {
		new Diamants();
	}

}
