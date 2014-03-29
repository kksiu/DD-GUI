package com.dd;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * This is the GUI and Main program manager for the DynamiDice Project.
 * This application is responsible for interfacing with the user, showing 
 * which images are set, and communicating images to the board.
 */
public class DDApplication extends JFrame implements ItemListener, ActionListener {

	private JPanel cards;
	private JToolBar buttonBar = new JToolBar();
	private JLabel imageLabel = new JLabel();
	private JButton openButton;
	private JButton saveButton;
	private JButton btnUpload;
	private JButton btnStartButton;
	private JButton btnEndButton;
	private JTextArea log;
    private JFileChooser fc;
	final static String MAINPANEL = "Main";
	final static String DICEPANEL = "Dice Program";	
	final static private String newline = "\n";
	private String imagedir = "images/";

	private MissingIcon placeholderIcon = new MissingIcon();

	/**
	 * List of temporary images to load
	 */
	private String[] imageFileNames = { "angry.png", "cool.png",
			"evil.png", "ic_action_computer.png", "smiley_face.png"};

	private String[] imageCaptions = { "angry", "cool", "evil", "grin", "happy"};

	public static void main(String[] args) {		
		/* Use an appropriate Look and Feel */
		try {
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
		} catch (UnsupportedLookAndFeelException ex) {
			ex.printStackTrace();
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InstantiationException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		/* Turn off metal's use of bold fonts */
		//UIManager.put("swing.boldMetal", Boolean.FALSE);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DDApplication app = new DDApplication();
					app.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		});
	}

	public DDApplication() {
		//Create and set up the window.
		JFrame frame = new JFrame("DDApplication");
		frame.setTitle("DynamiDice");
		frame.setBounds(100, 100, 700, 700);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Create and set up the content pane.
		addContentToPane(frame.getContentPane());

		// this centers the frame on the screen
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);		
		imageLoader.execute();
	}

	/**
	 * Creates the frame and initializes the tab panels.
	 * @param pane - the container window that gets filled
	 */
	public void addContentToPane(Container pane) {
		JTabbedPane tabbedPane = new JTabbedPane();

		//Create the "cards" (views)
		JPanel mainCard = new JPanel();
		JPanel diceCard = new JPanel();		

		mainCard = initializeMainCard();
		diceCard = initializeDiceCard();

		tabbedPane.addTab(MAINPANEL, mainCard);
		tabbedPane.addTab(DICEPANEL, diceCard);

		pane.add(tabbedPane, BorderLayout.CENTER);
	}

	/**
	 * Initializes and adds content to the main card tab.
	 * @return JPanel - the completed main card 
	 */
	private JPanel initializeMainCard() {
		JPanel mainCard = new JPanel();
		mainCard.setLayout(new GridLayout(0, 1, 0, 0));
		JLabel label1 = new JLabel("DynamiDice");
		label1.setHorizontalAlignment(SwingConstants.CENTER);
		label1.setFont(new Font("Dialog", Font.BOLD, 43));
		label1.setForeground(Color.GRAY);
		mainCard.add(label1);

		JLabel label2 = new JLabel("ECE 453 Project");
		label2.setHorizontalAlignment(SwingConstants.CENTER);
		label2.setFont(new Font("Dialog", Font.BOLD, 28));
		label2.setForeground(Color.GRAY);
		mainCard.add(label2);

		JLabel label3 = new JLabel("");
		mainCard.add(label3);
		return mainCard;		
	}

	/**
	 * Initializes and adds UI components to the Dice Card
	 * @return JPanel - the completed card 
	 */
	private JPanel initializeDiceCard() {
		JPanel diceCard = new JPanel();

		btnStartButton = new JButton("START");
		btnStartButton.setBounds(244, 113, 74, 25);
		btnStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start Button Pressed");
			}
		});
		diceCard.setLayout(null);
		JLabel lblDiceProgram = new JLabel("Dice Program");
		lblDiceProgram.setBounds(313, 10, 83, 15);	

		btnEndButton = new JButton("END");
		btnEndButton.setBounds(415, 113, 74, 25);
		btnEndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				System.out.println("End Button Pressed");
			}
		});	
		btnUpload = new JButton("Upload");
		btnUpload.setBounds(568, 5, 78, 25);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Upload Button Pressed");
				startImageFileUpload(e);
			}
		});

		diceCard.add(btnUpload);
		diceCard.add(btnEndButton);		
		diceCard.add(lblDiceProgram);
		diceCard.add(btnStartButton);

		buttonBar.setLocation(12, 581);
		buttonBar.setSize(671, 45);	
		buttonBar.add(Box.createGlue());
		buttonBar.add(Box.createGlue());
		diceCard.add(buttonBar);


		// A label for displaying the pictures
		imageLabel.setBounds(173, 238, 375, 309);		
		imageLabel.setVerticalTextPosition(JLabel.BOTTOM);
		imageLabel.setHorizontalTextPosition(JLabel.CENTER);
		imageLabel.setHorizontalAlignment(JLabel.CENTER);
		imageLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));	
		diceCard.add(imageLabel);

		return diceCard;
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, (String)e.getItem());		
	}
	
	private void startImageFileUpload(ActionEvent e) {
		fc = new JFileChooser();
		log = new JTextArea(5,20);
		log.setMargin(new Insets(5,5,5,5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		openButton = new JButton("Open a File");
		saveButton = new JButton("Add to Set");
		openButton.addActionListener(this);
		saveButton.addActionListener(this);
				     
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(saveButton);
		buttonPanel.add(openButton);	
		//TODO: Add UI components for user to see the image they want in the dialog, 
		//use saveButton to add to program and close dialog
		
		JDialog jdialog = new JDialog();
		jdialog.add(buttonPanel, BorderLayout.PAGE_START);
        jdialog.add(logScrollPane, BorderLayout.CENTER);
      
        jdialog.setLocationRelativeTo(btnUpload);
        jdialog.setSize(500, 500);
        
        jdialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//Handle open button action.
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(DDApplication.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//TODO: Manipulate file as desired, show in dialog panel
				log.append("Opening: " + file.getName() + "." + newline);
			} else {
				log.append("Open command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());

			//Handle save button action.
		} else if (e.getSource() == saveButton) {
			int returnVal = fc.showSaveDialog(DDApplication.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				//TODO: Add image file back to the set of images 
				log.append("Saving: " + file.getName() + "." + newline);
			} else {
				log.append("Save command cancelled by user." + newline);
			}
			log.setCaretPosition(log.getDocument().getLength());
		}
	}	

	
	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
			String description) {

		String dirPath = DDApplication.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		dirPath = dirPath.substring(0, dirPath.length() - 4); 
		File file = new File(dirPath + path);
		try {
			BufferedImage image = ImageIO.read(file);
			return new ImageIcon(image);
		} catch (IOException e) {		
			System.err.println("Couldn't find file: " +	dirPath + path);
		}
		return null;
	}


	/**
	 * SwingWorker class that loads the images a background thread and calls publish
	 * when a new one is ready to be displayed.
	 */
	private SwingWorker<Void, ThumbnailAction> imageLoader = new SwingWorker<Void, ThumbnailAction>() {

		/**
		 * Creates full size and thumbnail versions of the target image files.
		 */
		@Override
		protected Void doInBackground() throws Exception {
			for (int i = 0; i < imageCaptions.length; i++) {
				ImageIcon icon;
				icon = createImageIcon(imagedir + imageFileNames[i], imageCaptions[i]);

				ThumbnailAction thumbAction;
				if(icon != null){
					ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 32, 32));
					thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageCaptions[i]);

				}else{
					// the image failed to load so load a placeholder instead
					thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, imageCaptions[i]);
				}
				publish(thumbAction);
			}
			return null;
		}

		/**
		 * Process all loaded images.
		 */
		@Override
		protected void process(List<ThumbnailAction> chunks) {
			for (ThumbnailAction thumbAction : chunks) {
				JButton thumbButton = new JButton(thumbAction);
				// add the new button BEFORE the last glue this centers the buttons in the toolbar
				buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
			}
		}
	};

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * @param srcImg - source image to scale
	 * @param w - desired width
	 * @param h - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}

	/**
	 * Action class that shows the image specified in it's constructor.
	 */
	private class ThumbnailAction extends AbstractAction {

		private Icon displayPhoto;

		/**
		 * @param Icon - The full size photo to show in the button.
		 * @param Icon - The thumbnail to show in the button.
		 * @param String - The description of the icon.
		 */
		public ThumbnailAction(Icon photo, Icon thumb, String desc){
			displayPhoto = photo;

			// The short description becomes the tooltip of a button.
			putValue(SHORT_DESCRIPTION, desc);

			// The LARGE_ICON_KEY is the key for setting the
			// icon when an Action is applied to a button.
			putValue(LARGE_ICON_KEY, thumb);
		}

		/**
		 * Shows the full image in the main area and sets the application title.
		 */
		public void actionPerformed(ActionEvent e) {
			imageLabel.setIcon(displayPhoto);			
		}
	}
}
