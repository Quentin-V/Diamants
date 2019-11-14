package diamants;

class CarteDanger extends Carte {
	String danger;

	CarteDanger(String danger) {
		super("Danger");
		this.danger = danger;
	}

	@Override
	public String toString() {
		return this.danger;
	}
}
