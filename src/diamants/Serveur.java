package diamants;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

class Serveur {

	private ArrayList<GestionClient> alGestionClient;
	private ArrayList<GestionTable> alGestionTable;

	Serveur(){
		try{
			alGestionClient = new ArrayList<GestionClient>();
			alGestionTable  = new ArrayList<GestionTable>();
			ServerSocket serverSocket = new ServerSocket(6000);
			while (true){ // on boucle
				// attendre patiemment un client
				Socket toClient  = serverSocket.accept();
				// créer un GestionClient pour traiter ce nouveau client
				GestionClient gdc = new GestionClient(toClient, this);
				alGestionClient.add(gdc);
				// mettre ce gérant de client dans une Thread
				Thread tgdc =  new Thread(gdc);
				// lancer la thread qui gérera ce client
				tgdc.start();
				//Le client ce déconnecte
				fin();
			}
		}catch(Exception e){System.out.println("Client pas cool"); e.printStackTrace();}
	}


	void nouvelleTable(GestionClient gc, String nomTable){
		alGestionTable.add(new GestionTable(gc, nomTable));
	}

	ArrayList<GestionTable> getTables(){
		return alGestionTable;
	}

	boolean tableLibre(){
		for(GestionTable gt : alGestionTable)
			if(gt.getGcs().size() < 8)
				return true;
		return false;
	}

	void fin(){}

	public static void main(String[] args) { // TEST TODO
		new Serveur();
	}

	public void ecrire(String test) {
		System.out.println("Il est parti");
	}

	public void deconnexion(GestionClient gestionClient) {
		// TODO
	}
}
