package diamants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

class GestionClient implements Runnable {

	BufferedReader in;
	PrintWriter out;

	String  name;
	private int     nbDiamants;
	boolean quitter;
	boolean quitterPlusTours;

	private Serveur serv;
	
	Socket toClient;

	GestionClient(Socket s, Serveur serv){
		this.serv = serv;
		quitter = false;
		quitterPlusTours = false;
		toClient = s;
		try{
			out = new PrintWriter(s.getOutputStream(), true);
			in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		}catch(Exception e){}
	}


	public void run(){
		String reponse, nomTable;
		boolean dejaExistante = false;
		if(!serv.tableLibre()) {
			out.print("Aucun table n'existe, vous êtes donc sur une nouvelle table." +
					  "\nDonner un nom a cette table : ");
			out.flush();
			reponse = attendreReponse();
			for(GestionTable gt : serv.getTables())
				if(gt.getNom().equals(reponse))
					dejaExistante = true;
			while(dejaExistante) {
				out.print("Ce nom est déjà utilisé, donner un autre nom a cette table : ");
				out.flush();
				reponse = attendreReponse();
				for(GestionTable gt : serv.getTables())
					if(gt.getNom().equals(reponse))
						dejaExistante = true;
					else
						dejaExistante = false;
			}
			serv.nouvelleTable(this, reponse);
		}else {
			out.print("Voulez-vous créer une nouvelle table ?\nOui ou non : ");
			out.flush();
			reponse = attendreReponse(new String[] {"oui", "non"});
			if(reponse.equalsIgnoreCase("Oui")){
				out.print("Choisissez le nom de votre table : ");
				out.flush();
				nomTable = attendreReponse();
				for(GestionTable gt : serv.getTables())
					if(gt.getNom().equals(nomTable))
						dejaExistante = true;
				while(dejaExistante) {
					out.print("Ce nom est déjà utilisé, donner un autre nom a cette table : ");
					out.flush();
					nomTable = attendreReponse();
					for(GestionTable gt : serv.getTables())
						if(gt.getNom().equals(nomTable))
							dejaExistante = true;
						else
							dejaExistante = false;
				}
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
					if(gt.getNom().equalsIgnoreCase(reponse) && !gt.tableLancer)
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
			out.flush();
	}

	void ajouterDiamants(int nbDiamants){
	    this.nbDiamants += nbDiamants;
    }

    void quitter() {
        quitter = true;
    }
	
	void quitterPlusTours() {
		quitterPlusTours = true;
	}

    void nouvellePartie(){
	    quitter = false;
	    nbDiamants = 0;
    }

    void nouvelleManche(){
	    quitter = false;
		quitterPlusTours = false;
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
			if(!gt.tableLancer) affichage.append("\t").append(gt.getNom());
		}
		out.println(affichage);
	}
	
	int diamants() {
		return nbDiamants;
	}

}
