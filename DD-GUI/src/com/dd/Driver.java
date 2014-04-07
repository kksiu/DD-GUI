package com.dd;

import java.util.ArrayList;

import org.thingml.bglib.BGAPI;
import org.thingml.bglib.BGAPIDefaultListener;
import org.thingml.rtsync.core.TimeSynchronizable;
import org.thingml.rtsync.core.TimeSynchronizer;

import com.dd.util.DriverListener;

/**
 * This class contains access to the BGAPI and the connections necessary for BLE communcation
 * It also includes the logic that handles Image sending, Gyroscope data reading, or any
 * other BGAPI 
 */
public class Driver extends BGAPIDefaultListener implements TimeSynchronizable {

	private final int DEFAULT_SUB = 0x01; // 0x01 for notifications and 0x02 for indications
	protected BGAPI bgapi;
	protected int connection;

	private ArrayList<DriverListener> listeners = new ArrayList<DriverListener>();  

	public synchronized void addDriverListener(DriverListener l) {
		listeners.add(l);
	}

	public synchronized void removeDriverListener(DriverListener l) {
		listeners.remove(l);
	}

	// 16 bits timestamps with a 4ms resolution -> 18bits timestamps in ms -> Max value = 0x3FFF
	private TimeSynchronizer rtsync = new TimeSynchronizer(this, 0x03FFFF);

	public long getEpochTimestamp(int ts) {
		if (rtsync.isRunning()) return rtsync.getSynchronizedEpochTime(ts);
		else return 0;
	}

	public Driver(BGAPI bgapi, int connection) {
		this.bgapi = bgapi;
		this.connection = connection;
		bgapi.addListener(this);
	}

	public void disconnect() {
		bgapi.removeListener(this);
	}

	public void startTimeSync() {
		subscribeTimeSync();
		rtsync.start_timesync();
	}

	public void stopTimeSync() {
		unsubscribeTimeSync();
		rtsync.stop_timesync();
	}


	/**************************************************************
	 * Alert 
	 **************************************************************/ 
	public static final int ALERT_LEVEL = 0x4B;

	public void readAlertLevel() {
		bgapi.send_attclient_read_by_handle(connection, ALERT_LEVEL);
	}

	public void setAlertLevel(int value) {
		byte[] i = new byte[1];
		i[0] = (byte)(value & 0xFF);
		bgapi.send_attclient_write_command(connection, ALERT_LEVEL, i);
	}


	/**************************************************************
	 * IMU (Inertial Measurement Unit) from Gyroscope
	 **************************************************************/ 
	public static final int IMU_VALUE = 0x33;
	public static final int IMU_CONFIG = 0x34;

	public static final int QUAT_VALUE = 0x36;
	public static final int QUAT_CONFIG = 0x37;

	public static final int IMU_MODE = 0x42;

	public static final int IMU_INTERRUPT_VALUE = 0x3F;
	public static final int IMU_INTERRUPT_CONFIG = 0x40;

	public void subscribeIMU() {
		bgapi.send_attclient_write_command(connection, IMU_CONFIG, new byte[]{DEFAULT_SUB, 0x00});
	}

	public void unsubscribeIMU() {
		bgapi.send_attclient_write_command(connection, IMU_CONFIG, new byte[]{0x00, 0x00});
	}

	public void subscribeQuaternion() {
		bgapi.send_attclient_write_command(connection, QUAT_CONFIG, new byte[]{DEFAULT_SUB, 0x00});
	}

	public void unsubscribeQuaternion() {
		bgapi.send_attclient_write_command(connection, QUAT_CONFIG, new byte[]{0x00, 0x00});
	}

	public void subscribeIMUInterrupt() {
		bgapi.send_attclient_write_command(connection, IMU_INTERRUPT_CONFIG, new byte[]{0x01, 0x00});
	}

	public void unsubscribeIMUInterrupt() {
		bgapi.send_attclient_write_command(connection, IMU_INTERRUPT_CONFIG, new byte[]{0x00, 0x00});
	}

	public void readIMUMode() {
		bgapi.send_attclient_read_by_handle(connection, IMU_MODE);
	}

	public void setIMUMode(int value) {
		byte[] i = new byte[1];
		i[0] = (byte)(value & 0xFF);
		bgapi.send_attclient_write_command(connection, IMU_MODE, i);
	}

	private synchronized void imu(byte[] value) {

		int gx = ((value[1] & 0xFF) << 8) + (value[0] & 0xFF); if (gx > (1<<15)) { gx = gx - (1<<16); }
		int gy = ((value[3] & 0xFF) << 8) + (value[2] & 0xFF); if (gy > (1<<15)) { gy = gy - (1<<16); }
		int gz = ((value[5] & 0xFF) << 8) + (value[4] & 0xFF); if (gz > (1<<15)) { gz = gz - (1<<16); }

		int ax = ((value[7] & 0xFF) << 8) + (value[6] & 0xFF); if (ax > (1<<15)) { ax = ax - (1<<16); }
		int ay = ((value[9] & 0xFF) << 8) + (value[8] & 0xFF); if (ay > (1<<15)) { ay = ay - (1<<16); }
		int az = ((value[11] & 0xFF) << 8) + (value[10] & 0xFF); if (az > (1<<15)) { az = az - (1<<16); }

		int ts = ((value[13] & 0xFF) << 8) + (value[12] & 0xFF);

		for (DriverListener l : listeners) {
			l.imu(ax, ay, az, gx, gy, gz, ts*4);
		}
	}

