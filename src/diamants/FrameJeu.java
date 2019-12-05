package diamants;

import javax.swing.*;
import java.awt.*;

class FrameJeu extends JFrame {

	private PanelCartes pnlCartes;
	private PanelJoueurs pnlJoueurs;
	private Diamants diamants;

	FrameJeu(Diamants diamants) {

		this.diamants = diamants;

		this.setTitle("Diamants");
		this.setSize(1280, 720);
		this.setLocation(200, 100);

		this.setLayout(new GridLayout(2, 1));

		pnlCartes = new PanelCartes(InitialiserPioche.initialisation(), this); // TODO Relier les cartes visibles au panelcartes

		pnlJoueurs = new PanelJoueurs(2,this);

		this.add(pnlCartes);

		this.setVisible(true);
	}

}
