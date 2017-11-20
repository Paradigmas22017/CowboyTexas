package main;
import jade.core.*;
import jade.core.behaviours.*;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import main.MyReceiver;
		
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
	
	public AID getThiefAID() {
		return this.duelThief;
	}
	
	class Shoot extends OneShotBehaviour {

		private static final long serialVersionUID = -6525995407915555924L;

		public Shoot(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Herói Atirando..");
			@SuppressWarnings("deprecation")
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(ACLMessage.INFORM);
			msg.setContent(Integer.toString(getPoints()));
			msg.addReceiver(getThiefAID());
			myAgent.send(msg);
			System.out.println("Poder do Heroi: " + msg.getContent());
						
		}
	}
	
	class AcceptingDuel extends OneShotBehaviour {

		private static final long serialVersionUID = -6525995407915555924L;

		public AcceptingDuel(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Herói diz: Aceito seu desafio, seu maltrapilho!");
			@SuppressWarnings("deprecation")
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			msg.setContent("Aceito seu desafio, seu maltrapilho!");
			msg.addReceiver(getThiefAID());
			myAgent.send(msg);				
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
			addBehaviour(new MyReceiver(this, -1, 
					MessageTemplate.MatchPerformative(ACLMessage.PROPOSE))
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
			        			Random rollingDice = new Random();
								int acceptingChance = rollingDice.nextInt(100);
								if (acceptingChance >= 0) {
									getThief(msg);
									AcceptingDuel aD = new AcceptingDuel(myAgent);
									Shoot s = new Shoot(myAgent);
									addBehaviour(aD);
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
									        			if(receivedShot > getPoints()) {
									        				takeDown();									        			
									        			} else {
									        				// Herói vence
									        			}
									        		} 
									        	}
											});
								}
			        		} 
			        	}
					});
		} catch(FIPAException e) {
			e.printStackTrace();
		}
	
	}
	
	
	protected void takeDown() {
		try {
			System.out.println("O heroi cai morto no chão!");
			DFService.deregister(this);
		} catch (FIPAException e) {
			e.printStackTrace();
		}
	}
	
}
