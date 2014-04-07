package com.dd;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.dd.dialog.BLEConnectionDialog;
import com.dd.util.DriverListener;

/**
 * This is the GUI and Main program manager for the DynamiDice Project.
 * This application is responsible for interfacing with the user, showing 
 * which images are set, and communicating images to the board.
 */
public class DDFrame extends JFrame implements ActionListener, DriverListener {

	final static String MAINPANEL = "Main";
	final static String DICEPANEL = "Dice Program";	
	final static private String newline = "\n";
	private String imagedir = "images/";

	private SimpleDateFormat timestampFormat = new SimpleDateFormat("HH:mm:ss.SSS");
	private DecimalFormat numFormat = new DecimalFormat("0.00");
	private DecimalFormat imunumFormat = new DecimalFormat("0.00000");

	protected BLEConnectionDialog bledialog = new BLEConnectionDialog();
	protected Driver driver;


	public DDFrame() {

		initialize();
		reset();

		imageLoader.execute();
	}

	/**
	 * Reset function to clear the UI when the dice program ends
	 * or when the BLE needs to be reconnected
	 */
	private void reset() {
		if (driver != null) {
			driver.removeDriverListener(this);
			driver.disconnect();
		}

		/* TODO: Reset all dice data
		 * - reset image set
		 * - reset data label and text fields to original state
		 * - reset anything that may have state
		 */

	}

