package diamants;

import java.util.ArrayList;

class GestionTable implements Runnable{

  final int MINIMUM_JOUEUR = 1;

  ArrayList<GestionClient> gcs;
  String nom;
  boolean tableLancer = false;

  GestionTable(GestionClient gc, String nomTable){
    this.gcs = new ArrayList<>();
	nom = nomTable;
    this.gcs.add(gc);
    Thread t = new Thread(this);
	t.start();
  }

  public void run() {
    String rep;
    int nbRestant;
    boolean rejouer = true;
    ArrayList<Carte> pioche;
    ArrayList<Carte> plateau = new ArrayList<>();
    int[] stockDiamantsTmp;
    int nbDiamantPlateau;
    int nbQuitteur;
    int nbArtSorti;
    Carte carte;

    lancerTable();

    while(rejouer){
      for(int i = 0; i < 5; i++) {
		plateau = new ArrayList<>();
        stockDiamantsTmp = new int[gcs.size()];
        nbDiamantPlateau = 0;
        nbArtSorti = 0;
        pioche = InitialiserPioche.initialisation();
        nbRestant = gcs.size();
        while (nbRestant > 0) {
          nbQuitteur = 0;
          carte = pioche.get((int) (Math.random() * pioche.size()));
          switch (carte.type) {
            case "Artefact":
              ++nbArtSorti;
              break;
            case "Danger":
              if(checkDanger(plateau, ((CarteDanger)carte).danger))nbRestant = 0;
              break;
            case "Tresor":
              for (GestionClient gc : gcs)
                 if(!gc.quittez)
					 stockDiamantsTmp[gcs.indexOf(gc)] += ((CarteTresor) carte).nbDiamants/nbRestant;
              nbDiamantPlateau += ((CarteTresor) carte).nbDiamants % nbRestant;
              break;

          }
          plateau.add(carte);
		  
		  for (GestionClient gc : gcs) 
				gc.ecrire("Les cartes en jeu sont : " + listeCartes(plateau), true);
		  
		  if(nbRestant != 0) {
			  for (GestionClient gc : gcs) {
				if(!gc.quittez) {
					gc.ecrire("Voulez-vous rester ou quittez ?", true);
					rep = gc.attendreReponse(new String[]{"rester", "quittez"});
					if (rep.equalsIgnoreCase("quittez")) {
					  gc.quittez();
					  nbRestant--;
					  nbQuitteur++;
					}
				}
			  }
			  for(GestionClient gc :gcs){
				if(gc.quittez && !gc.quittezPlusTours){
				  gc.ajouterDiamants(stockDiamantsTmp[gcs.indexOf(gc)] + nbDiamantPlateau/nbQuitteur);
				  stockDiamantsTmp[gcs.indexOf(gc)] = 0;
				  if(nbQuitteur == 1){
					for(Carte c : plateau){
					  if(c instanceof CarteArtefact)
						if(nbArtSorti < 4)
						  gc.ajouterDiamants(5);
						else
						  gc.ajouterDiamants(10);
					}
				  }
				  gc.quittezPlusTours();
				}

				if(nbQuitteur != 0)
					nbDiamantPlateau = nbDiamantPlateau%nbQuitteur;
			  }
				  
			  for (GestionClient gc1 : gcs)
				  for (int u = 0; u<gcs.size();u++)
					gc1.ecrire("Le nombre de diamants du joueur " + u + " sur lui est de " + stockDiamantsTmp[u], true);
			
			}
		}
	    for (GestionClient gc1 : gcs)
		  for (int u = 0; u<gcs.size();u++)
			gc1.ecrire("Le nombre de diamants du joueur " + u + " à son campement est de " + gcs.get(u).diamants(), true);
        
		for (GestionClient gc : gcs)
          gc.nouvelleManche();
      }
      tableLancer = false;
      gcs.get(0).ecrire("Voulez-vous rejouer ?", true);
      rep = gcs.get(0).attendreReponse(new String[] {"oui", "non"});
      tableLancer = true;
      rejouer = rep.equalsIgnoreCase("oui");
      for(GestionClient gc : gcs)
        gc.nouvellePartie();
    }
  }

  private void lancerTable(){
    gcs.get(0).ecrire("Voulez-vous lancez la partie ?", true);
    String rep = "";
    do{
      if(rep.equalsIgnoreCase("oui"))
        gcs.get(0).ecrire("Il n'y a pas assez de joueur", true);
      rep = gcs.get(0).attendreReponse(new String[] {"oui"});
    }while(gcs.size() < MINIMUM_JOUEUR);

    tableLancer = true;
  }

  private boolean checkDanger(ArrayList<Carte> plateau, String danger) {
    for(Carte c : plateau)
      if(c instanceof CarteDanger && ((CarteDanger) c).danger.equals(danger)) return true;
    return false;
  }

  private String listeCartes(ArrayList<Carte> plateau) {
    StringBuilder out = new StringBuilder();
    for(Carte c : plateau){
      out.append(c).append(" | ");
    }
    return out.toString();
  }

  String getNom(){ return nom; }
  ArrayList<GestionClient> getGcs() { return gcs; }
  void ajouterGc(GestionClient gc){ gcs.add(gc); }

}

