package diamants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class GestionClient implements Runnable {

	private BufferedReader in;
	private PrintWriter out;

	private String  name;
	private int     nbDiamants;
	boolean quittez;
	boolean quittezPlusTours;

	private Serveur serv;


	GestionClient(Socket s, Serveur serv){
		this.serv = serv;
		quittez = false;
		quittezPlusTours = false;
		try{
			out = new PrintWriter(s.getOutputStream(), true);
			in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		}catch(Exception e){}
	}


	public void run(){
		out.println("Voulez-vous créer une nouvelle table ?");
		String reponse, nomTable;
		if(!serv.tableLibre()) {
			out.println("Aucun table n'existe, vous êtes donc sur une nouvelle table." +
					  "\nDonner un nom a cette table : ");
			reponse = attendreReponse();
			serv.nouvelleTable(this, reponse);
		}else {
			reponse = attendreReponse(new String[] {"oui", "non"});
			if(reponse.equalsIgnoreCase("Oui")){
				out.print("Choisissez le nom de votre table : ");
				nomTable = attendreReponse();
				serv.nouvelleTable(this, nomTable);
			}else{
				afficherTable();
				ArrayList<String> nomTables = new ArrayList<>();
				for(GestionTable gt : serv.getTables())
					if(gt.getGcs().size()<8)
						nomTables.add(gt.getNom());
				String[] tabTables = new String[nomTables.size()];
				nomTables.toArray(tabTables);
				reponse = attendreReponse(tabTables);
				for(GestionTable gt : serv.getTables())
					if(gt.getNom().equalsIgnoreCase(reponse))
						gt.ajouterGc(this);
			}
		}
	}

	String attendreReponse(){
		String rep = null;

		while(rep == null){
			try{
				rep = in.readLine();
			}catch(IOException ignored){}
		}
		return rep;
	}

	String attendreReponse(String[] attendu){
		String rep = null;

		while(rep == null){
			try{
				rep = in.readLine();
				if(!contient(attendu, rep)) {
					rep = null;
					throw new IllegalArgumentException();
				}
			}catch(IllegalArgumentException iae) {
				String envoiException = "Votre réponse doit être parmi les suivantes : ";
				for(String r : attendu) envoiException += r + ", ";
				envoiException = envoiException.substring(0, envoiException.length()-2);
				out.println(envoiException);
			}catch(IOException ignored){}
		}
		return rep;
	}

	void ecrire(String message, boolean retourLigne){
		if(retourLigne)
			out.println(message);
		else
			out.print(message);
	}

	void ajouterDiamants(int nbDiamants){
	    this.nbDiamants += nbDiamants;
    }

    void quittez() {
        quittez = true;
    }
	
	void quittezPlusTours() {
		quittezPlusTours = true;
	}

    void nouvellePartie(){
	    quittez = false;
	    nbDiamants = 0;
    }

    void nouvelleManche(){
	    quittez = false;
		quittezPlusTours = false;
    }
	
	private boolean contient(String[] attendu, String s) {
		for(String att : attendu) {
			if(att.equalsIgnoreCase(s)) return true;
		}
		return false;
	}

	private void afficherTable() {
		StringBuilder affichage = new StringBuilder("Sélectionner la table : \n");
		for (GestionTable gt : serv.getTables()) {
			affichage.append("\t").append(gt.getNom());
		}
		out.println(affichage);
	}
	
	int diamants() {
		return nbDiamants;
	}

}