	/**
	 * Initialization of the UI Components, AUTO GENERATED from Swing Designer 
	 */
	private void initialize() {
		setBounds(100, 100, 1000, 645);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("DynamiDice");

		JPanel startupPanel = new JPanel();
		startupPanel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "BLE Connection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));

		JPanel titlePanel = new JPanel();
		titlePanel.setBorder(new TitledBorder(null, "", TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel dicePanel = new JPanel();
		dicePanel.setBorder(new TitledBorder(null, "Dice Program", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		JPanel uploadImagePanel = new JPanel();
		uploadImagePanel.setBorder(new TitledBorder(null, "Add Images", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.LEADING)
								.addGroup(groupLayout.createSequentialGroup()
										.addComponent(dicePanel, GroupLayout.PREFERRED_SIZE, 626, GroupLayout.PREFERRED_SIZE)
										.addGap(18)
										.addComponent(uploadImagePanel, GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
										.addGroup(groupLayout.createSequentialGroup()
												.addGap(155)
												.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, 532, GroupLayout.PREFERRED_SIZE))
												.addComponent(startupPanel, GroupLayout.PREFERRED_SIZE, 325, GroupLayout.PREFERRED_SIZE))
												.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addGap(16)
						.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(27)
						.addComponent(startupPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGap(19)
						.addGroup(groupLayout.createParallelGroup(Alignment.BASELINE)
								.addComponent(dicePanel, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE)
								.addComponent(uploadImagePanel, 0, 0, Short.MAX_VALUE))
								.addContainerGap())
				);
		uploadImageBtn = new JButton("Upload Image");
		uploadImageBtn.addActionListener(this);
		addImageToSetBtn = new JButton("Add to Set");
		addImageToSetBtn.addActionListener(this);
		previewLabel = new JLabel("");
		GroupLayout gl_uploadImagePanel = new GroupLayout(uploadImagePanel);
		gl_uploadImagePanel.setHorizontalGroup(
			gl_uploadImagePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_uploadImagePanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_uploadImagePanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_uploadImagePanel.createParallelGroup(Alignment.TRAILING, false)
							.addComponent(addImageToSetBtn, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
							.addComponent(uploadImageBtn, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
						.addComponent(previewLabel, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(131, Short.MAX_VALUE))
		);
		gl_uploadImagePanel.setVerticalGroup(
			gl_uploadImagePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_uploadImagePanel.createSequentialGroup()
					.addContainerGap()
					.addComponent(uploadImageBtn)
					.addGap(18)
					.addComponent(addImageToSetBtn)
					.addPreferredGap(ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
					.addComponent(previewLabel, GroupLayout.PREFERRED_SIZE, 173, GroupLayout.PREFERRED_SIZE)
					.addGap(33))
		);
		uploadImagePanel.setLayout(gl_uploadImagePanel);
		btnStart = new JButton("START");
		btnStart.addActionListener(this);
		btnStop = new JButton("STOP");
		btnStop.addActionListener(this);
		imageToolBar = new JToolBar();

		imageToolBar.add(Box.createGlue());
		imageToolBar.add(Box.createGlue());

		JLabel lblS = new JLabel("");
		GroupLayout gl_dicePanel = new GroupLayout(dicePanel);
		gl_dicePanel.setHorizontalGroup(
				gl_dicePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dicePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(lblS, GroupLayout.PREFERRED_SIZE, 172, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED, 373, Short.MAX_VALUE)
						.addComponent(btnStart)
						.addGap(32)
						.addComponent(btnStop)
						.addGap(64))
						.addComponent(imageToolBar, GroupLayout.DEFAULT_SIZE, 793, Short.MAX_VALUE)
				);
		gl_dicePanel.setVerticalGroup(
				gl_dicePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dicePanel.createSequentialGroup()
						.addGroup(gl_dicePanel.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_dicePanel.createParallelGroup(Alignment.BASELINE)
										.addComponent(btnStop)
										.addComponent(btnStart))
										.addGroup(gl_dicePanel.createSequentialGroup()
												.addContainerGap()
												.addComponent(lblS, GroupLayout.PREFERRED_SIZE, 154, GroupLayout.PREFERRED_SIZE)))
												.addGap(32)
												.addComponent(imageToolBar, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE)
												.addContainerGap(38, Short.MAX_VALUE))
				);
		dicePanel.setLayout(gl_dicePanel);

		connectBLEBtn = new JButton("Connect BLE");
		connectBLEBtn.addActionListener(this);
		GroupLayout gl_startupPanel = new GroupLayout(startupPanel);
		gl_startupPanel.setHorizontalGroup(
				gl_startupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_startupPanel.createSequentialGroup()
						.addGap(53)
						.addComponent(connectBLEBtn)
						.addContainerGap(750, Short.MAX_VALUE))
				);
		gl_startupPanel.setVerticalGroup(
				gl_startupPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_startupPanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(connectBLEBtn, GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
						.addContainerGap())
				);
		startupPanel.setLayout(gl_startupPanel);

		JLabel titleLabel = new JLabel("");
		titleLabel.setIcon(new ImageIcon("/Users/ryanriebling/Desktop/Screen Shot 2014-04-07 at 1.43.45 PM.png"));
		GroupLayout gl_titlePanel = new GroupLayout(titlePanel);
		gl_titlePanel.setHorizontalGroup(
				gl_titlePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_titlePanel.createSequentialGroup()
						.addGap(68)
						.addComponent(titleLabel)
						.addContainerGap(165, Short.MAX_VALUE))
				);
		gl_titlePanel.setVerticalGroup(
				gl_titlePanel.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_titlePanel.createSequentialGroup()
						.addContainerGap()
						.addComponent(titleLabel, GroupLayout.PREFERRED_SIZE, 72, Short.MAX_VALUE))
				);
		titlePanel.setLayout(gl_titlePanel);
		getContentPane().setLayout(groupLayout);	
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == addImageToSetBtn) {
			btnAddImageToSetActionPerformed(e);
		}
		if (e.getSource() == uploadImageBtn) {
			btnUploadImageActionPerformed(e);
		}
		if (e.getSource() == connectBLEBtn) {
			btnConnectBLEActionPerformed(e);
		}
	}		

	/*************************************************************************
	 * BUTTON HANDLERS 
	 ************************************************************************/

	protected void btnConnectBLEActionPerformed(ActionEvent e) {
		reset();
		if (driver != null) {
			driver.stopTimeSync();
		}

		bledialog.setVisible(true);

		if (bledialog.isConnected()) {
			driver = new Driver(bledialog.getBgapi(), bledialog.getConnection());
			driver.addDriverListener(this);
		}
	}

	protected void btnUploadImageActionPerformed(ActionEvent e) {
		fc = new JFileChooser();
		filter = new FileNameExtensionFilter(
				"JPG & GIF Images", "jpg", "gif", "png");
		fc.setFileFilter(filter);
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
			previewLabel.setSize(imagePreview.getIconWidth(), imagePreview.getIconHeight());
			previewLabel.setIcon(imagePreview);		
		} 
	}

	protected void btnAddImageToSetActionPerformed(ActionEvent e) {
		ThumbnailAction thumbAction;
		if(imagePreview != null){
			ImageIcon thumbnailIcon = new ImageIcon(getScaledImage(imagePreview.getImage(), 32, 32));
			thumbAction = new ThumbnailAction(imagePreview, thumbnailIcon, "Image");
		} else {
			thumbAction = new ThumbnailAction(placeholderIcon, placeholderIcon, "Missing Image");
		}
		JButton thumbButton = new JButton(thumbAction);
		imageToolBar.add(thumbButton, imageToolBar.getComponentCount() - 1);
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
	private JLabel imageLabel;

	private JFileChooser fc;
	private FileNameExtensionFilter filter; 
	private JLabel selectedImage;
	private ImageIcon imagePreview;
	private JToolBar imageToolBar; 

	private ImageIcon placeholderIcon;

	/** List of temporary images to load */
	private String[] imageFileNames = { "angry.png", "cool.png",
			"evil.png", "ic_action_computer.png", "smiley_face.png"};

	private String[] imageCaptions = { "angry", "cool", "evil", "grin", "happy"};

	private JButton btnStart;
	private JButton btnStop;
	private JButton connectBLEBtn;
	private JButton uploadImageBtn;
	private JButton addImageToSetBtn;
	private JLabel previewLabel;


	@Override
	public void testPattern(byte[] data, int timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeSync(int seq, int timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void manufacturer(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void model_number(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void serial_number(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void hw_revision(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fw_revision(String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void alertLevel(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imuMode(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imuInterrupt(int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void imu(int ax, int ay, int az, int gx, int gy, int gz,
			int timestamp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void quaternion(int w, int x, int y, int z, int timestamp) {
		// TODO Auto-generated method stub

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
				imageToolBar.add(thumbButton, imageToolBar.getComponentCount() - 1);
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


}
