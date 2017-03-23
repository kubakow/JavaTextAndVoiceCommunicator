package kubakow.chat.client;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class MessageFrame extends JFrame implements ActionListener, Runnable {
	private String username;
	private JTextArea previousMessagesTextArea;
	private JButton sendButton;
	private JTextArea messageTextArea;
	private JPanel messagePanel;
	private JPanel previousMessagesTextPanel;
	private JPanel mainPanel;
	private JMenuBar menuBar;
	private JMenu menu;
	private JMenuItem menuItem;
	private JPanel forSendPanel;
	private JPanel sendPanel;
	private Socket clientSocket;
	private OutputStream outputStream;
	private String messageFromSend;
	private InputStream inputStream;
	private byte[] bufferReceived;
	private int bufferSize;
	private String messageString;
	private boolean flag;
	
	public MessageFrame(Socket clientSocket, String username) {
		this.username = username;
		this.clientSocket = clientSocket;
		
		try {
			inputStream = clientSocket.getInputStream();
			outputStream = clientSocket.getOutputStream();
			bufferReceived = new byte[1024];
			
			try {
				byte[] buffer = username.getBytes();
				outputStream.write(buffer);
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		// frame settings for communication
		JFrame mainFrame = new JFrame("Lets Talk " + username);
		mainFrame.setSize(400, 450);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				try {
					flag=false;
					outputStream.close();
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				e.getWindow().dispose();
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		menuBar = new JMenuBar();
		menu = new JMenu("Let's Talk");
		menu.setMnemonic(KeyEvent.VK_L);
		menuBar.add(menu);
		menuItem = new JMenuItem("Connect to voice chat");
		menu.add(menuItem);
		menuItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//TODO another window with voice chat controls
				
			}
		});

		Border raisedBevel, loweredBevel, compound;
		raisedBevel = BorderFactory.createRaisedBevelBorder();
		loweredBevel = BorderFactory.createLoweredBevelBorder();
		compound = BorderFactory.createCompoundBorder(raisedBevel, loweredBevel);

		messageTextArea = new JTextArea();
		messageTextArea.setLineWrap(true);
		messageTextArea.setSize(new Dimension(300, 150));
		messageTextArea.setWrapStyleWord(true);
		messageTextArea.setLineWrap(true);
		messageTextArea.setBackground(new Color(250, 250, 250));
		messageTextArea.setFont(messageTextArea.getFont().deriveFont(12f));
		messageTextArea.setBorder(loweredBevel);

		messagePanel = new JPanel();
		BorderLayout newmess = new BorderLayout(10, 10);
		messagePanel.setLayout(newmess);
		messagePanel.add(messageTextArea);

		previousMessagesTextPanel = new JPanel();
		previousMessagesTextArea = new JTextArea();
		previousMessagesTextArea.setBorder(compound);
		previousMessagesTextArea.setSize(new Dimension(360, 280));
		previousMessagesTextArea.setFont(previousMessagesTextArea.getFont().deriveFont(12f));
		previousMessagesTextArea.setLineWrap(true);
		previousMessagesTextArea.setWrapStyleWord(true);
		previousMessagesTextArea.setEditable(false);
		BorderLayout laytext = new BorderLayout(1, 1);
		previousMessagesTextPanel.setLayout(laytext);
		previousMessagesTextPanel.add(previousMessagesTextArea);

		JScrollPane previousMessagesScrollPane = new JScrollPane(previousMessagesTextPanel);
		previousMessagesScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		previousMessagesScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		previousMessagesScrollPane.setPreferredSize(new Dimension(380, 280));

		JScrollPane messageScrollPane = new JScrollPane(messagePanel);
		messageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		messageScrollPane.setPreferredSize(new Dimension(350, 150));


		sendPanel = new JPanel();
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					byte[] buf = getMessage().getBytes();
					outputStream.write(buf);
					setTextArea(username+": "+getMessage()+"\n");
					setMessage(null);
				
				} catch (IOException e1) {
					setTextArea("\n"+username+": "+getMessage()+"\n"+"You have no connection with server, this message will not be delivered");
				}

			}
		});
		//
		forSendPanel = new JPanel();
		BorderLayout messageLayout = new BorderLayout(10, 10);
		forSendPanel.setLayout(messageLayout);
		forSendPanel.add(messageScrollPane);
		forSendPanel.add(sendPanel, BorderLayout.EAST);
		sendPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		sendPanel.add(sendButton, c);

		// setting main panel
		mainPanel = new JPanel();
		GroupLayout mainlayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainlayout);
		mainlayout.setAutoCreateGaps(true);
		mainlayout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup horizontal = mainlayout.createSequentialGroup();
		horizontal.addGroup(mainlayout.createParallelGroup().addComponent(previousMessagesScrollPane).addComponent(forSendPanel));
		mainlayout.setHorizontalGroup(horizontal);

		GroupLayout.SequentialGroup vertical = mainlayout.createSequentialGroup();
		vertical.addGroup(mainlayout.createSequentialGroup().addComponent(previousMessagesScrollPane));
		vertical.addGroup(mainlayout.createSequentialGroup().addComponent(forSendPanel));
		mainlayout.setVerticalGroup(vertical);

		mainFrame.setJMenuBar(menuBar);
		mainFrame.add(mainPanel);
		mainFrame.setVisible(true);
	}

	public void run(){
		flag = true;
		while(flag){
			if(flag==false){
				break;
			}
			try {
				bufferSize = inputStream.read(bufferReceived);
			} catch (IOException e) {
				//e.printStackTrace();
				System.out.println("You have been disconnected");
				try{
				inputStream.close();
				clientSocket.close();
				flag=false;
				bufferSize=0;
				}catch(Exception ee){ee.printStackTrace();}
			}
			
			if (bufferSize > 0) {
				messageString = new String(bufferReceived, 0, bufferSize);
				setTextArea(messageString);
			}else{
				setTextArea("Connection lost");
				flag=false;;
			}
		
		}
		try {
			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public String getMessage() {
		return messageTextArea.getText();
	}

	public void setMessage(String message) {
		this.messageTextArea.setText(message);
	}

	public String getTextArea() {
		return previousMessagesTextArea.getText();
	}

	public void setTextArea(String message) {
		this.previousMessagesTextArea.append(message);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public String getUsername() {
		return username;
	}
	public String getMessageFromSend() {
		return messageFromSend;
	}

	private int counter = 0;

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	
	
	
}
