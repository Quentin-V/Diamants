package diamants;

import java.util.ArrayList;

class GestionTable implements Runnable{

	private final int MINIMUM_JOUEUR = 1;

	ArrayList<GestionClient> gcs;
	private String nom;
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
		ArrayList<Carte> plateau;
		int[] stockDiamantsTmp;
		int nbDiamantPlateau;
		int nbQuitteur;
		int nbArtSorti;
		Carte carte;

		lancerTable();

		while(rejouer){

			for(int i = 0; i < 5; i++) {

				for(GestionClient gc : gcs){
					System.out.println("Client n°" + gcs.indexOf(gc));
				}

				plateau = new ArrayList<>();
				stockDiamantsTmp = new int[gcs.size()];
				nbDiamantPlateau = 0;
				nbArtSorti = 0;
				pioche = InitialiserPioche.initialisation();
				nbRestant = gcs.size();

				effacerToutLeMonde();
				messagePourTous("Vous commencez l'exploration du temple n°"+(i+1), true);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) { e.printStackTrace(); }

				while (nbRestant > 0) {
					nbQuitteur = 0;
					carte = pioche.get((int) (Math.random() * pioche.size()));

					switch (carte.type) {
						case "Artefact":
							++nbArtSorti;
							break;
						case "Danger":
							if(checkDanger(plateau, ((CarteDanger)carte).danger)) {
								plateau.add(carte);
								afficherInfos(plateau, nbDiamantPlateau, stockDiamantsTmp);
								messagePourTous("Deux dangers de type " + ((CarteDanger)plateau.get(plateau.size()-1)).danger + " sont apparus, l'exploration se termine !", true);
								try {
									Thread.sleep(5000);
								} catch (InterruptedException e) { e.printStackTrace(); }
								nbRestant = 0;
							}
							break;
						case "Tresor":
							for (GestionClient gc : gcs)
								if(!gc.quitter)
									stockDiamantsTmp[gcs.indexOf(gc)] += ((CarteTresor) carte).nbDiamants/nbRestant;
							nbDiamantPlateau += ((CarteTresor) carte).nbDiamants % nbRestant;
							break;

					}

					if(nbRestant != 0) {

						plateau.add(carte);
						afficherInfos(plateau, nbDiamantPlateau, stockDiamantsTmp);

						ArrayList<GestionClient> gcsEnJeu = new ArrayList<>();
						for (GestionClient gc : gcs) {
							if(!gc.quitter) {
								gc.ecrire("Voulez-vous rester ou quitter : ", false);
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
							if(gc.quitter && !gc.quitterPlusTours){
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
								gc.quitterPlusTours();
							}

							if(nbQuitteur != 0)
								nbDiamantPlateau = nbDiamantPlateau%nbQuitteur;
						}

					}
				}

				for (GestionClient gc : gcs)
					gc.nouvelleManche();
			}
			int maxDiam = 0;
			ArrayList<GestionClient> gagnants = new ArrayList<>();

			//Trouver le/les gagnants
			for(GestionClient joueur : gcs) {
				if(joueur.nbDiamants > maxDiam) {
					gagnants.clear();
					gagnants.add(joueur);
					maxDiam = joueur.nbDiamants;
				}else if(joueur.nbDiamants == maxDiam) {
					gagnants.add(joueur);
				}
			}

			if(gagnants.size() == 1) {
				messagePourTous("Le gagnant est : " + gagnants.get(0).name + " avec " + gagnants.get(0).nbDiamants + " diamants.", true);
			}else {
				messagePourTous("Les gagnants sont : ", true);
				for(GestionClient gagnant : gagnants) {
					messagePourTous("\t" + gagnant.name + "avec " + gagnant.nbDiamants + " diamants.", true);
				}
			}

			for (GestionClient gc1 : gcs) {
				messagePourTous("Infos de la partie : ", true);
				for (GestionClient gc : gcs)
					gc1.ecrire("\t" + gc.name + " a " + gc.nbDiamants + " diamants à son campement", true);
			}


			tableLancer = false;
			try {
				gcs.get(0).ecrire("Voulez-vous rejouer ?", true);
			} catch (Exception e) {
				break;
			}
			rep = gcs.get(0).attendreReponse(new String[] {"oui", "non"});
			tableLancer = true;
			rejouer = rep.equalsIgnoreCase("oui");
			for(GestionClient gc : gcs)
				gc.nouvellePartie();
		}
	}

	private void afficherInfos(ArrayList<Carte> plateau, int nbDiamantPlateau, int[] stockDiamantsTmp) {
		effacerToutLeMonde();

		messagePourTous("Les cartes en jeu sont : " + listeCartes(plateau), true);

		for (GestionClient gc1 : gcs) {
			gc1.ecrire("Nombre de diamants sur le plateau : " + nbDiamantPlateau, true);
			gc1.ecrire("Nombre de diamants en cours : ", true);
			for (int u = 0; u<gcs.size();u++)
				gc1.ecrire(String.format("\t%20s : %2d", gcs.get(u).name, stockDiamantsTmp[u]), true);
			gc1.ecrire("Vos diamants sauvegardés : " + gc1.nbDiamants, true);
		}
	}

	void messagePourTous(String message, boolean retourLigne) {
        for(GestionClient gc : gcs){
            gc.ecrire(message,retourLigne);
        }
	}

	void effacerToutLeMonde() {
		messagePourTous("\033[H\033[2J", false);
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
			if(timer % 10 == 0 && timer >= 160) {
				messagePourTous( (20-(timer/10)) + "...", true);
			}
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
		for(Carte c : plateau) {
			if(c instanceof CarteDanger && ((CarteDanger) c).danger.equals(danger))
				return true;
		}
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
		messagePourTousLesAutresQue(gc, gc.name + " s'est connecté !", true);
		gc.ecrire("Vous êtes connecté à la table : " + this.nom, true);
	}

	private void messagePourTousLesAutresQue(GestionClient gc, String message, boolean retourligne) {
		for(GestionClient unGc : gcs)
			if(unGc != gc)
				unGc.ecrire(message, retourligne);
	}

}

