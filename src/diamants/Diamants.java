package diamants;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;

class Diamants {

	//private FrameJeu frJeu;
	//private Client client;

	private Diamants() {
		new Launcher(this);
	}

	private void lancer(Socket s) {
		/*this.frJeu = */new FrameJeu(this);
		/*this.client = */new Client(s);
	}

	public static void main(String[] args) {
		new Diamants();
	}

	private static class Launcher extends JFrame implements ActionListener {

		JTextField tfAdresse, tfPort;

		Diamants diam;

		Launcher(Diamants diam) {

			this.diam = diam;

			this.setTitle("Launcher");
			this.setLocation(400, 300);
			this.setSize(365, 215);
			this.setLayout(null);

			JLabel lblAdresseIp = new JLabel();
			lblAdresseIp.setBounds(26, 24, 70, 20);
			lblAdresseIp.setText("Adresse IP");

			JLabel lblPort = new JLabel();
			lblPort.setBounds(26, 70, 70, 20);
			lblPort.setText("Port");

			JButton btnConnexion = new JButton();
			btnConnexion.setBounds(121, 128, 100, 30);
			btnConnexion.setText("Connexion");
			btnConnexion.addActionListener(this);

			this.tfAdresse = new JTextField(10);
			this.tfAdresse.setBounds(113, 24, 195, 26);

			this.tfPort = new JTextField();
			this.tfPort.setBounds(113, 70, 103, 26);

			this.add(lblAdresseIp);
			this.add(lblPort);
			this.add(tfAdresse);
			this.add(tfPort);
			this.add(btnConnexion);

			this.setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int port = Integer.parseInt(this.tfPort.getText());
				if(port > 65535 || port < 0) throw new NumberFormatException("Port invalide");
				if(tfAdresse.getText().matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})|([A-Za-z0-9]+(\\.[A-Za-z]+)+)")) {
					Socket s = new Socket(tfAdresse.getText(), port);
					diam.lancer(s);
					this.dispose();
				}else {
					throw new AdresseIncorrecteException("Adresse invalide");
				}
			}catch(NumberFormatException nfe) {
				new FrameErreur("Le port saisi est invalide");
			}catch(IllegalArgumentException iae) {
				new FrameErreur("L'adresse saisie est invalide");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

}
