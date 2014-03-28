package com.dd;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

/**
 * This is the GUI and Main program manager for the DynamiDice Project.
 * This application is responsible for interfacing with the user, showing 
 * which images are set, and communicating images to the board.
 */
public class DDApplication {

	private JFrame frame;
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DDApplication window = new DDApplication();
					//window.frame.setVisible(true);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public DDApplication() {
		initialize();		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridLayout(0, 1, 0, 0));

		JLabel lblDynamidice = new JLabel("DynamiDice");
		lblDynamidice.setHorizontalAlignment(SwingConstants.CENTER);
		lblDynamidice.setFont(new Font("Lucida Grande", Font.BOLD, 24));
		lblDynamidice.setForeground(Color.DARK_GRAY);

		frame.getContentPane().add(lblDynamidice);

		JButton btnDiceProgram = new JButton("Dice Program");
		btnDiceProgram.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		frame.getContentPane().add(btnDiceProgram);

		JButton btnGameProgram = new JButton("Game Program");
		btnGameProgram.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				
			}
		});
		frame.getContentPane().add(btnGameProgram);
	}
	
}
