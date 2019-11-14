package diamants;


class Client {
	private int				pochetTmp
	private int				pochetSauvee;
	private boolean			sortit;
	private BufferedReader	in;
	private PrintWritter	out;
	
	public Client(Socket s) {
		this.pochetTmp		= 0;
		this.pochetSauvee	= 0;
		this.sortit			= false;
		try {
			this.out	= new PrintWriter(s.getOutputStream(), true);
			this.in		= new BufferedReader(new InputStreamReader(s.getInputStream()));
		}
		catch(IOExeption ie) {
			System.out.println(ie);
		}
	}
	
	
}
