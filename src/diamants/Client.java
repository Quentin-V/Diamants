package diamants;


import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class Client {
	private int				pochetTmp;
	private int				pochetSauvee;
	private boolean			sortit;
	private BufferedReader	in;
	private PrintWriter		out;
	private Socket           socket;
	private Diamants         diamants;


	/**
	 * @param s Le socket de connexion au serveur
	 */
	Client(Socket s, Diamants diamants) {
		this.pochetTmp    = 0;
		this.pochetSauvee = 0;
		this.sortit       = false;
		this.socket       = s;
		this.diamants     = diamants;
		try {
			this.out = new PrintWriter(s.getOutputStream(), true);
			this.in  = new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch(IOException ie) {
			ie.printStackTrace();
		}
		Listener listener = new Listener(this);
		Thread t = new Thread(listener);
		t.start();
	}

	private class Listener implements Runnable {
		Client client;
		Listener(Client client) {
			this.client = client;
		}

		@Override
		public void run() {
			try {
				String s = in.readLine();
				System.out.println(s);
				if (s.equals("plateau")) {
					listenPlateau();
				}else if (s.equals("Bonjour joueur, quel est ton nom ? ")) {
					new FrameDemande("Quel est votre nom", client);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void listenPlateau() {
			try {
				ObjectInputStream ois = new ObjectInputStream(this.client.socket.getInputStream());
				byte b = -1;
				while(b == -1) {
					try {
						Object recu = ois.readObject();
						this.client.diamants.frJeu.pnlCartes.refresh((ArrayList<Carte>) recu);
						b = 0;
					}catch(Exception ignored) {}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return la reponse de si le joueur veut retourner au campement
	 */
	String demandeSortit() {
		this.out.println("Voulez-vous rentrez au campement ? (O/N)");

		switch(demande(new String[]{"O","N"}, "Voulez-vous rentrez au campement ? (O/N)"))
		{
			case "O" :
				this.sortit = true;
				break;
			case "N" :
				this.sortit = false;
				break;
		}

		return "" + this.sortit;
	}


	void envoyer(String message) {
		out.println(message);
	}

	/**
	 * @param repPossible sont les differentes reponses que peut choisir le joueur
	 * @param question a laquelle le joueur doit repondre
	 * @return la reponse du joueur
	 */
	private String demande(String[] repPossible, String question) {
		String rep = "";
		boolean sortBoucle = false;
		while(!sortBoucle) {
			this.out.println(question);
			//rep = this.in.read();
			for (int i = 0; i < repPossible.length; i++)
				if (rep.equals(repPossible[i])) {
					sortBoucle = true;
				}
		}
		return rep;
	}

	/**
	 * @return true si le joueur est au campement
	 */
	boolean getSortit() {
		return this.sortit;
	}

	/**
	 * @return le nombre de points que le joueur a et qu'il peut encore perdre
	 */
	int getPochetTmp() {
		return this.pochetTmp;
	}

	/**
	 * @return le nombre de points que le joueur a et qu'il ne peut pas perdre
	 */
	int getPochetSauvee() {
		return this.pochetSauvee;
	}

	/**
	 * @param nbRubis
	 */
	void ajouterRubis(int nbRubis) {
		this.pochetTmp += nbRubis;
	}

	/**
	 * @param nbDiamants
	 */
	void ajouterDiamants(int nbDiamants) {
		this.pochetTmp += (nbDiamants * 5);
	}

	/**
	 * Sauve les pierres que le joueur pouvait perdre
	 */
	void sauverPierresPrecieuses() {
		this.pochetSauvee	+= this.pochetTmp;
		this.pochetTmp 		=  0;
	}

	/**
	 * Perd les pierres que le joueur pouvait perdre
	 */
	void perdrePierresPrecieuses() {
		this.pochetTmp = 0;
	}
}
