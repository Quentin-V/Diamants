package diamants;

class Serveur {

	private ArrayList<GestionClient> alGestionClient;
	private ArrayList<GestionTable>  alGestionTable;

	Serveur(){
		try{
			alGestionClient = new ArrayList<GestionClient>();
			alGestionTable  = new ArrayList<GestionTable>();
			ServerSocket serverSocket = new ServerSocket(6000);
			while (true){ // on boucle
				// attendre patiemment un client
				Socket toClient  = serverSocket.accept();
				// créer un GestionClient pour traiter ce nouveau client
				GestionClient gdt = new GestionClient(s, this);
				alGestionClient.add(gdc);
				// mettre ce gérant de client dans une Thread
				Thread tgdc =  new Thread(gdc);
				// lancer la thread qui gérera ce client
				tgdc.start();
			}
		}catch(Exception e){System.out.println("Client pas cool");}
	}


	void nouvelleTable(GestionClient gc){
		alGestionTable.add(new GestionTable());
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

}
