package diamants;

import javax.swing.*;
import java.awt.*;

class PanelJoueurs extends JPanel {

	PanelJoueurs(int nbJoueurs, FrameJeu frameJeu) {
		if(nbJoueurs < 4) this.setLayout(new GridLayout(1, nbJoueurs));
		else              this.setLayout(new GridLayout(nbJoueurs/3+1, 3));
	}

}
