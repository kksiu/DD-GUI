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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
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
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dd.dialog.BLEConnectionDialog;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JProgressBar;
import javax.swing.border.TitledBorder;
import javax.swing.border.LineBorder;

/**
 * This is the GUI and Main program manager for the DynamiDice Project.
 * This application is responsible for interfacing with the user, showing 
 * which images are set, and communicating images to the board.
 */
public class DDFrame extends JFrame implements ItemListener, ActionListener {


	final static String MAINPANEL = "Main";
	final static String DICEPANEL = "Dice Program";	
	final static private String newline = "\n";
	private String imagedir = "images/";

	private SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private DecimalFormat numFormat = new DecimalFormat("0.00");
	private DecimalFormat imunumFormat = new DecimalFormat("0.00000");

	protected BLEConnectionDialog bledialog = new BLEConnectionDialog();

	public DDFrame() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setTitle("DynamiDice");
		
		JPanel startupPanel = new JPanel();
		startupPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "BLE Connection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JPanel dicePanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
						.addComponent(startupPanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE)
						.addComponent(dicePanel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 401, GroupLayout.PREFERRED_SIZE)
						.addComponent(titlePanel, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 803, Short.MAX_VALUE))
					.addContainerGap())
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addGap(16)
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(27)
					.addComponent(startupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
					.addGap(161)
					.addComponent(dicePanel, GroupLayout.PREFERRED_SIZE, 177, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(50, Short.MAX_VALUE))
		);
		btnStart = new JButton("START");
		btnStart.addActionListener(this);
		btnStop = new JButton("STOP");
		btnStop.addActionListener(this);
		GroupLayout gl_dicePanel = new GroupLayout(dicePanel);
		gl_dicePanel.setHorizontalGroup(
			gl_dicePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dicePanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(btnStart)
					.addGap(34)
					.addComponent(btnStop)
					.addContainerGap(175, Short.MAX_VALUE))
		);
		gl_dicePanel.setVerticalGroup(
			gl_dicePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dicePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_dicePanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnStart)
						.addComponent(btnStop))
					.addContainerGap(140, Short.MAX_VALUE))
		);
		dicePanel.setLayout(gl_dicePanel);
		
		JButton btnConnectBLE = new JButton("Connect BLE");
		btnConnectBLE.addActionListener(new BtnConnectBLEActionListener());
		
		
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setMinimum(-100);
		progressBar.setStringPainted(true);
		progressBar.setForeground(UIManager.getColor("Button.focus"));
		GroupLayout gl_startupPanel = new GroupLayout(startupPanel);
		gl_startupPanel.setHorizontalGroup(
			gl_startupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_startupPanel.createSequentialGroup()
					.addGap(53)
					.addComponent(btnConnectBLE)
					.addGap(82)
					.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, 382, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(175, Short.MAX_VALUE))
		);
		gl_startupPanel.setVerticalGroup(
			gl_startupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_startupPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_startupPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConnectBLE, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 29, Short.MAX_VALUE))
					.addContainerGap())
		);
		startupPanel.setLayout(gl_startupPanel);
		
		JLabel titleLabel = new JLabel("");
		titleLabel.setIcon(new ImageIcon("/Users/ryanriebling/Desktop/Screen Shot 2014-04-07 at 1.43.45 PM.png"));
		GroupLayout gl_titlePanel = new GroupLayout(titlePanel);
		gl_titlePanel.setHorizontalGroup(
			gl_titlePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_titlePanel.createSequentialGroup()
					.addGap(211)
					.addComponent(titleLabel)
					.addContainerGap(206, Short.MAX_VALUE))
		);
		gl_titlePanel.setVerticalGroup(
			gl_titlePanel.createParallelGroup(Alignment.LEADING)
				.addComponent(titleLabel, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 72, Short.MAX_VALUE)
		);
		titlePanel.setLayout(gl_titlePanel);
		getContentPane().setLayout(groupLayout);		
		initialize();
		reset();

		//Create and set up the content pane.
		addContentToPane(frame.getContentPane());

		// this centers the frame on the screen
		frame.setLocationRelativeTo(null);

		frame.setVisible(true);		
		imageLoader.execute();
	}



	private void initialize() {
		// TODO Initial all the UI components and set up the Driver

	}



	private void reset() {
		// TODO Auto-generated method stub

	}


	/**
	 * Creates the frame and initializes the tab panels.
	 * @param pane - the container window that gets filled
	 */
	public void addContentToPane(Container pane) {

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
		btnStartButton.setBounds(402, 63, 74, 25);
		btnStartButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Start Button Pressed");
			}
		});
		diceCard.setLayout(null);
		JLabel lblDiceProgram = new JLabel("Dice Program");
		lblDiceProgram.setFont(new Font("Dialog", Font.BOLD, 33));
		lblDiceProgram.setBounds(383, 10, 240, 39);	

		btnEndButton = new JButton("END");
		btnEndButton.setBounds(512, 63, 74, 25);
		btnEndButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {			
				System.out.println("End Button Pressed");
			}
		});	
		btnUpload = new JButton("Upload");
		btnUpload.setBounds(905, 10, 78, 25);
		btnUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("Upload Button Pressed");
				openImageUploadWindow(e);
			}
		});

		sendDataButton = new JButton("Send");
		sendDataButton.setBounds(700, 10, 78, 25);
		sendDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("sendDataButton Pressed");				
			}
		});

		buttonBar = new JToolBar();
		imageLabel = new JLabel();

		diceCard.add(btnUpload);
		diceCard.add(btnEndButton);		
		diceCard.add(lblDiceProgram);
		diceCard.add(btnStartButton);
		diceCard.add(sendDataButton);

		buttonBar.setLocation(12, 667);
		buttonBar.setSize(971, 82);	
		buttonBar.add(Box.createGlue());
		buttonBar.add(Box.createGlue());
		diceCard.add(buttonBar);

		// A label for displaying the pictures
		imageLabel.setBounds(313, 323, 375, 309);
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

	private void openImageUploadWindow(ActionEvent e) {
		fc = new JFileChooser();
		filter = new FileNameExtensionFilter(
				"JPG & GIF Images", "jpg", "gif", "png");
		fc.setFileFilter(filter);
		log = new JTextArea(5,20);
		log.setMargin(new Insets(5,5,5,5));
		log.setEditable(false);
		JScrollPane logScrollPane = new JScrollPane(log);

		openButton = new JButton("Open a File");
		addImageButton = new JButton("Add to Set");
		openButton.addActionListener(this);
		addImageButton.addActionListener(this);

		buttonPanel = new JPanel();
		buttonPanel.add(addImageButton);
		buttonPanel.add(openButton);	

		selectedImage = new JLabel();		
		selectedImage.setVerticalTextPosition(JLabel.BOTTOM);
		selectedImage.setHorizontalTextPosition(JLabel.CENTER);
		selectedImage.setHorizontalAlignment(JLabel.CENTER);

		JDialog jdialog = new JDialog();
		jdialog.getContentPane().add(buttonPanel, BorderLayout.PAGE_START);
		jdialog.getContentPane().add(logScrollPane, BorderLayout.CENTER);
		jdialog.getContentPane().add(log);
		jdialog.getContentPane().add(selectedImage);

		jdialog.setLocationRelativeTo(btnUpload);
		jdialog.setSize(500, 500);

		jdialog.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		//Handle open button action.
		if (e.getSource() == openButton) {
			int returnVal = fc.showOpenDialog(DDFrame.this);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				Image image = null;

				try {
					image = ImageIO.read(file);					
				} catch (IOException ex) {
					System.out.println("Error reading image: " + ex.getMessage());
				}				

				imagePreview = new ImageIcon(getScaledImage(image, 64, 64));
				selectedImage.setSize(imagePreview.getIconWidth(), imagePreview.getIconHeight());
				selectedImage.setIcon(imagePreview);
				buttonPanel.add(selectedImage);
			} 

			//Handle addImage button action.
		} else if (e.getSource() == addImageButton) {
			ThumbnailAction thumbAction;
			if(imagePreview != null){
				ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(imagePreview.getImage(), 32, 32));
				thumbAction = new ThumbnailAction(imagePreview, thumbnailIcon, "Image");
			} else {
				thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, "Missing Image");
			}
			JButton thumbButton = new JButton(thumbAction);
			buttonBar.add(thumbButton, buttonBar.getComponentCount() - 1);
		}
	}		

	/** Returns an ImageIcon, or null if the path was invalid. */
	protected ImageIcon createImageIcon(String path,
			String description) {

		String dirPath = DDFrame.class.getProtectionDomain()
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
					ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(icon.getImage(), 64, 64));
					thumbAction = new ThumbnailAction(icon, thumbnailIcon, imageCaptions[i]);
				} else {
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
	 * Action class that shows the image specified in it's constructor.
	 */
	private class ThumbnailAction extends AbstractAction {

		private ImageIcon displayPhoto;

		/**
		 * @param Icon - The full size photo to show in the button.
		 * @param Icon - The thumbnail to show in the button.
		 * @param String - The description of the icon.
		 */
		public ThumbnailAction(ImageIcon photo, Icon thumb, String desc){
			displayPhoto = new ImageIcon(getScaledImage(photo.getImage(), 64, 64));

			// The short description becomes the tooltip of a button.
			putValue(SHORT_DESCRIPTION, desc);

			// The LARGE_ICON_KEY is the key for setting the
			// icon when an Action is applied to a button.
			putValue(LARGE_ICON_KEY, thumb);
		}

		/** Shows the full image in the main area and sets the application title. */
		public void actionPerformed(ActionEvent e) {
			imageLabel.setSize(displayPhoto.getIconWidth(), displayPhoto.getIconHeight());
			imageLabel.setIcon(displayPhoto);			
		}
	}
	
	private class BtnConnectBLEActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
		}
	}

	/**
	 * Resizes an image using a Graphics2D object backed by a BufferedImage.
	 * @param srcImg - source image to scale
	 * @param w - desired width
	 * @param h - desired height
	 * @return - the new resized image
	 */
	private Image getScaledImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();
		return resizedImg;
	}


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
					DDFrame app = new DDFrame();
					app.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}			
		});
	}

	/** UI Components **/
	private JFrame frame; 
	private JTabbedPane tabbedPane; 
	private JPanel cards;
	private JPanel mainCard;
	private JPanel diceCard;
	private JPanel buttonPanel;
	private JToolBar buttonBar;
	private JLabel imageLabel;
	private JButton openButton;
	private JButton addImageButton;
	private JButton btnUpload;
	private JButton btnStartButton;
	private JButton btnEndButton;
	private JTextArea log;
	private JFileChooser fc;
	private FileNameExtensionFilter filter; 
	private JLabel selectedImage;
	private ImageIcon imagePreview;

	private JButton sendDataButton;
	private ImageIcon placeholderIcon;

	/** List of temporary images to load */
	private String[] imageFileNames = { "angry.png", "cool.png",
			"evil.png", "ic_action_computer.png", "smiley_face.png"};

	private String[] imageCaptions = { "angry", "cool", "evil", "grin", "happy"};
	private JButton btnStart;
	private JButton btnStop;
	
	
}
