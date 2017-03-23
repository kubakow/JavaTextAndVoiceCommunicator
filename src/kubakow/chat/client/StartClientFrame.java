package kubakow.chat.client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class StartClientFrame extends JFrame{

	private JFrame startClientFrame;
	private JPanel mainPanel;
	private JLabel ipLabel;
	private JLabel portLabel;
	private JLabel nameLabel;
	private JTextField ipTextField;
	private JTextField portTextField;
	private JTextField usernameTextField;
	private JButton connectButton;
	private JButton closeButton;
	private Socket socket;
	
	public StartClientFrame(){
		startClientFrame = new JFrame("Choose server to connect");
		startClientFrame.setSize(250, 170);
		startClientFrame.setLocationRelativeTo(null);
		startClientFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		ipLabel = new JLabel("IP: ");
		portLabel = new JLabel("PORT: ");
		nameLabel = new JLabel("Your name: ");
	
		Dimension dim = new Dimension(100, 10);
		ipTextField = new JTextField();
		ipTextField.setPreferredSize(dim);
		ipTextField.setEditable(true);
		
	
		portTextField = new JTextField();
		portTextField.setPreferredSize(dim);
		portTextField.setEditable(true);
	
		usernameTextField = new JTextField();
		usernameTextField.setPreferredSize(dim);
		usernameTextField.setEditable(true);
	
		connectButton = new JButton("CONNECT");
		connectButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				try {
					socket = new Socket(getIp(),getPort());
					new Thread(new MessageFrame(socket,getUserName())).start();
				} catch (UnknownHostException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
			}
		});
		
		closeButton = new JButton("CLOSE");
		closeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				startClientFrame.dispose();
			}
		});
		
		mainPanel = new JPanel();
		GroupLayout mainLayout = new GroupLayout(mainPanel);
		mainPanel.setLayout(mainLayout);
		mainLayout.setAutoCreateGaps(true);
		mainLayout.setAutoCreateContainerGaps(true);
		
		GroupLayout.SequentialGroup verticalMainLayout = mainLayout.createSequentialGroup();
		verticalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(ipLabel).addComponent(ipTextField));
		verticalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(portLabel).addComponent(portTextField));
		verticalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(nameLabel).addComponent(usernameTextField));
		verticalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(connectButton).addComponent(closeButton));
		mainLayout.setVerticalGroup(verticalMainLayout);
		
		GroupLayout.SequentialGroup horizontalMainLayout = mainLayout.createSequentialGroup();
		horizontalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(ipLabel)
				.addComponent(portLabel).addComponent(nameLabel).addComponent(connectButton));
		horizontalMainLayout.addGroup(mainLayout.createParallelGroup().addComponent(ipTextField)
				.addComponent(portTextField).addComponent(usernameTextField).addComponent(closeButton));
		mainLayout.setHorizontalGroup(horizontalMainLayout);
		
		startClientFrame.add(mainPanel);
		
		startClientFrame.setVisible(true);
	}
	
	public String getIp() {
		return ipTextField.getText();
	}

	public void setIp(String ip) {
		this.ipTextField.setText(ip);
	}

	public Integer getPort() {
		return Integer.valueOf(portTextField.getText());
	}

	public void setPort(String port) {
		this.portTextField.setText(port);
	}

	public String getUserName() {
		return usernameTextField.getText();
	}

	public void setUserName(String username) {
		this.usernameTextField.setText(username);
	}


	
}
