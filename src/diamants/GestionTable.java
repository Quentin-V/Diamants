package diamants;

class GestionTable implements Runnable{

  private ArrayList<GestionClient> gc;
  private Server serv;
  private String nom;

  GestionTable(GestionClient gc, Serveur serv){
    this.serv = serv;
    this.gc = gc;
	nom = "truc";

  }

  public 

  String getNom(){
	  return nom;
  }
}
