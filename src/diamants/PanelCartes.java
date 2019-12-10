package diamants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

class PanelCartes extends JPanel {

	private FrameJeu frameJeu;
	private ArrayList<Carte> cartes;

	PanelCartes(ArrayList<Carte> cartes, FrameJeu frameJeu) {
		this.frameJeu = frameJeu;
		this.cartes = cartes;
		refresh(cartes);
	}

	void refresh(ArrayList<Carte> plateau) {
		this.cartes = plateau;
		this.removeAll();
		for(Carte c : this.cartes) {
			try {
				if(c instanceof CarteDanger) {
					ImageIcon image = new ImageIcon(ImageIO.read(getClass().getResource("/"+((CarteDanger) c).danger+".png")));
					image = new ImageIcon(image.getImage().getScaledInstance(120, 200, Image.SCALE_DEFAULT));
					this.add(new JLabel(image));
				}else if(c instanceof CarteTresor) {
					ImageIcon image = new ImageIcon(ImageIO.read(getClass().getResource("/diam"+((CarteTresor) c).nbDiamants+".png")));
				     image = new ImageIcon(image.getImage().getScaledInstance(120, 200, Image.SCALE_DEFAULT));
					this.add(new JLabel(image));
				}else if(c instanceof CarteArtefact) {
					ImageIcon image = new ImageIcon(ImageIO.read(getClass().getResource("/arte"+((CarteArtefact) c).numArtefact+".png")));
					image = new ImageIcon(image.getImage().getScaledInstance(120, 200, Image.SCALE_DEFAULT));
					this.add(new JLabel(image));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
