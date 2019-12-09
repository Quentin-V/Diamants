package diamants;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Classe qui crée une frame d'avertissement avec un message passé en paramètre du constructeur
 */
class FrameErreur extends JFrame {
	FrameErreur(String s) {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setTitle("Erreur");
		this.setLocation(500, 500);
		this.setLayout(new FlowLayout());
		JLabel lblWarn = new JLabel();
		try {
			lblWarn.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("/warn.png"))));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.add(lblWarn);
		this.add(new JLabel(s));
		this.pack();
		this.setVisible(true);
	}
}
