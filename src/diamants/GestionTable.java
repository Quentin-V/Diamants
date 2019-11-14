package diamants;

class GestionTable implements Runnable{

  private BufferedReader in;
  private PrintWriter    out;
  private Server serv;

  GestionTable(Socket s, Serveur serv){
    this.serv = serv;
    try{
      out = new PrintWriter(s.getOutputStream(), true);
      in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
    }catch(Exception e){}

    try{
      ServerSocket serverSocket = new ServerSocket(6100);
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
    }catch(Exception e){System.out.println("Client est casser");}
  }
}
