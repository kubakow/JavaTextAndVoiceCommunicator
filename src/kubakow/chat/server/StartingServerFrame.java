package kubakow.chat.server;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class StartingServerFrame extends JFrame implements ActionListener {

	private JFrame serverFrame;
	private JTextField ip1;
	private JTextField port1;
	private JLabel iplabel;
	private JLabel portlabel;
	private Server server;
	private Socket client;
	private JPanel main;
	private JButton start;
	private JButton disconnect;

	public StartingServerFrame(Server server){
		this.server = server;
		
		serverFrame = new JFrame("Create server");
		serverFrame.setSize(new Dimension(225, 150));
		serverFrame.setLocationRelativeTo(null);
		serverFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		serverFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				server.shutdownServer();
				e.getWindow().dispose();
			}
		});
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ip1 = new JTextField();
		ip1.setText(getHost().getHostAddress());
		ip1.setPreferredSize(new Dimension(100,10));
		ip1.setEditable(false);
		iplabel = new JLabel("Your IP: ");
		portlabel = new JLabel("Set Port: ");
		port1 = new JTextField();
		port1.setPreferredSize(new Dimension(100,10));
		
		
		start = new JButton("START");
		start.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					
						Thread runningServer = new Thread(){
									
						public void run() {
							try {
						server.startServer(getPort1());
						port1.setEnabled(false);
						ip1.setEnabled(false);
						start.setEnabled(false);
						server.acceptNewClient();
					} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
					};
					runningServer.start();
					}
				});

		disconnect = new JButton("DISCONNECT");
		disconnect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					server.shutdownServer();
					port1.setEnabled(true);
					ip1.setEnabled(true);
					start.setEnabled(true);
				} catch (Exception e1) {
						e1.printStackTrace();
					}

				}
			});
		
		
		
		
		main = new JPanel();
		GroupLayout mainlayout = new GroupLayout(main);
		main.setLayout(mainlayout);
		mainlayout.setAutoCreateGaps(true);
		mainlayout.setAutoCreateContainerGaps(true);

		GroupLayout.SequentialGroup horizontal = mainlayout.createSequentialGroup();
		horizontal.addGroup(mainlayout.createParallelGroup()
				.addComponent(iplabel).addComponent(portlabel).addComponent(start));
		horizontal.addGroup(mainlayout.createParallelGroup()
				.addComponent(ip1).addComponent(port1).addComponent(disconnect));
		
		mainlayout.setHorizontalGroup(horizontal);

		GroupLayout.SequentialGroup vertical = mainlayout.createSequentialGroup();
		vertical.addGroup(mainlayout.createParallelGroup().addComponent(iplabel).addComponent(ip1));
		vertical.addGroup(mainlayout.createParallelGroup().addComponent(portlabel).addComponent(port1));
		vertical.addGroup(mainlayout.createParallelGroup().addComponent(start).addComponent(disconnect));
		mainlayout.setVerticalGroup(vertical);

		
		serverFrame.add(main);
		
		serverFrame.setVisible(true);
	}

	public InetAddress getHost() {
		InetAddress IP = null;
		try {
			IP = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e1) {
			e1.printStackTrace();

		}
		return IP;
	}

	public int getPort1() {
		return Integer.parseInt(port1.getText());
	}

	public void setPort1(String port11) {
		this.port1.setText(port11);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

}