package cc.gilmore.robotarm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.ArrayUtils;
import org.usb4java.Context;
import org.usb4java.Device;
import org.usb4java.DeviceDescriptor;
import org.usb4java.DeviceHandle;
import org.usb4java.DeviceList;
import org.usb4java.LibUsb;
import org.usb4java.LibUsbException;


public class RobotArmDriver extends JFrame {

	private DeviceHandle handle;
	Context context;
	
	final short vendorId = 4711;
	final short productId = 0;
	final long timeout = 10 * 1000;

	//Commands are formatted as 3 bytes to send and 1 byte indicating time to wait after command is executed, units are deci-seconds
	
	//First byte controls grip, shoulder and wrist.
	/*
	 * Bit	Controls	Bit combinations 
	 * 0,1	Grip		00-do not move, 01-close, 10-open
	 * 2,3	Wrist		00-do not move, 01-move up, 10-move down
	 * 4,5	Elbow		00-do not move, 01-move up, 10-move down
	 * 6,7	Shoulder	00-do not move, 01-move up, 10-move down
	 * 
	 * 00	stop grip, wrist, elbow and shoulder movement
	 * 01	grip close
	 * 02	grip open
	 * 04	wrist up
	 * 08	wrist down
	 * 10	elbow up
	 * 20	elbow down
	 * 40	shoulder up
	 * 80	shoulder down 
	 */
	//Second byte controls base.
	//    00-do not move, 01-rotate clockwise, 10-rotate counter-clockwise
	//Third byte controls LED light inside the grip.
	
	final byte STOP 			= (byte)0x0;
	final byte GRIP_CLOSE 		= (byte)0x1;
	final byte GRIP_OPEN 		= (byte)0x2;
	final byte WRIST_UP 		= (byte)0x4;
	final byte WRIST_DOWN 		= (byte)0x8;
	final byte ELBOW_UP 		= (byte)0x10;
	final byte ELBOW_DOWN 		= (byte)0x20;
	final byte SHOULDER_UP 		= (byte)0x40;
	final byte SHOULDER_DOWN 	= (byte)0x80;
	
	final byte BASE_ROTATE_CLOCKWISE = 1;
	final byte BASE_ROTATE_COUNTER_CLOCKWISE = 2;
	
	final byte LIGHT_OFF = 0;
	final byte LIGHT_ON = 1;

	
	// Empirical limits - deciseconds
	final int maxWristDownFromHome = 36;
	final int maxWristUpFromHome = 36;

	final int maxElbowDownFromHome = 80;
	final int maxElbowUpFromHome = 80;

	final int maxShoulderDownFromHome = 80; // Needs a bit more time to get up the further it goes down
	final int maxShoulderUpFromHome = 60; // Ditto

	final int maxBaseClockFromHome = 30;
	final int maxBaseCounterFromHome = 30;

	final int maxGripperOpenFromHome = 0;
	final int maxGripperCloseFromHome = 19;

	
	public static void main(String[] args) {
        /* Use an appropriate Look and Feel */
        /*
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
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
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        
        //Schedule a job for event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
		*/
        
		try {
			RobotArmDriver robot = new RobotArmDriver();
			
			boolean checkRobotIsHomed = true;
			robot.init(checkRobotIsHomed);
			robot.run();
			robot.shutdown();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	private static void createAndShowGUI() {
        //Create and set up the window.
		RobotArmDriver frame = new RobotArmDriver();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Set up the content pane.
        frame.addComponentsToPane();
        
        
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
	
	private void addComponentsToPane() {
        JButton button = new JButton("Clear");
        
        https://docs.oracle.com/javase/tutorial/uiswing/examples/events/KeyEventDemoProject/src/events/KeyEventDemo.java
        	
        button.addActionListener(this);
        
        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);
        
        //Uncomment this if you wish to turn off focus
        //traversal.  The focus subsystem consumes
        //focus traversal keys, such as Tab and Shift Tab.
        //If you uncomment the following line of code, this
        //disables focus traversal and the Tab events will
        //become available to the key event listener.
        //typingArea.setFocusTraversalKeysEnabled(false);
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));
        
        getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(button, BorderLayout.PAGE_END);
    }
	*/
	
