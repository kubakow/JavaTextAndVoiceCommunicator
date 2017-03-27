package kubakow.chat.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;


public class Server{
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	private DatagramSocket serverReceivingSocket = null;
	private DatagramSocket serverSendingSocket = null;
	boolean flag;
	private List<SingleClientThread> clientsList; 
	private List<InetAddress> voiceAddressList;
	
	public Server() {
		clientsList = new ArrayList<SingleClientThread>();
		voiceAddressList = new ArrayList<InetAddress>();
	}

	public void acceptNewClient(){
		flag = true;
		while(flag){
		try {
			clientSocket = serverSocket.accept();
			SingleClientThread newlyConnectedClient = new SingleClientThread(clientSocket);
			clientsList.add(newlyConnectedClient);

			VoiceClientThread newVoiceClient = new VoiceClientThread(clientSocket.getInetAddress());
			voiceAddressList.add(clientSocket.getInetAddress());
			
			for(int i=0;i<clientsList.size();i++){
				System.out.println(clientsList.get(i).getId());
			}
			System.out.println(clientsList.size());
			newlyConnectedClient.start();
			newVoiceClient.start();
			
			System.out.println(clientSocket);
		} catch (IOException e) {
			e.printStackTrace();
			flag = false;
		}
	}
	}
	

	

	public void shutdownServer() {
		try {
			if (serverSocket != null)
				serverSocket.close();
			
			for(SingleClientThread client : clientsList){
				client.disconnectClient();}
			
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public void startServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			serverReceivingSocket = new DatagramSocket(port+1);
			serverSendingSocket = new DatagramSocket();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	public class SingleClientThread extends Thread{
		Socket clientSocket;
		String clientName;
		InputStream in;
		OutputStream out;
		byte[] bufrec;
		int size;
		String message;
		String fullMessage;
		long id;
		boolean flag2;
		public SingleClientThread(Socket clientSocket){
			this.clientSocket = clientSocket;
			id = this.getId();
			try {
				in = clientSocket.getInputStream();
				out = clientSocket.getOutputStream();
				bufrec = new byte[1024];
				try {
					size = in.read(bufrec);
				} catch (IOException e) {
					e.printStackTrace();
				}
				
				if (size > 0) {
					clientName = new String(bufrec, 0, size);
				}else{
					clientName = "User ";}

			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
		}
		
		public void run(){
			flag2 = true;
			while(flag2){
				
				try {
					size = in.read(bufrec);
				} catch (IOException e) {
					e.printStackTrace();
					size =0;
					flag2 = false;
					
				}
				
				if (size > 0) {
					message = new String(bufrec, 0, size);
					fullMessage = clientName +": "+ message +"\n";
					System.out.println(fullMessage);
				
				for(SingleClientThread client : clientsList){
					if(client.getId()==id){
						
					}else{client.writeMessage(fullMessage);}
				}
				}
			}
			
		}
		
		public void writeMessage(String fullmess){
			
			try {
				out.write(fullmess.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void disconnectClient() {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

public class VoiceClientThread extends Thread{
		
		private InetAddress clientAddress;
		private byte[] buf = new byte[8820];	
		private boolean serverWorkingFlag;
		
		
		DatagramPacket receivedPacket;
		DatagramPacket sentPacket;
		
		public VoiceClientThread(InetAddress clientAddress){
			this.clientAddress = clientAddress;
			
		}	
		
		
			
			@Override
			public void run() {
				
				try{
					
					serverWorkingFlag = true;

					while(serverWorkingFlag){
						receivedPacket = new DatagramPacket(buf,  buf.length);
						serverReceivingSocket.receive(receivedPacket);
						System.out.println("received from: " + receivedPacket.getAddress());
						for(InetAddress address : voiceAddressList){
							if(address==receivedPacket.getAddress()){
							}else{
						sentPacket =  new DatagramPacket(receivedPacket.getData(), receivedPacket.getLength(), address, serverReceivingSocket.getPort());
						System.out.println("sent to: "+address+" "+receivedPacket.getAddress());
						serverSendingSocket.send(sentPacket);
							}
						}
					}
					
					
					}catch(SocketException se){se.printStackTrace();
					}catch(IOException ioe){ioe.printStackTrace();}
				
				
				
				
				
			}
	}
	
	
}