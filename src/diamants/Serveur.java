package diamants;

class Serveur {

  private ArrayList<GerantDeClient> allGerantClient;

  public Serveur(){
    try{
			allGerantClient = new ArrayList<GerantDeClient>();
			GerantDeClient.createArray();
			ServerSocket serverSocket = new ServerSocket(6000);
			while (true) // on boucle
			{
				// attendre patiemment un client
				Socket s  = serverSocket.accept();
				// créer un GerantDeClient pour traiter ce nouveau client
				GerantDeClient gdc = new GerantDeClient(s, this);
				allGerantClient.add(gdc);
				// mettre ce gérant de client dans une Thread
				Thread tgdc =  new Thread(gdc);
				// lancer la thread qui gérera ce client
				tgdc.start();
			}
		}catch(Exception e){System.out.println("Table est casser");}
  }

}
