import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import java.util.Random;

public class Hero extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7779051071406903573L;
	
	private int agility;
	private int skill;
	private int accuracy;
	private int trickery;
	private int points;
	
	public int getPoints() {
		return this.points;
	}
	
	class waitingForDuel extends CyclicBehaviour {

		private static final long serialVersionUID = 3639679338986836773L;
		public waitingForDuel(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Inicializando waitingForDuel");
			ACLMessage msg = myAgent.receive();
			if(msg != null) {
				ACLMessage reply = msg.createReply();
				if(msg.getPerformative() == ACLMessage.PROPOSE) {
					String content = msg.getContent();
					if(content != null) {
						Random rollingDice = new Random();
						int acceptingChance = rollingDice.nextInt(100);
						if (acceptingChance >= 30) {
							reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
							reply.setContent("Desafio aceito seu maltrapilho!!");
						} else {
							reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
							reply.setContent("Ainda não sou forte o suficiente!");
						}

					} else {
						reply.setPerformative(ACLMessage.REFUSE	);
						reply.setContent("O herói não está na cidade!");
					}
					System.out.println("Resposta enviada com sucesso!");
					System.out.println(reply.getContent());
					send(reply);
			
				} else {
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
					reply.setContent("Não entendi sua mensagem");
				}
		
			} else {
				block();
			}
		}
	}
	
	protected void setup() {
		System.out.println("Registrando Heroi no DF");
		Random rollingDice = new Random();
		this.accuracy = rollingDice.nextInt(15);
		this.agility = rollingDice.nextInt(15);
		this.skill = rollingDice.nextInt(15);
		this.trickery = 0;
		this.points = this.accuracy + this.agility + this.skill + this.trickery;	
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Heroi");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
			System.out.println("Criando e adicionando um comportamento!");
			waitingForDuel wFD = new waitingForDuel(this);
			addBehaviour(wFD);
		} catch (FIPAException e) {
			e.printStackTrace();
			doDelete();
		}
	}
	
	protected void takeDown() {
		try {
			System.out.println("Morri!");
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
}
