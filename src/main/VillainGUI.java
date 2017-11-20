package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import main.Villain.ToChallenge;

class VillainGui extends JFrame {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Villain myAgent;
	
	VillainGui(Villain a) {
		super(a.getLocalName());
		
		myAgent = a;
		
		JPanel p = new JPanel();
		p.setLayout(new GridLayout(2, 2));
		getContentPane().add(p, BorderLayout.CENTER);
		
		JButton addButton = new JButton("Duel");
		addButton.addActionListener( new ActionListener() {
			public void actionPerformed(ActionEvent ev) {
				try {
					ToChallenge tc = new ToChallenge(myAgent);
					myAgent.addBehaviour(tc);
				}
				catch (Exception e) {
					JOptionPane.showMessageDialog(VillainGui.this, "Invalid values. "+e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); 
				}
			}
		} );
		p = new JPanel();
		p.add(addButton);
		getContentPane().add(p, BorderLayout.SOUTH);
		
		// Make the agent terminate when the user closes 
		// the GUI using the button on the upper right corner	
		addWindowListener(new	WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}
		} );
		
		setResizable(false);
	}
	
	public void showGui() {
		pack();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int centerX = (int)screenSize.getWidth() / 2;
		int centerY = (int)screenSize.getHeight() / 2;
		setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);
		super.setVisible(true);
	}	
}
