package diamants;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

class GestionClient implements Runnable {

	BufferedReader in;
	PrintWriter out;

	String name;
	int    nbDiamants;

	boolean quitter;
	boolean quitterPlusTours;

	private Serveur serv;

	Socket toClient;


	GestionClient(Socket s, Serveur serv){
		this.serv = serv;
		quitter = false;
		quitterPlusTours = false;
		name = "";
		toClient = s;
		try{
			out = new PrintWriter(s.getOutputStream(), true);
			in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		}catch(Exception ignored){}
	}


	public void run(){
		ecrire("Bonjour joueur, quel est ton nom ? ", false);
		this.name = attendreReponse();
		if(this.name != null) {
			selectionnTable();
		}
	}

	private void selectionnTable() {
		String reponse, nomTable;
		if (!serv.tableLibre()) {
			ecrire("Aucun table n'existe, vous êtes donc sur une nouvelle table." +
				   "\nDonner un nom a cette table : ", false);
			reponse = attendreReponse();
			serv.nouvelleTable(this, reponse);
		} else {
			ecrire("Voulez-vous créer une nouvelle table ?\nOui ou non : ", false);
			reponse = attendreReponse(new String[]{"oui", "non"});
			if (reponse.equalsIgnoreCase("Oui")) {
				ecrire("Choisissez le nom de votre table : ", false);
				nomTable = attendreReponse();
				serv.nouvelleTable(this, nomTable);
			} else {
				afficherTable();
				ArrayList<String> nomTables = new ArrayList<>();
				for (GestionTable gt : serv.getTables())
					if (gt.getGcs().size() < 8)
						nomTables.add(gt.getNom());
				String[] tabTables = new String[nomTables.size()];
				nomTables.toArray(tabTables);
				reponse = attendreReponse(tabTables);
				for (GestionTable gt : serv.getTables())
					if (gt.getNom().equalsIgnoreCase(reponse) && !gt.tableLancer)
						gt.ajouterGc(this);
			}
		}
	}

	String attendreReponse(){
		String rep = "";
		try {
			while(rep.equals("") && toClient.isConnected()){
				try{
					rep = in.readLine();
					System.out.println(rep); // DEBUG
				}catch(IOException ignored){}
			}
		}catch(NullPointerException npe) {this.serv.deconnexion(this);}
		return rep;
	}

	String attendreReponse(String[] attendu){
		String rep = null;

		while(rep == null){
			try{
				rep = in.readLine();
				if(rep == null)
					throw new ClientDecoException();
				if(!contient(attendu, rep)) {
					rep = null;
					throw new IllegalArgumentException();
				}
			}catch(ClientDecoException cde) {
				serv.deconnexion(this);
				Thread.currentThread().interrupt();
				break;
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
		//System.out.println("Envoyé : " + message); // DEBUG
		if(retourLigne) {
			out.println(message);
			out.print("");
			out.flush();
		} else {
			out.print(message);
			out.print("");
			out.flush();
		}
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

	void envoyerPlateau(ArrayList<Carte> plateau) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(this.toClient.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		out.println("plateau");
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			oos.writeObject(plateau);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