	private synchronized void quaternion(byte[] value) {
		int w = ((value[1] & 0xFF) << 8) + (value[0] & 0xFF); if (w > (1<<15)) { w = w - (1<<16); }
		int x = ((value[3] & 0xFF) << 8) + (value[2] & 0xFF); if (x > (1<<15)) { x = x - (1<<16); }
		int y = ((value[5] & 0xFF) << 8) + (value[4] & 0xFF); if (y > (1<<15)) { y = y - (1<<16); }
		int z = ((value[7] & 0xFF) << 8) + (value[6] & 0xFF); if (z > (1<<15)) { z = z - (1<<16); }
		int ts = ((value[9] & 0xFF) << 8) + (value[8] & 0xFF);

		for (DriverListener l : listeners) {
			l.quaternion(w, x, y, z, ts*4);
		}
	}

	private void imuMode(byte[] value) {
		for (DriverListener l : listeners) {
			l.imuMode((value[0] & 0xFF));
		}
	}

	private void alertLevel(byte[] value) {
		for (DriverListener l : listeners) {
			l.alertLevel((value[0] & 0xFF));
		}
	}

	private void imuInterrupt(byte[] value) {
		for (DriverListener l : listeners) {
			l.imuInterrupt((value[0] & 0xFF));
		}
	}

	/**************************************************************
	 * TESTING PATTERN
	 **************************************************************/ 
	public static final int CLK_VALUE = 0x44;
	public static final int CLK_CONFIG = 0x45;

	@Override
	public void sendTimeRequest(int seqNum) {
		bgapi.send_attclient_write_command(connection, CLK_VALUE, new byte[]{(byte)seqNum});
	}

	public void subscribeTimeSync() {
		bgapi.send_attclient_write_command(connection, CLK_CONFIG, new byte[]{0x01, 0x00});
	}

	public void unsubscribeTimeSync() {
		bgapi.send_attclient_write_command(connection, CLK_CONFIG, new byte[]{0x00, 0x00});
	}

	private synchronized void timeSync(byte[] value) {

		int ts = ((value[2] & 0xFF) << 8) + (value[1] & 0xFF);

		rtsync.receive_TimeResponse(value[0] & 0xFF, ts*4);

		for (DriverListener l : listeners) {
			l.timeSync(value[0] & 0xFF, ts*4);
		}
	}


	/**************************************************************
	 * TESTING PATTERN
	 **************************************************************/ 
	public static final int TEST_VALUE = 0x47;
	public static final int TEST_CONFIG = 0x48;

	public void subscribeTestPattern() {
		bgapi.send_attclient_write_command(connection, TEST_CONFIG, new byte[]{0x01, 0x00});
	}

	public void unsubscribeTestPattern() {
		bgapi.send_attclient_write_command(connection, TEST_CONFIG, new byte[]{0x00, 0x00});
	}

	private synchronized void testPattern(byte[] value) {

		int ts = (value[0] & 0xFF);

		for (DriverListener l : listeners) {
			l.testPattern(value, ts*4);
		}
	}


	/**************************************************************
	 * Device info
	 **************************************************************/ 

	public static final int MANUFACTURER = 0x0B;
	public static final int MODEL = 0x19;
	public static final int SERIAL = 0x11;
	public static final int HW_REV = 0x16;
	public static final int FW_REV = 0x0E;

	public void requestDeviceInfo() {
		bgapi.send_attclient_read_by_handle(connection, MANUFACTURER);
	}

	synchronized void manufacturer(byte[] value) {
		for (DriverListener l : listeners) {
			l.manufacturer(new String(value));
		}
		bgapi.send_attclient_read_by_handle(connection, MODEL);
	}

	synchronized void model_number(byte[] value) {
		for (DriverListener l : listeners) {
			l.model_number(new String(value));
		}
		bgapi.send_attclient_read_by_handle(connection, SERIAL);
	}

	synchronized void serial_number(byte[] value) {
		for (DriverListener l : listeners) {
			l.serial_number(new String(value));
		}
		bgapi.send_attclient_read_by_handle(connection, HW_REV);
	}

	synchronized void hw_revision(byte[] value) {
		for (DriverListener l : listeners) {
			l.hw_revision(new String(value));
		}
		bgapi.send_attclient_read_by_handle(connection, FW_REV);
	}

	synchronized void fw_revision(byte[] value) {
		for (DriverListener l : listeners) {
			l.fw_revision(new String(value));
		}
	}


	/**************************************************************
	 * Receive attribute values
	 **************************************************************/ 

	long receivedBytes = 0;

	public long getReceivedBytes() {
		return receivedBytes;
	}

	@Override
	public void receive_attclient_attribute_value(int connection, int atthandle, int type, byte[] value) {
		if (this.connection == connection) {
			receivedBytes += value.length;
			switch(atthandle) {

			case IMU_VALUE: imu(value); break;
			case QUAT_VALUE: quaternion(value); break;
			case IMU_MODE: imuMode(value); break;
			case IMU_INTERRUPT_VALUE: imuInterrupt(value); break;

			case MANUFACTURER: manufacturer(value); break;
			case MODEL: model_number(value); break;
			case SERIAL: serial_number(value); break;
			case HW_REV: hw_revision(value); break;
			case FW_REV: fw_revision(value); break;

			case CLK_VALUE: timeSync(value); break;
			case TEST_VALUE: testPattern(value); break;

			case ALERT_LEVEL: alertLevel(value); break;

			default: 
				System.out.println("[Driver] Got unknown attribute. Handle=" + Integer.toHexString(atthandle) + " val = " + bytesToString(value));
				break;
			}
		}
	}

	/**
	 * Convert Bytes to String 
	 * NOTE: Leading byte 0's do not show in log
	 * @param bytes - the data in bytes
	 * @return String - Example:"[ a 0 fa 0 1 ]"
	 */
	public String bytesToString(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		result.append("[ ");
		for(byte b : bytes) result.append( Integer.toHexString(b & 0xFF) + " ");
		result.append("]");
		return result.toString();        
	}
}
