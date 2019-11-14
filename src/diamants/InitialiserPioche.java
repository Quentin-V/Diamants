package diamants;

import java.util.ArrayList;

abstract class InitialiserPioche {

	ArrayList<Carte> initalisation() {
		ArrayList<Carte> piocheRetour = new ArrayList<>();

		int[] diamantsCartesTresor = new int[] {1,2,3,4,5,5,7,7,9,11,11,13,14,15,17}; // Valeurs de toutes les cartes trésor

		for(Integer nbDiamants : diamantsCartesTresor) { // Ajout des cartes trésor
			piocheRetour.add(new CarteTresor(nbDiamants));
		}

		String[] typesDangers = new String[] {"Feu", "Momie", "Rochers", "Serpents", "Araignees"}; // Type de cartes danger

		for(String danger : typesDangers) { // Ajout des cartes danger
			for(int i = 0; i < 3; i++) // Ajout 3 cartes de chaque danger
				piocheRetour.add(new CarteDanger(danger));
		}

		for(int i = 0; i < 5; i++)
			piocheRetour.add(new CarteArtefacat());

		return piocheRetour;
	}

	private ArrayList<Carte> melanger(ArrayList<Carte> alC) {
		ArrayList<Carte> pioche = alC;

		return alC;
	}

}
