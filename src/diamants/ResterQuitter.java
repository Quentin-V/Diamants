package diamants;

public class ResterQuitter implements Runnable {

	Boolean rester;
	private GestionClient gc;

	ResterQuitter(Boolean rester, GestionClient gc) {
		this.rester = rester;
		this.gc = gc;
	}

	@Override
	public void run() {
		rester = gc.attendreReponse(new String[] {"rester", "quitter"}).equals("rester");
	}
}
