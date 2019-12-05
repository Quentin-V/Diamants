package diamants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;

class PanelCartes extends JPanel {

	private FrameJeu frameJeu;
	private ArrayList<Carte> cartes;

	PanelCartes(ArrayList<Carte> cartes, FrameJeu frameJeu) {
		this.frameJeu = frameJeu;
		this.cartes = cartes;
		refresh();
	}

	void refresh() {
		this.removeAll();
		for(Carte c : this.cartes) {
			try {
				if(c instanceof CarteDanger) {
					this.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/"+((CarteDanger) c).danger+".png")))));
				}else if(c instanceof CarteTresor) {
					this.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/diam"+((CarteTresor) c).nbDiamants+".png")))));
				}else if(c instanceof CarteArtefact) {
					this.add(new JLabel(new ImageIcon(ImageIO.read(getClass().getResource("/arte"+((CarteArtefact) c).numArtefact+".png")))));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
