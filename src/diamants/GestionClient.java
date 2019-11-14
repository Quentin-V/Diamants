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
	private String name;

	private Serveur serv;


	GestionClient(Socket s, Serveur serv){
		this.serv = serv;
		try{
			out = new PrintWriter(s.getOutputStream(), true);
			in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
		}catch(Exception e){}
	}


	public void run(){
		out.println("Voulez-vous créer une nouvelle table ?");
		String reponse;
		if(!serv.tableLibre()) {
			out.println("Aucun table n'existe, vous êtes donc sur une nouvelle table.");
			serv.nouvelleTable(this);
		}else {
			reponse = attendreReponse(new String[] {"oui", "non"});
			if(reponse.equalsIgnoreCase("Oui")){
				serv.nouvelleTable(this);
			}else{
				afficherTable();
				ArrayList<String> nomTables = new ArrayList<>();
				for(GestionTable gt : serv.getTables())
					if(gt.getGcs().size()<8)
						nomTables.add(gt.getNom());
				reponse = attendreReponse((String[]) nomTables.toArray());
			}
		}
	}

	private String attendreReponse(){
		String rep = null;

		while(rep == null){
			try{
				rep = in.readLine();
			}catch(IOException ignored){}
		}
		return rep;
	}

	private String attendreReponse(String[] attendu){
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
				envoiException = envoiException.substring(0, envoiException.length()-3);
				out.println(envoiException);
			}catch(IOException ignored){}
		}
		return rep;
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
}
