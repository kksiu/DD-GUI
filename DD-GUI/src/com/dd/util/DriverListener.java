package com.dd.util;

public interface DriverListener {
	
	/** Add additional listeners needed for the Driver here **/
	
	
	/** End additional listeners **/
	
	/**
	 * Testing a data pattern at time intervals
	 * @param data	- the byte values to be tested
	 * @param timestamp	- the time at execution 
	 */
	void testPattern(byte[] data, int timestamp);

	/**
	 * Testing the time sync data patterns 
	 * @param seq	 - data[0] & 0xFF
	 * @param timestamp - the time at execution
	 */
	void timeSync(int seq, int timestamp);

	/**
	 * 
	 * @param value
	 */
	void manufacturer(String value);
	
	/**
	 * 
	 * @param value
	 */
	void model_number(String value);
	
	/**
	 * 
	 * @param value
	 */
	void serial_number(String value);
	
	/**
	 * 
	 * @param value
	 */
	void hw_revision(String value);
	
	/**
	 * 
	 * @param value
	 */
	void fw_revision(String value);

	/**
	 * 
	 * @param value
	 */
	void alertLevel(int value);
	
	/**
	 * 
	 * @param value
	 */
	void imuMode(int value);
	
	/**
	 * 
	 * @param value
	 */
	void imuInterrupt(int value);
	
	/**
	 * 
	 * @param ax
	 * @param ay
	 * @param az
	 * @param gx
	 * @param gy
	 * @param gz
	 * @param timestamp
	 */
	void imu(int ax, int ay, int az, int gx, int gy, int gz, int timestamp);
	
	/**
	 * 
	 * @param w
	 * @param x
	 * @param y
	 * @param z
	 * @param timestamp
	 */
	void quaternion(int w, int x, int y, int z, int timestamp);

}
