import jade.core.Agent;

public class Main extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected void setup() {
		System.out.println("Alo Mundo! ");
		System.out.println("Meu nome: "+ getLocalName());
	}
}
