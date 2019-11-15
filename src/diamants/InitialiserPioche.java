package diamants;

import java.util.ArrayList;

/**
 * Classe qui permet d'initialiser la pioche du jeu.
 */
abstract class InitialiserPioche {

	/**
	 * Méthode qui crée un pioche avec toutes les cartes du jeu
	 * @return La pioche crée mélangée
	 */
	static ArrayList<Carte> initialisation() {
		ArrayList<Carte> piocheRetour = new ArrayList<>();

		int[] diamantsCartesTresor = new int[] {1,2,3,4,5,5,7,7,9,11,11,13,14,15,17}; // Valeurs de toutes les cartes trésor

		for(Integer nbDiamants : diamantsCartesTresor) { // Ajout des cartes trésor
			piocheRetour.add(new CarteTresor(nbDiamants));
		}

		String[] typesDangers = new String[] {"Feu", "Momie", "Rochers", "Serpents", "Araignees"}; // Type de cartes danger

		for(String danger : typesDangers)  // Ajout des cartes danger
			for(int i = 0; i < 3; i++) // Ajout 3 cartes de chaque danger
				piocheRetour.add(new CarteDanger(danger));

		for(int i = 0; i < 5; i++) // Ajout des artefacts
			piocheRetour.add(new CarteArtefact());

		piocheRetour = melanger(piocheRetour); // Mélange de la pioche

		return piocheRetour;
	}

	/**
	 * Méthode de mélange de la pioche.
	 * @param pioche
	 * @return Pioche donnée en paramètre mélangée
	 */
	private static ArrayList<Carte> melanger(ArrayList<Carte> pioche) {
		ArrayList<Carte> piocheMelangee = new ArrayList<>(); // Création d'une autre liste

		for(int i = 0; i < pioche.size(); i++) { // On prend au hasard des cartes de la pioche donnée et on let met dans la nouvzlle pioche
			piocheMelangee.add(pioche.remove((int) (Math.random() * pioche.size())));
		}

		return piocheMelangee;
	}

}
