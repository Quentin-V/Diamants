package diamants;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Client {
	private int				pochetTmp;
	private int				pochetSauvee;
	private boolean			sortit;
	private BufferedReader	in;
	private PrintWriter		out;

	/**
	 * @param la socket du client
	 */
	public Client(Socket s) {
		this.pochetTmp		= 0;
		this.pochetSauvee	= 0;
		this.sortit			= false;
		try {
			this.out	= new PrintWriter(s.getOutputStream(), true);
			this.in		= new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch(IOException ie) {
			System.out.println(ie);
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

		return this.sortit;
	}


	/**
	 * @param repPossible sont les differentes reponses que peut choisir le joueur
	 * @param question a laquelle le joueur doit repondre
	 * @return la reponse du joueur
	 */
	private String demande(String[] repPossible, String question) {
		String rep = "";
		try
		{
			boolean sortBoucle = false;
			while(!sortBoucle) {
				this.out.println(question);
				rep = this.in.read();
				for (int i = 0; i < repPossible.length; i++)
					if (rep.equals(repPossible[i])) {
						sortBoucle = true;
					}
			}
		}
		catch (IOException e){
			System.out.println(ie);
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
	 * @param nombre de rubis que le joueur vient de recolter
	 */
	void ajouterRubis(int nbRubis) {
		this.pochetTmp += nbRubis;
	}

	/**
	 * @param nombre de diamants que le joueur vient de recoltes
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
