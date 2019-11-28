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
		String rep = gc.attendreReponse(new String[] {"rester", "quitter"});
		if(rep != null)
			rester = rep.equals("rester");
		else
			rester = false;
	}
}
