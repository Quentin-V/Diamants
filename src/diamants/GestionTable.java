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

				deconnecte();

				for(GestionClient gc : gcs){
					System.out.println("Client n°" + gcs.indexOf(gc));
				}

				plateau = new ArrayList<>();
				stockDiamantsTmp = new int[gcs.size()];
				nbDiamantPlateau = 0;
				nbArtSorti = 0;
				pioche = InitialiserPioche.initialisation();
				nbRestant = gcs.size();


				messagePourTous("Vous commancez l'exploration du temple n°"+(i+1), true);

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

					messagePourTous("Les cartes en jeu sont : " + listeCartes(plateau), true);

					if(nbRestant != 0) {

						for (GestionClient gc1 : gcs) {
							gc1.ecrire("Nombre de diamants sur le plateau : " + nbDiamantPlateau, true);
							gc1.ecrire("Nombre de diamants en cours : ", true);
							for (int u = 0; u<gcs.size();u++)
								gc1.ecrire("\t" + u + " : " + stockDiamantsTmp[u], true);
						}

						ArrayList<GestionClient> gcsEnJeu = new ArrayList<>();
						for (GestionClient gc : gcs) {
							if(!gc.quitter && !deconnecte()) {
								gc.ecrire("Voulez-vous rester ou quitter : ", false);
								if(!deconnecte())
    								gcsEnJeu.add(gc);
							}
						}
						ArrayList<Boolean> rester = attendreResterOuQuitter(gcsEnJeu);
						for(int j = 0; j < rester.size(); ++j) {
							if(!rester.get(j)) {
								gcsEnJeu.get(j).quitter();
								nbRestant--;
								nbQuitteur++;
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

    private boolean deconnecte() {
		for(GestionClient gc : gcs){
			//System.out.println("Gc num : " + gcs.indexOf(gc) + "\n\tConnected : " + gc.toClient.isConnected() + "\n\tClosed : " + gc.toClient.isClosed());
			try {
				if(!gc.toClient.getInetAddress().isReachable(500)){
					gcs.remove(gc);
					messagePourTous(gc.name + " s'est déconnecté", true);
					return true;
				}
			} catch (IOException e) {
			}
		}

	    return false;
    }

    private void messagePourTous(String message, boolean retourLigne) {
        for(GestionClient gc : gcs){
            gc.ecrire(message,retourLigne);
        }
	}

    private ArrayList<Boolean> attendreResterOuQuitter(ArrayList<GestionClient> gcsEnJeu) {
		ArrayList<Boolean> rester = new ArrayList<>();
		for(int i = 0; i < gcsEnJeu.size(); ++i) {
			rester.add(null);
		}

		ArrayList<ResterQuitter> roqs = new ArrayList<>();
		for(GestionClient gc : gcsEnJeu) {
			roqs.add(new ResterQuitter(null, gc));
			Thread t = new Thread(roqs.get(gcsEnJeu.indexOf(gc)));
			t.start();
		}

		int timer = 0;
		while(rester.contains(null) && timer < 200) {

			for(ResterQuitter roq : roqs) {
				if(roq.rester != null) rester.set(roqs.indexOf(roq), roq.rester);
			}
			try {
				Thread.sleep(100);
				++timer;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			/*
			for(GestionClient gc : gcsEnJeu) {
				if(rester.get(gcsEnJeu.indexOf(gc)) != null) continue;
				System.out.println("Dans le try");
				String reponse = gc.attendreReponse(new String[] {"rester", "quitter"});
				boolean reste = true;
				if(reponse.equalsIgnoreCase("quitter")) {
					reste = false;
				}
				rester.set(gcsEnJeu.indexOf(gc), reste);
			}

			 */
		}
		if(rester.contains(null)) {
			for(ResterQuitter roq : roqs) {
				if(roq.rester == null) rester.set(roqs.indexOf(roq), false);
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

