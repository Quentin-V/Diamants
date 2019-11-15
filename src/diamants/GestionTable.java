package diamants;

import java.util.ArrayList;

class GestionTable implements Runnable{

  private ArrayList<GestionClient> gcs;
  private Serveur serv;
  private String nom;

  GestionTable(GestionClient gc, Serveur serv, String nomTable){
    this.serv = serv;
    this.gcs = new ArrayList<>();
	nom = nomTable;
    this.gcs.add(gc);

  }

  public void run() {
    String rep;
    int nbRestant;
    boolean rejouer = true;

    while(rejouer){
      for(int i = 0; i < 5; i++){
        nbRestant = gcs.size();
        while(nbRestant > 0){
          for(GestionClient gc : gcs) {
            gc.ecrire("Voulez-vous rester ou quittez ?", true);
            rep = gc.attendreReponse(new String[] {"rester", "quittez"});
            if(rep.equalsIgnoreCase("quittez")) {
              nbRestant --;
            }
          }
        }
      }
    }
  }

  String getNom(){ return nom; }
  ArrayList<GestionClient> getGcs() { return gcs; }
  void ajouterGc(GestionClient gc){ gcs.add(gc); }

}

