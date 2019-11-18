package diamants;

import java.io.IOException;
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
                 if(!gc.quitter)
					 stockDiamantsTmp[gcs.indexOf(gc)] += ((CarteTresor) carte).nbDiamants/nbRestant;
              nbDiamantPlateau += ((CarteTresor) carte).nbDiamants % nbRestant;
              break;

          }
          plateau.add(carte);
		  
		  for (GestionClient gc : gcs) 
				gc.ecrire("Les cartes en jeu sont : " + listeCartes(plateau), true);
		  
		  if(nbRestant != 0) {

		  	System.out.println("Nombre de diamants sur le plateau : " + nbDiamantPlateau);

			  for (GestionClient gc1 : gcs) {
				  gc1.ecrire("Nombre de diamants en cours : ", true);
				  for (int u = 0; u<gcs.size();u++)
					  gc1.ecrire("\t" + u + " : " + stockDiamantsTmp[u], true);
			  }

			  ArrayList<GestionClient> gcsEnJeu = new ArrayList<>();
			  for (GestionClient gc : gcs) {
			    if(!gc.quitter) {
				    gc.ecrire("Voulez-vous rester ou quitter ?", true);
				    gcsEnJeu.add(gc);
			    }
			  }
			  ArrayList<Boolean> rester = attendreResterOuQuitter(gcsEnJeu);
			  System.out.print("RESTER : ");
			  System.out.println(rester);
			  for(int j = 0; j < rester.size(); ++j) {
				  if(!rester.get(j)) {
					  gcsEnJeu.get(j).quitter();
					  nbRestant--;
					  nbQuitteur++;
					  System.out.println("Etat quitter gcgeti : " + gcsEnJeu.get(j).quitter);
				  }
			  }
			  for(GestionClient gc :gcs){
				if(gc.quitter && !gc.quittezPlusTours){
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

	private ArrayList<Boolean> attendreResterOuQuitter(ArrayList<GestionClient> gcsEnJeu) {
  	    ArrayList<Boolean> rester = new ArrayList<>();
  	    for(int i = 0; i < gcsEnJeu.size(); ++i) {
  	    	rester.add(null);
        }
  	    while(rester.contains(null)) {
  	    	for(GestionClient gc : gcsEnJeu) {
		        try {
			        String reponse = gc.in.readLine();
			        boolean reste;
			        if(reponse.equalsIgnoreCase("rester")) {
			        	reste = true;
			        }else if(reponse.equalsIgnoreCase("quitter")) {
			        	reste = false;
			        }else throw new IllegalArgumentException();
			        rester.set(gcsEnJeu.indexOf(gc), reste);
		        } catch (IllegalArgumentException iae) {gc.ecrire("Votre réponse doit être rester ou quitter.", true);}
		          catch (IOException ignored) {}
  	    	}
        }
  	    return rester;
	}

	private void lancerTable(){
    gcs.get(0).ecrire("Ecrivez lancer lorsque vous voulez lancer la partie", true);
    String rep = "";
    do{
      if(rep.equalsIgnoreCase("lancer"))
        gcs.get(0).ecrire("Il n'y a pas assez de joueur", true);
      rep = gcs.get(0).attendreReponse(new String[] {"lancer"});
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
  void ajouterGc(GestionClient gc) {
  	gcs.add(gc);
  	for(GestionClient unGc : gcs) {
  		if(unGc != gc) {
  			unGc.ecrire("Un joueur s'est connecté !", true);
	    }else {
  			unGc.ecrire("Vous êtes connecté à la table : " + this.nom, true);
	    }
    }
  }

}

