package diamants;

class GestionClient implements Runnable{

  private BufferedReader in;
  private PrintWriter    out;
  private String name;

  private ServeurSimple ss;


  GerantDeClient(Socket s, ServeurSimple ss){
    this.ss = ss;
    try{
      out = new PrintWriter(s.getOutputStream(), true);
      in  = new BufferedReader( new InputStreamReader(s.getInputStream()) );
    }catch(Exception e){}
  }

  public void run(){

    System.out.println("Un nouveau client s'est connecte");
    try{
      out.println("Bienvenue sur le serveur de Pierre");
      out.println("Quel est votre nom ?");
      name = in.readLine();
      System.out.println("Son nom est : " + name);

      String message = "";
      while(!message.equalsIgnoreCase("quitter")){
        message = in.readLine();
        if(!message.equalsIgnoreCase("quitter") && !message.equalsIgnoreCase(""))
          ss.giveMessage(name, message);
      }
    }catch(Exception e){}

    fin();
  }
}
