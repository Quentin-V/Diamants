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
			ServerSocket serverSocket = new ServerSocket(8000);
			while (true){ // on boucle

				for(GestionTable gt : alGestionTable) {
					if(gt.gcs.size() == 0) alGestionTable.remove(gt);
				}

				// attendre patiemment un client
				Socket toClient  = serverSocket.accept();
				// créer un GestionClient pour traiter ce nouveau client
				GestionClient gdc = new GestionClient(toClient, this);
				alGestionClient.add(gdc);
				// mettre ce gérant de client dans une Thread
				Thread tgdc =  new Thread(gdc);
				// lancer la thread qui gérera ce client
				tgdc.start();
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
			if(gt.getGcs().size() < 8 && !gt.tableLancer)
				return true;
		return false;
	}

	public static void main(String[] args) { // TEST TODO
		new Serveur();
	}

	void deconnexion(GestionClient gestionClient) {
		for(GestionTable gt : alGestionTable) {
			if(gt.gcs.contains(gestionClient)) {
				gt.gcs.remove(gestionClient);
				gt.messagePourTous(gestionClient.name + " s'est deconnecté", true);
			}
		}
		gestionClient = null;
	}
}