	public void init(boolean checkRobotIsHomed) throws Exception {
		try {
			context = new Context();
			int initResult = LibUsb.init(context);
			if(initResult != LibUsb.SUCCESS) throw new LibUsbException("Unable to initialize libusb.", initResult);
			
			handle = findDevice(context, vendorId, productId);
			if(null == handle) {
				throw new Exception("Unable to communicate with robot");
			}
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		boolean successfullyHomedRobot = checkRobotIsHomed ? moveToHomeWithHumanHelp() : true;
		if(!successfullyHomedRobot) {
			throw new Exception("Failed to home the robot");
		}
	}

	public void run() {
		List<byte[]> commandList = new ArrayList<byte[]>();

		commandList.add(new byte[] {SHOULDER_DOWN + ELBOW_UP + WRIST_UP, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {SHOULDER_UP + ELBOW_DOWN + WRIST_DOWN, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {SHOULDER_UP + ELBOW_DOWN + WRIST_DOWN, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {SHOULDER_DOWN + ELBOW_UP + WRIST_UP, STOP, LIGHT_ON, 30});

		/*
		commandList.add(new byte[] {SHOULDER_DOWN, STOP, LIGHT_ON, 50});
		commandList.add(new byte[] {ELBOW_UP, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {WRIST_UP, STOP, LIGHT_ON, 30});

		commandList.add(new byte[] {WRIST_DOWN, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {ELBOW_DOWN, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {SHOULDER_UP, STOP, LIGHT_ON, 70});

		commandList.add(new byte[] {SHOULDER_UP, STOP, LIGHT_ON, 40});
		commandList.add(new byte[] {ELBOW_DOWN, STOP, LIGHT_ON, 20});
		commandList.add(new byte[] {WRIST_DOWN, STOP, LIGHT_ON, 20});

		commandList.add(new byte[] {WRIST_UP, STOP, LIGHT_ON, 20});
		commandList.add(new byte[] {ELBOW_UP, STOP, LIGHT_ON, 20});
		commandList.add(new byte[] {SHOULDER_DOWN, STOP, LIGHT_ON, 50});
		*/
		
		/*
		commandList.add(new byte[] {SHOULDER_DOWN, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {WRIST_DOWN, STOP, LIGHT_ON, 3});
		commandList.add(new byte[] {GRIP_CLOSE, STOP, LIGHT_ON, 2});
		commandList.add(new byte[] {GRIP_OPEN, STOP, LIGHT_ON, 2});
		commandList.add(new byte[] {WRIST_UP, STOP, LIGHT_ON, 3});
		commandList.add(new byte[] {ELBOW_UP, STOP, LIGHT_ON, 30});
		commandList.add(new byte[] {SHOULDER_UP, STOP, LIGHT_ON, 30});
		*/
		
		for(byte[] command : commandList) {
			runCommand(command);
		}
	}
	
	public void shutdown() {
		if(null != handle) LibUsb.close(handle);
		if(null != context) LibUsb.exit(context);
	}
	
	private void runCommand(byte[] command) {
		try {
			if(null == handle) {
				System.out.println("Unable to communicate with the robot for running a command. Is it plugged in?");
				return;
			}
			
			ByteBuffer buffer = ByteBuffer.allocateDirect(3);
			buffer.put(new byte[] {command[0], command[1], command[2]});
			
			int transfered = LibUsb.controlTransfer(handle, 
						    (byte) 0x40, (byte) 0x06, (short) 0x100, (short) 0, buffer, timeout);
			buffer.clear();

			if (transfered < 0) throw new LibUsbException("Control transfer failed", transfered);
			System.out.print(transfered + " bytes sent for command{"+command[0]+","+command[1]+","+command[2]+"} waiting "+command[3]+" deciseconds");
			
			Thread.sleep(command[3] * 100);

			// Send stop command
			buffer = ByteBuffer.allocateDirect(3);
			buffer.put(new byte[] {0, 0, 0});
			
			transfered = LibUsb.controlTransfer(handle, 
						    (byte) 0x40, (byte) 0x06, (short) 0x100, (short) 0, buffer, timeout);

			if (transfered < 0) throw new LibUsbException("Control transfer failed", transfered);
			System.out.println("...stop");
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	vendorId=1003, productId=-30184
	vendorId=3315, productId=12292
	vendorId=5075, productId=22311
	
	vendorId=1080, productId=30976
	vendorId=1080, productId=30976
	vendorId=4130, productId=30740
	vendorId=4130, productId=30728
	vendorId=4130, productId=30728
	
	vendorId=3315, productId=12292
	vendorId=4711, productId=0
	vendorId=5075, productId=22311
	vendorId=1003, productId=-30184

	vendorId=1080, productId=30976
	vendorId=1080, productId=30976
	vendorId=4130, productId=30740
	vendorId=4130, productId=30728
	vendorId=4130, productId=30728
	 */
	private void listDevices(Context context) {
	    DeviceList list = new DeviceList();
	    int result = LibUsb.getDeviceList(context, list);
	    if(result < 0) throw new LibUsbException("Unable to get device list", result);

	    try
	    {
	        for(Device device: list)
	        {
	            DeviceDescriptor descriptor = new DeviceDescriptor();
	            result = LibUsb.getDeviceDescriptor(device, descriptor);
	            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
	            System.out.println("vendorId=" + descriptor.idVendor() + ", productId=" + descriptor.idProduct());
	        }
	    }
	    finally
	    {
	        // Ensure the allocated device list is freed
	        LibUsb.freeDeviceList(list, true);
	    }
	}
	
	private DeviceHandle findDevice(Context context, short vendorId, short productId)
	{
	    DeviceList list = new DeviceList();
	    int result = LibUsb.getDeviceList(context, list);
	    if (result < 0) throw new LibUsbException("Unable to get device list", result);

	    try
	    {
	        // Iterate over all devices and scan for the right one
	        for (Device device: list)
	        {
	            DeviceDescriptor descriptor = new DeviceDescriptor();
	            result = LibUsb.getDeviceDescriptor(device, descriptor);
	            if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
	            short descVendorId = descriptor.idVendor();
	            short descProductId = descriptor.idProduct();
	            if (descVendorId == vendorId && descProductId == productId)
            	{
	            	DeviceHandle handle = new DeviceHandle();
	        		int openArmResult = LibUsb.open(device, handle);
	        		if(openArmResult != LibUsb.SUCCESS) throw new LibUsbException("Unable to open USB device", openArmResult);
	            	return handle;
            	}
	        }
	    }
	    finally
	    {
	        // Ensure the allocated device list is freed
	        LibUsb.freeDeviceList(list, true);
	    }

	    // Device not found
	    return null;
	}

	private boolean moveToHomeWithHumanHelp() {
		boolean homedRobot = false;
		
		int answer = askHuman("Is the robot arm in the home position, in a line pointing straight up? (y or n)", 
						new char[] {'y', 'n'});
		if('y' == answer) {
			homedRobot = true;
			return homedRobot;
		}

		do {
			char axisToNudge = askHuman("Which axis should I nudge? (w = wrist, e = elbow, s = shoulder, b = base)", 
							new char[] {'w', 'e', 's', 'b'});
			char nudgeDirection = askHuman("Which way? (1 = up or clockwise, 2 = down or counter-clockwise)", 
							new char[] {'1', '2'});
			
			byte armNudge = 0;
			byte baseNudge = 0;
			
			switch(axisToNudge) {
			case 'b':
				baseNudge = ('1' == nudgeDirection) ? BASE_ROTATE_CLOCKWISE : BASE_ROTATE_COUNTER_CLOCKWISE;  
				break;
			case 'e':
				armNudge = ('1' == nudgeDirection) ? ELBOW_UP : ELBOW_DOWN;  
				break;
			case 'x':
				armNudge = ('1' == nudgeDirection) ? SHOULDER_UP : SHOULDER_DOWN;  
				break;
			case 'w':
				armNudge = ('1' == nudgeDirection) ? WRIST_UP : WRIST_DOWN;  
				break;
			}
			
			byte nudgeDuration = 5;
			byte[] command = new byte[] {armNudge, baseNudge, 0, nudgeDuration};
			runCommand(command);
	
			char success = askHuman("Is the robot homed?", new char[] {'y', 'n'});
	
			homedRobot = ('y' == success);
		} while(!homedRobot);
		
		return homedRobot;
	}
	
	private char askHuman(final String question, final char[] allowedAnswers) {
		System.out.println(question);

		char answer = getAnswer();
		while(!ArrayUtils.contains(allowedAnswers, answer)) {
			System.out.println("Allowable answers are only: " + ArrayUtils.toString(allowedAnswers));
			answer = getAnswer();
		}
		return answer;
	}
	
	private char getAnswer() {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); 
         
        try {
			String answer = reader.readLine();
			return answer.charAt(0);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
        return 0;
	}
}
