package main;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Villain extends Agent{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7779051071406903573L;
	
	private int agility;
	private int skill;
	private int accuracy;
	private int trickery;
	private int points;
	private AID duelThief;
	
	public int getPoints() {
		return this.points;
	}
	
	public void verifyShootMsg(ACLMessage msg) {
		if(msg.getContent() == null) {
			this.doWait();
		}
	} 
	
	public void getThief(ACLMessage msg) {
		if(msg.getSender() == null) {
			this.doWait();
		} else {
			this.duelThief = msg.getSender();
		}
	}
	
	class Shoot extends OneShotBehaviour {

		private static final long serialVersionUID = -6525112407915555924L;

		public Shoot(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Atirando..");
			@SuppressWarnings("deprecation")
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(ACLMessage.INFORM);
			msg.setContent(Integer.toString(getPoints()));
			/* myAgent.duelThief*/
			myAgent.send(msg);
			System.out.println("Enviando Pontos: " + msg.getContent());
						
		}
	}
	
	class ToChallenge extends OneShotBehaviour {

		private static final long serialVersionUID = -6525112407915555924L;

		public ToChallenge(Agent a) {
			super(a);
		}
		
		public void action() {
			String msgDuel = "Você deseja duelar comigo??";
			@SuppressWarnings("deprecation")
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(ACLMessage.PROPOSE);
			msg.setContent(msgDuel);
			/* myAgent.duelThief*/
			myAgent.send(msg);
			System.out.println(msg.getContent());
		}
	}

	protected void setup() {
		System.out.println("Registrando Heroi no DF");
		Random rollingDice = new Random();
		this.accuracy = rollingDice.nextInt(10);
		this.agility = rollingDice.nextInt(10);
		this.skill = rollingDice.nextInt(10);
		this.trickery = rollingDice.nextInt(10);;
		this.points = this.accuracy + this.agility + this.skill + this.trickery;	
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Villain");
		dfd.addServices(sd);
		
		try {
			DFService.register(this, dfd);
			System.out.println("Criando e adicionando um comportamento!");
			
			ToChallenge tc = new ToChallenge(this);
			addBehaviour(tc);

			addBehaviour(new MyReceiver(this, -1, 
					MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL))
					{
						/**
						 * 
						 */
						private static final long serialVersionUID = -3430588291231242952L;
						
						public void handle( ACLMessage msg ) 
			        	{
							if(msg != null) {
								Shoot s = new Shoot(myAgent);
								addBehaviour(s);
								addBehaviour(new MyReceiver(myAgent, -1, 
										MessageTemplate.MatchPerformative(ACLMessage.INFORM))
										{
								        	/**
											 * 
											 */
											private static final long serialVersionUID = -3430588296512442952L;
				
											public void handle( ACLMessage msg ) 
								        	{  
								        		if (msg == null) 
								        			System.out.println("Timeout");
								        		else {
								        			verifyShootMsg(msg);
								        			String content = msg.getContent();
								        			int receivedShot = Integer.parseInt(content);
								        			System.out.println(getPoints());
								        			if(receivedShot > getPoints()) {
								        				takeDown();									        			
								        			} else {
								        				System.out.println("Vilão vence!");
								        			}
								        		}
								        	}
										});
								}
			        	}
					});

			} catch (Exception e) {
			
			}
	
	}
	
	
	protected void takeDown() {
		try {
			System.out.println("O heroi venceu o vilão!");
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}

	
}
