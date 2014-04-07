package com.dd.dialog;

import gnu.io.SerialPort;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.SwingWorker;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.thingml.bglib.BDAddr;
import org.thingml.bglib.BGAPI;
import org.thingml.bglib.BGAPIListener;
import org.thingml.bglib.BGAPIPacketLogger;
import org.thingml.bglib.gui.BLEAttribute;
import org.thingml.bglib.gui.BLED112;
import org.thingml.bglib.gui.BLEDevice;
import org.thingml.bglib.gui.BLEDeviceList;
import org.thingml.bglib.gui.BLEService;

public class BLEConnectionDialog extends JDialog implements BGAPIListener, ActionListener, PropertyChangeListener {
	public BLEConnectionDialog() {
		initialize();
	}
	private void initialize() {
		panel = new JPanel();
		panel.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Connection", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Progress", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GroupLayout groupLayout = new GroupLayout(getContentPane());
		groupLayout.setHorizontalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
						.addContainerGap()
						.addGroup(groupLayout.createParallelGroup(Alignment.TRAILING)
								.addComponent(panel_1, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)
								.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE))
								.addContainerGap())
				);
		groupLayout.setVerticalGroup(
				groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
						.addComponent(panel, GroupLayout.PREFERRED_SIZE, 65, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.UNRELATED)
						.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 195, GroupLayout.PREFERRED_SIZE)
						.addContainerGap(97, Short.MAX_VALUE))
				);
		progressBar = new JProgressBar();
		progressBar.setForeground(SystemColor.controlHighlight);
		progressBar.setStringPainted(true);
		scrollPaneLog = new JScrollPane();
		connLogLabel = new JLabel("Connection Log:");
		connLogLabel.setForeground(SystemColor.controlHighlight);
		bledConnectedBtn = new JButton("BLED CONNECTED!");
		bledConnectedBtn.setEnabled(false);
		bledConnectedBtn.setForeground(SystemColor.controlHighlight);
		GroupLayout gl_panel_1 = new GroupLayout(panel_1);
		gl_panel_1.setHorizontalGroup(
				gl_panel_1.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addContainerGap()
										.addComponent(progressBar, GroupLayout.DEFAULT_SIZE, 545, Short.MAX_VALUE))
										.addGroup(gl_panel_1.createSequentialGroup()
												.addGap(23)
												.addComponent(connLogLabel)
												.addPreferredGap(ComponentPlacement.UNRELATED)
												.addComponent(scrollPaneLog, GroupLayout.PREFERRED_SIZE, 198, GroupLayout.PREFERRED_SIZE)
												.addGap(18)
												.addComponent(bledConnectedBtn, GroupLayout.DEFAULT_SIZE, 197, Short.MAX_VALUE)))
												.addContainerGap())
				);
		gl_panel_1.setVerticalGroup(
				gl_panel_1.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_1.createSequentialGroup()
						.addContainerGap()
						.addComponent(progressBar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
								.addGroup(gl_panel_1.createSequentialGroup()
										.addGap(23)
										.addGroup(gl_panel_1.createParallelGroup(Alignment.LEADING)
												.addComponent(connLogLabel)
												.addComponent(scrollPaneLog, GroupLayout.DEFAULT_SIZE, 112, Short.MAX_VALUE)))
												.addGroup(gl_panel_1.createSequentialGroup()
														.addGap(51)
														.addComponent(bledConnectedBtn)))
														.addContainerGap())
				);
		taskLogOutput = new JTextArea();
		taskLogOutput.setRows(20);
		taskLogOutput.setColumns(5);
		scrollPaneLog.setViewportView(taskLogOutput);
		panel_1.setLayout(gl_panel_1);
		serialPortLabel = new JLabel("Serial Port:");
		serialPortLabel.setForeground(SystemColor.controlHighlight);
		portTextField = new JTextField();
		portTextField.setColumns(10);
		browsePortsBtn = new JButton("Find BLED");
		browsePortsBtn.addActionListener(this);
		connectBLEDBtn = new JButton("Connect");
		connectBLEDBtn.addActionListener(this);
		connectBLEDBtn.setForeground(SystemColor.controlHighlight);
		disconnectBLEDBtn = new JButton("Disconnect");
		disconnectBLEDBtn.setEnabled(false);
		disconnectBLEDBtn.addActionListener(this);
		disconnectBLEDBtn.setForeground(Color.RED);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addContainerGap()
						.addComponent(serialPortLabel, GroupLayout.PREFERRED_SIZE, 75, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, 128, GroupLayout.PREFERRED_SIZE)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(browsePortsBtn)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(connectBLEDBtn)
						.addPreferredGap(ComponentPlacement.RELATED)
						.addComponent(disconnectBLEDBtn)
						.addContainerGap(17, Short.MAX_VALUE))
				);
		gl_panel.setVerticalGroup(
				gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
						.addGroup(gl_panel.createParallelGroup(Alignment.BASELINE)
								.addComponent(serialPortLabel)
								.addComponent(portTextField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
								.addComponent(browsePortsBtn)
								.addComponent(connectBLEDBtn)
								.addComponent(disconnectBLEDBtn))
								.addContainerGap(7, Short.MAX_VALUE))
				);
		panel.setLayout(gl_panel);
		getContentPane().setLayout(groupLayout);
	}

	protected BGAPI bgapi;
	protected SerialPort port;
	protected BLEDeviceList deviceList = new BLEDeviceList();
	private BGAPIPacketLogger LOG = new BGAPIPacketLogger();

	public BGAPI getBgapi() {
		return bgapi;
	}

	public int getConnection() {
		return connection;
	}

	public BLEDevice getBledevice() {
		return bledevice;
	}

	public boolean isConnected() {
		return (bgapi != null && connection > -1);
	}

	public void disconnect() {
		if (isConnected()) {
			do_disconnect();
		}

		if (bgapi != null) {
			bgapi.removeListener(this);
			bgapi.getLowLevelDriver().removeListener(LOG);
			System.out.println("BLE: Rreset BLED112 Dongle");
			bgapi.send_system_reset(0);
			bgapi.disconnect();
		}
		if (port != null) {
			port.close();
		}
		bgapi = null;
		port = null;
	}

	public void do_disconnect() {
		bledevice = null;
		if (connection >= 0) {
			bgapi.send_connection_disconnect(connection);
		}
		connection = -1;
		//		jTextFieldConnStatus.setText("Diconnected.");
		//		jButtonConnect.setEnabled(true);
		//		jButtonRefresh.setEnabled(false);
		//		jButtonDisconnect.setEnabled(false);
		//		jButtonDiscover.setEnabled(true);
	}


	/** GATT DISCOVERY **/  
	private static final int IDLE = 0;
	private static final int SERVICES = 1;
	private static final int ATTRIBUTES = 2;
	private Iterator<BLEService> discovery_it = null;
	private BLEService discovery_srv = null;
	private int discovery_state = IDLE;

	// Callbacks for class system (index = 0)
	public void receive_system_reset() {}
	public void receive_system_hello() {}
	public void receive_system_address_get(BDAddr address) {}
	public void receive_system_reg_write(int result) {}
	public void receive_system_reg_read(int address, int value) {}
	public void receive_system_get_counters(int txok, int txretry, int rxok, int rxfail) {}
	public void receive_system_get_connections(int maxconn) {}
	public void receive_system_read_memory(int address, byte[] data) {}
	public void receive_system_get_info(int major, int minor, int patch, int build, int ll_version, int protocol_version, int hw) {
		//		jTextFieldBLED112.setText("Connected. BLED112:" + major + "." + minor + "." + patch + " (" + build + ") " + "ll=" + ll_version + " hw=" + hw);
	}
	public void receive_system_endpoint_tx() {}
	public void receive_system_whitelist_append(int result) {}
	public void receive_system_whitelist_remove(int result) {}
	public void receive_system_whitelist_clear() {}
	public void receive_system_boot(int major, int minor, int patch, int build, int ll_version, int protocol_version, int hw) {}
	public void receive_system_debug(byte[] data) {}
	public void receive_system_endpoint_rx(int endpoint, byte[] data) {}

	// Callbacks for class flash (index = 1)
	public void receive_flash_ps_defrag() {}
	public void receive_flash_ps_dump() {}
	public void receive_flash_ps_erase_all() {}
	public void receive_flash_ps_save(int result) {}
	public void receive_flash_ps_load(int result, byte[] value) {}
	public void receive_flash_ps_erase() {}
	public void receive_flash_erase_page(int result) {}
	public void receive_flash_write_words() {}
	public void receive_flash_ps_key(int key, byte[] value) {}

	// Callbacks for class attributes (index = 2)
	public void receive_attributes_write(int result) {}
	public void receive_attributes_read(int handle, int offset, int result, byte[] value) {}
	public void receive_attributes_read_type(int handle, int result, byte[] value) {}
	public void receive_attributes_user_response() {}
	public void receive_attributes_value(int connection, int reason, int handle, int offset, byte[] value) {
		System.out.println("Attribute Value att=" + Integer.toHexString(handle) + " val = " + bytesToString(value));
	}
	public void receive_attributes_user_request(int connection, int handle, int offset) {}

	// Callbacks for class connection (index = 3)
	public void receive_connection_disconnect(int connection, int result) {}
	public void receive_connection_get_rssi(int connection, int rssi) {}
	public void receive_connection_update(int connection, int result) {}
	public void receive_connection_version_update(int connection, int result) {}
	public void receive_connection_channel_map_get(int connection, byte[] map) {}
	public void receive_connection_channel_map_set(int connection, int result) {}
	public void receive_connection_features_get(int connection, int result) {}
	public void receive_connection_get_status(int connection) {}
	public void receive_connection_raw_tx(int connection) {}

	protected int connection = -1;
	protected BLEDevice bledevice = null;
	private JPanel panel;
	private JPanel panel_1;
	private JLabel serialPortLabel;
	private JTextField portTextField;
	private JButton browsePortsBtn;
	private JButton connectBLEDBtn;
	private JButton disconnectBLEDBtn;
	public void receive_connection_status(int conn, int flags, BDAddr address, int address_type, int conn_interval, int timeout, int latency, int bonding) {
		//		jTextFieldConnStatus.setText("[" + address.toString() + "] Conn = " + conn + " Flags = " + flags);
		//		if (flags != 0) {
		//			bledevice = deviceList.getFromAddress(address.toString());
		//			this.connection = conn;
		//			jButtonConnect.setEnabled(false);
		//			jButtonRefresh.setEnabled(true);
		//			jButtonDisconnect.setEnabled(true);
		//			jButtonDiscover.setEnabled(false);
		//			if (autoclose) this.setVisible(false);
		//		}
		//		else {
		//			System.out.println("Connection lost!");
		//			connection = -1;
		//			bledevice = null;
		//			jButtonConnect.setEnabled(true);
		//			jButtonRefresh.setEnabled(false);
		//			jButtonDisconnect.setEnabled(false);
		//			jButtonDiscover.setEnabled(true);
		//		} 
		//		autoclose = false;
	}
	public void receive_connection_version_ind(int connection, int vers_nr, int comp_id, int sub_vers_nr) {}
	public void receive_connection_feature_ind(int connection, byte[] features) {}
	public void receive_connection_raw_rx(int connection, byte[] data) {}
	public void receive_connection_disconnected(int connection, int reason) {}

	// Callbacks for class attclient (index = 4)
	public void receive_attclient_find_by_type_value(int connection, int result) {}
	public void receive_attclient_read_by_group_type(int connection, int result) {}
	public void receive_attclient_read_by_type(int connection, int result) {}
	public void receive_attclient_find_information(int connection, int result) {}
	public void receive_attclient_read_by_handle(int connection, int result) {}
	public void receive_attclient_attribute_write(int connection, int result) {}
	public void receive_attclient_write_command(int connection, int result) {}
	public void receive_attclient_reserved() {}
	public void receive_attclient_read_long(int connection, int result) {}
	public void receive_attclient_prepare_write(int connection, int result) {}
	public void receive_attclient_execute_write(int connection, int result) {}
	public void receive_attclient_read_multiple(int connection, int result) {}
	public void receive_attclient_indicated(int connection, int attrhandle) {}
	public void receive_attclient_procedure_completed(int connection, int result, int chrhandle) {
		if (discovery_state != IDLE && bledevice != null) {
			if (discovery_state == SERVICES) { // services have been discovered
				discovery_it = bledevice.getServices().values().iterator();
				discovery_state = ATTRIBUTES;
			}
			if (discovery_state == ATTRIBUTES) {
				if (discovery_it.hasNext()) {
					discovery_srv = discovery_it.next();
					bgapi.send_attclient_find_information(connection, discovery_srv.getStart(), discovery_srv.getEnd());
				}
				else { // Discovery is done
					System.out.println("Discovery completed:");
					System.out.println(bledevice.getGATTDescription());
					discovery_state = IDLE;
				}
			}
		}
		if (result != 0) {
			System.err.println("ERROR: Attribute Procedure Completed with error code 0x" + Integer.toHexString(result));
		}
	}
	public void receive_attclient_group_found(int connection, int start, int end, byte[] uuid) {
		if (bledevice != null) {
			BLEService srv = new BLEService(uuid, start, end);
			bledevice.getServices().put(srv.getUuidString(), srv); 
		}
	}
	public void receive_attclient_attribute_found(int connection, int chrdecl, int value, int properties, byte[] uuid) {}
	public void receive_attclient_find_information_found(int connection, int chrhandle, byte[] uuid) {
		if (discovery_state == ATTRIBUTES && discovery_srv != null) {
			BLEAttribute att = new BLEAttribute(uuid, chrhandle);
			discovery_srv.getAttributes().add(att);
		}
	}
	public void receive_attclient_attribute_value(int connection, int atthandle, int type, byte[] value) {
		//System.out.println("Attclient Value att=" + Integer.toHexString(atthandle) + " val = " + bytesToString(value));
	}
	public void receive_attclient_read_multiple_response(int connection, byte[] handles) {}

	// Callbacks for class sm (index = 5)
	public void receive_sm_encrypt_start(int handle, int result) {}
	public void receive_sm_set_bondable_mode() {}
	public void receive_sm_delete_bonding(int result) {}
	public void receive_sm_set_parameters() {}
	public void receive_sm_passkey_entry(int result) {}
	public void receive_sm_get_bonds(int bonds) {}
	public void receive_sm_set_oob_data() {}
	public void receive_sm_smp_data(int handle, int packet, byte[] data) {}
	public void receive_sm_bonding_fail(int handle, int result) {}
	public void receive_sm_passkey_display(int handle, int passkey) {}
	public void receive_sm_passkey_request(int handle) {}
	public void receive_sm_bond_status(int bond, int keysize, int mitm, int keys) {}

	// Callbacks for class gap (index = 6)
	public void receive_gap_set_privacy_flags() {}
	public void receive_gap_set_mode(int result) {}
	public void receive_gap_discover(int result) {}
	public void receive_gap_connect_direct(int result, int connection_handle) {}
	public void receive_gap_end_procedure(int result) {}
	public void receive_gap_connect_selective(int result, int connection_handle) {}
	public void receive_gap_set_filtering(int result) {}
	public void receive_gap_set_scan_parameters(int result) {}
	public void receive_gap_set_adv_parameters(int result) {}
	public void receive_gap_set_adv_data(int result) {}
	public void receive_gap_set_directed_connectable_mode(int result) {}
	public void receive_gap_scan_response(int rssi, int packet_type, BDAddr sender, int address_type, int bond, byte[] data) {
		BLEDevice d = deviceList.getFromAddress(sender.toString());
		if (d == null) { 
			d = new BLEDevice(sender.toString());
			deviceList.add(d);
			System.out.println("Create device: " + d.toString());
		}
		String name = new String(data).trim();
		if (d.getName().length() < name.length()) d.setName(name);
		d.setRssi(rssi);
		deviceList.changed(d);
	}
	public void receive_gap_mode_changed(int discover, int connect) {}

	// Callbacks for class hardware (index = 7)
	public void receive_hardware_io_port_config_irq(int result) {}
	public void receive_hardware_set_soft_timer(int result) {}
	public void receive_hardware_adc_read(int result) {}
	public void receive_hardware_io_port_config_direction(int result) {}
	public void receive_hardware_io_port_config_function(int result) {}
	public void receive_hardware_io_port_config_pull(int result) {}
	public void receive_hardware_io_port_write(int result) {}
	public void receive_hardware_io_port_read(int result, int port, int data) {}
	public void receive_hardware_spi_config(int result) {}
	public void receive_hardware_spi_transfer(int result, int channel, byte[] data) {}
	public void receive_hardware_i2c_read(int result, byte[] data) {}
	public void receive_hardware_i2c_write(int written) {}
	public void receive_hardware_set_txpower() {}
	public void receive_hardware_io_port_status(int timestamp, int port, int irq, int state) {}
	public void receive_hardware_soft_timer(int handle) {}
	public void receive_hardware_adc_result(int input, int value) {}

	// Callbacks for class test (index = 8)
	public void receive_test_phy_tx() {}
	public void receive_test_phy_rx() {}
	public void receive_test_phy_end(int counter) {}
	public void receive_test_phy_reset() {}
	public void receive_test_get_channel_map(byte[] channel_map) {}


	public String bytesToString(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		result.append("[ ");
		for(byte b : bytes) result.append( Integer.toHexString(b & 0xFF) + " ");
		result.append("]");
		return result.toString();        
	}

	Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
	private Task ble113ConnectionTask;

	/** AUTO GENERATED UI **/
	private JProgressBar progressBar;
	private JScrollPane scrollPaneLog;
	private JLabel connLogLabel;
	private JButton bledConnectedBtn;
	private JTextArea taskLogOutput;


	/*************************************************************************
	 * BUTTON HANDLERS 
	 ************************************************************************/

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == connectBLEDBtn) {
			connectBLEDBtnActionPerformed(e);
		}
		if (e.getSource() == disconnectBLEDBtn) {
			disconnectBLEDBtnActionPerformed(e);
		}
		if (e.getSource() == browsePortsBtn) {
			browsePortsBtnActionPerformed(e);
		}
	}

	protected void browsePortsBtnActionPerformed(ActionEvent e) {
		portTextField.setText(BLED112.selectSerialPort());
	}

	protected void disconnectBLEDBtnActionPerformed(ActionEvent e) {
		//TODO: Implement disconnect from serial port
	}

	protected void connectBLEDBtnActionPerformed(ActionEvent e) {
		connectBLEDBtn.setEnabled(false);
		disconnectBLEDBtn.setEnabled(false);
		port  = BLED112.connectSerial(portTextField.getText().trim());

		if (port != null) {
			prefs.put("BLED112Serial", portTextField.getText().trim());
			try {				
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				ble113ConnectionTask = new Task();
				ble113ConnectionTask.addPropertyChangeListener(this);
				ble113ConnectionTask.execute();

			} catch (Exception ex) {
				Logger.getLogger(BLEConnectionDialog.class.getName()).log(Level.SEVERE, null, ex);;

			}
		}

	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress" == evt.getPropertyName()) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setValue(progress);
			taskLogOutput.append(String.format(
                    "Completed %d%% of task.\n", ble113ConnectionTask.getProgress()));
		}
		  
	}

	/** Class for executing BLE Discovery and Connection **/
	private class Task extends SwingWorker<Void, Void> {
		/* Main Task */
		@Override
		protected Void doInBackground() throws Exception {
			// TODO Auto-generated method stub
			return null;
		}

		/* Executed in event dispatching thread */
		@Override
		public void done() {

		}
	}
}
