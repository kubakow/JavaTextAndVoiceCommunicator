package kubakow.chat.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ClientVoiceControls {
	private JPanel mainPanel;
	private JButton turnOnMicButton;
	private JButton turnOffMicButton;
	private JButton turnOnSoundButton;
	private JButton turnOffSoundButton;
	private TargetDataLine targetLine;
	private SourceDataLine sourceLine;
	private boolean sendingFromMicFlag = true;
	private boolean receivingToPhonesFlag = true;
	private DatagramSocket datagramSocket;
	private DatagramSocket datagramSocket2;
	private InetAddress address;
	private InetAddress constructorInetAddress;
	private int constructorPort;
	
	public ClientVoiceControls(InetAddress constructorInetAddress, int contructorPort){
		this.constructorInetAddress = constructorInetAddress;
		this.constructorPort = constructorPort;
	JFrame frame = new JFrame("Voice controls");
	frame.setSize(500, 70);
	frame.setLocationRelativeTo(null);
	frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	frame.addWindowListener(new WindowAdapter() {
		@Override
		public void windowClosing(WindowEvent e) {
			if(sourceLine!=null && sourceLine.isOpen())
				sourceLine.close();
			if(targetLine!= null && targetLine.isOpen())
				targetLine.close();
			if(!datagramSocket.isClosed())
			datagramSocket.close();
			if(!datagramSocket2.isClosed())
				datagramSocket2.close();
		}
	});
	mainPanel = new JPanel();
	
	turnOnMicButton = new JButton();
	turnOnMicButton.setText("Mic ON");
	turnOnMicButton.setSize(new Dimension(40,10));
	turnOnMicButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			targetThread.start();
			turnOnMicButton.setEnabled(false);
			turnOffMicButton.setEnabled(true);
		}
	});
	
	turnOffMicButton = new JButton();
	turnOffMicButton.setText("Mic OFF");
	turnOffMicButton.setSize(new Dimension(40,10));
	turnOffMicButton.setEnabled(false);
	turnOffMicButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			stopUsingMic();
			turnOnMicButton.setEnabled(true);
			turnOffMicButton.setEnabled(false);
		}
	});
	
	
	turnOnSoundButton = new JButton();
	turnOnSoundButton.setText("Listen");
	turnOnSoundButton.setSize(new Dimension(40,10));
	turnOnSoundButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sourceThread.start();
			turnOnSoundButton.setEnabled(false);
			turnOffSoundButton.setEnabled(true);
		}
	});
	
	
	
	turnOffSoundButton = new JButton();
	turnOffSoundButton.setText("Mute");
	turnOffSoundButton.setSize(new Dimension(40,10));
	turnOffSoundButton.setEnabled(false);
	turnOffSoundButton.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			muteSound();
			turnOnSoundButton.setEnabled(true);
			turnOffSoundButton.setEnabled(false);
		}
	});
	
	
	mainPanel.add(turnOnMicButton);
	mainPanel.add(turnOffMicButton);
	mainPanel.add(turnOnSoundButton);
	mainPanel.add(turnOffSoundButton);
	
	frame.add(mainPanel);
	
	frame.setVisible(true);
	accessMicAndPhones();
	
	
	}
	public void accessMicAndPhones(){
		try{
			AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, 4, 44100, false);
			
			DataLine.Info lineInfo = new DataLine.Info(TargetDataLine.class, format);
			DataLine.Info sLineInfo = new DataLine.Info(SourceDataLine.class, format);
			
			if(!AudioSystem.isLineSupported(lineInfo) && !AudioSystem.isLineSupported(sLineInfo)){
				System.err.println("line not supported");
			}
			targetLine = (TargetDataLine)AudioSystem.getLine(lineInfo);
			targetLine.open(format);
			
			sourceLine = (SourceDataLine)AudioSystem.getLine(sLineInfo);
			sourceLine.open(format);
			
			
		}catch(LineUnavailableException lue){lue.printStackTrace();}		
	
				
	}
	
	
	
	Thread targetThread = new Thread(){
		byte[] buf = new byte[8820];
		
	@Override
		public void run() {
		
		try {
			System.out.println("Starting capturing microphone");
			targetLine.start();
			datagramSocket = new DatagramSocket();
		int readBytes;
		
		DatagramPacket packet;
		int i = 0;
		address = constructorInetAddress;
		while(sendingFromMicFlag){
			readBytes = targetLine.read(buf, 0, buf.length);
			packet = new DatagramPacket(buf, readBytes, address, constructorPort+1);
			datagramSocket.send(packet);
			System.out.println("wyslano: "+i+"packetow");
		
		}
		} catch (Exception e) {
		e.printStackTrace();
		
		}
		
	
	}
	
	};
	
	public void stopUsingMic(){
		sendingFromMicFlag = false;
		if(targetLine!=null){
			targetLine.stop();
		}
		if(datagramSocket!=null)
		datagramSocket.close();
		
	}
	
	Thread sourceThread = new Thread(){
		byte[] buf = new byte[8820];
		
		public void run() {
			try{
				sourceLine.start();
				datagramSocket2 = new DatagramSocket(constructorPort+1);
				DatagramPacket packet;
				while(receivingToPhonesFlag){
					packet = new DatagramPacket(buf, buf.length);
					datagramSocket2.receive(packet);
				}
				
			}catch(Exception e){e.printStackTrace();}
			
		}
	};
	
	public void muteSound(){
		receivingToPhonesFlag = false;
		if(sourceLine!=null){
		sourceLine.flush();
		sourceLine.stop();
		}
		if(datagramSocket2!=null)
			datagramSocket2.close();
	}
}