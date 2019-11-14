package diamants;

import java.util.ArrayList;

class GestionTable implements Runnable{

  private ArrayList<GestionClient> gcs;
  private Serveur serv;
  private String nom;

  GestionTable(GestionClient gc, Serveur serv){
    this.serv = serv;
    this.gcs = new ArrayList<>();
    this.gcs.add(gc);
	nom = "truc";

  }

  public void run() {

  }

  String getNom(){
	  return nom;
  }

  ArrayList<GestionClient> getGcs() {
    return gcs;
  }
}
