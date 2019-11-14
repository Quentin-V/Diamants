package diamants;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class Client {
	private int				pochetTmp;
	private int				pochetSauvee;
	private boolean			sortit;
	private BufferedReader in;
	private PrintWriter	out;
	
	public Client(Socket s) {
		this.pochetTmp		= 0;
		this.pochetSauvee	= 0;
		this.sortit			= false;
		try {
			this.out	= new PrintWriter(s.getOutputStream(), true);
			this.in		= new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch(IOException ie) {
			System.out.println(ie);
		}
	}
	
	
}
