package application;

import java.io.PrintWriter;

import com.fazecast.jSerialComm.*;

public class SerialCommunication {
	
	private PrintWriter output;
	
	public SerialCommunication(){
		SerialPort ports[] = SerialPort.getCommPorts();
		SerialPort port = ports[0];
		
		while(!port.openPort()) {
			try {Thread.sleep(100); } catch(Exception e) {}
			System.out.println("Serial Port " + port.getSystemPortName() + " is not opened.");
		}
		System.out.println("Serial Port is opened.");
		
		port.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
		// wait after connecting, so the bootloader can finish
		try {Thread.sleep(100); } catch(Exception e) {} 
		output = new PrintWriter(ports[0].getOutputStream());
	}
	
	public void open(int index) {
		output.print('o');
		output.print(index);
		output.flush();
		try {Thread.sleep(100); } catch(Exception e) {}
	}
	
	public void close(int index) {
		output.print('c');
		output.print(index);
		output.flush();
		try {Thread.sleep(100); } catch(Exception e) {}
	}
	
	public void openAll() {
		output.print('r');
		output.print(0);
		output.flush();
		try {Thread.sleep(100); } catch(Exception e) {}
	}
	
	public void closeAll() {
		output.print('r');
		output.print(1);
		output.flush();
		try {Thread.sleep(100); } catch(Exception e) {}
	}
	
}