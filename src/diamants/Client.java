package diamants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

	Client() {
		String ip = null;
		while(ip == null) {
			System.out.print("Adresse IP : ");
			try {
				ip = attendreReponse();
				InetAddress.getByName(ip);
				/*if(!ip.matches("([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})|([A-Za-z0-9]+(\\.[A-Za-z]+)+)")) {
					throw new IllegalArgumentException();
				}*/
			}catch(IllegalArgumentException | UnknownHostException iae) {System.out.println("Adresse invalide.");}
		}
		String port = null;
		while(port == null) {
			System.out.print("Port : ");
			try {
				port = attendreReponse();
				int iPort = Integer.parseInt(port);
				if(iPort < 0 || iPort > 65355) {
					throw new IllegalArgumentException();
				}
			}catch(NumberFormatException nfe) {System.out.println("Port invalide.");}
			 catch(IllegalArgumentException iae) {System.out.println("Le port doit être compris entre 0 et 65355.");}
		}

		Socket toServer = null;
		BufferedReader in = null;
		PrintWriter out = null;
		try {
			toServer = new Socket(ip, Integer.parseInt(port));
			in = new BufferedReader(new InputStreamReader(toServer.getInputStream()));
			out = new PrintWriter(toServer.getOutputStream(), true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Listener listener = new Listener(in);
		Thread t = new Thread(listener);
		t.start();
		Writer writer = new Writer(out);
		Thread t2 = new Thread(writer);
		t2.start();
	}

	private class Listener implements Runnable {

		BufferedReader in;
		Listener(BufferedReader in) {
			this.in = in;
		}

		@Override
		public void run() {
			String l;
			while(true) {
				try {
					if ((l = in.readLine()) != null) {
						System.out.println(l);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private class Writer implements Runnable {

		PrintWriter out;
		BufferedReader sysIn;
		Writer(PrintWriter out) {
			this.out = out;
			this.sysIn = new BufferedReader(new InputStreamReader(System.in));
		}

		@Override
		public void run() {
			while(true) {
				try {
					this.out.println(this.sysIn.readLine());
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(300);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	String attendreReponse() {
		String rep = null;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(rep == null){
			try{
				rep = in.readLine();
				System.out.println(rep);
			}catch(IOException ignored){}
		}
		return rep;
	}

	public static void main(String[] args) {
		new Client();
	}

}
