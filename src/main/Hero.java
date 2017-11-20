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
	
	public int getPoints() {
		return this.points;
	}
	
/*	
	class waitingForDuel extends ReceiverBehaviour {

		private static final long serialVersionUID = 3639679338986836773L;
		public waitingForDuel(Agent a, int time,) {
			super(a);
		}
		
		public void handle( ACLMessage msg) {
			System.out.println("Esperando por duelo");
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
							receivingShot rS = new receivingShot(myAgent);
							shoot s = new shoot(myAgent);
							addBehaviour(s);
							addBehaviour(rS);
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
	
	class receivingShot extends CyclicBehaviour {

		private static final long serialVersionUID = -4083197750569324234L;
		public receivingShot(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Recebendo Tiro..");
			ACLMessage msg = myAgent.receive();
			if(msg != null) {
				ACLMessage reply = msg.createReply();
				if(msg.getPerformative() == ACLMessage.INFORM) {
					String content = msg.getContent();
					System.out.println("Content:" + content);
					int receivedShot = Integer.parseInt(content); 
					System.out.println("receivedShot:" + receivedShot);
					System.out.println("Recebi tiro com força:" + receivedShot);
					if(receivedShot > getPoints()) {
						takeDown();
					} else {
						System.out.println("O Heroi Venceu!");
					}
				} else {
					reply.setPerformative(ACLMessage.NOT_UNDERSTOOD);
				}
			} else {
				block();
			}
		}
	}
*/	
	class Shoot extends OneShotBehaviour {

		private static final long serialVersionUID = -6525995407915555924L;

		public Shoot(Agent a) {
			super(a);
		}
		
		public void action() {
			System.out.println("Atirando..");
			@SuppressWarnings("deprecation")
			ACLMessage msg = new ACLMessage();
			msg.setPerformative(ACLMessage.INFORM);
			msg.setContent(Integer.toString(getPoints()));
			myAgent.send(msg);
			System.out.println("Enviando Pontos: " + msg.getContent());
						
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
								if (acceptingChance >= 30) {
									System.out.println("Aceito seu desafio, seu maltrapilho!");
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
									        			String content = msg.getContent();
									        			int receivedShot = Integer.parseInt(content);
									        			if(receivedShot > getPoints()) {
									        				takeDown();									        			
									        			} else {
									        				System.out.println("Heroi vence!");
									        			}
									        		} 
									        	}
											});
								}
			        		} 
			        	}
					});
		} catch(Exception e) {
			
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
