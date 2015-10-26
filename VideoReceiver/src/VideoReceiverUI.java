import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.event.ActionEvent;

public class VideoReceiverUI {
	private String savePath = "";
    private ServerSocket serverSocket;
    private Socket client;
    private Thread listeningThread;
	
	
	private JFrame frame;
	private JTextField savePathInput;
	private JTextField commandFileInput;
	/**
	 * @wbp.nonvisual location=283,391
	 */
	private final JFileChooser fileChooser = new JFileChooser();
    private JTextField portNumberInput;

	
	/**
	 * Launch the application.
	 */
	
	
	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					VideoReceiverUI window = new VideoReceiverUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 */
	public VideoReceiverUI() throws IOException {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 528, 359);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		savePathInput = new JTextField();
		savePathInput.setBounds(17, 59, 274, 28);
		frame.getContentPane().add(savePathInput);
		savePathInput.setColumns(10);
		
		commandFileInput = new JTextField();
		commandFileInput.setColumns(10);
		commandFileInput.setBounds(17, 120, 274, 28);
		frame.getContentPane().add(commandFileInput);
		
		JLabel lblSaveDirectory = new JLabel("Save Directory");
		lblSaveDirectory.setBounds(17, 45, 97, 16);
		frame.getContentPane().add(lblSaveDirectory);
		
		JLabel commandLabel = new JLabel("Command File to run on Receive");
		commandLabel.setBounds(17, 102, 274, 16);
		frame.getContentPane().add(commandLabel);
		
		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int chosen = fileChooser.showOpenDialog(frame);
				if (chosen == JFileChooser.APPROVE_OPTION){
					savePath = fileChooser.getSelectedFile().getAbsolutePath();
					savePathInput.setText(savePath);
				}
			}
		});
		btnNewButton.setBounds(292, 60, 117, 29);
		frame.getContentPane().add(btnNewButton);
		
		JButton button = new JButton("Browse");
		button.setBounds(292, 121, 117, 29);
		frame.getContentPane().add(button);
		
		JLabel lblStatus = new JLabel("Status");
		lblStatus.setBounds(17, 6, 61, 16);
		frame.getContentPane().add(lblStatus);
		
		JLabel statusLabel = new JLabel("Not Ready");
		statusLabel.setBounds(90, 6, 201, 16);
		frame.getContentPane().add(statusLabel);
		
		final JButton btnStartListening = new JButton("Start Listening");
		btnStartListening.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (btnStartListening.getText().equals("Start Listening")){
				btnStartListening.setText("Stop Listening");
				listeningThread = new Thread(new Runnable(){
					public void run(){
						while (true){
						try {
							serverSocket = new ServerSocket(Integer.parseInt(portNumberInput.getText()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				        System.out.println("Server Socket Established...");
				        System.out.println("Listening for clients...");
						try {
							client = serverSocket.accept();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				        System.out.println("Receiving...");
				        try{
				        ObjectInputStream put = new ObjectInputStream(
				                client.getInputStream());
		
				        String path = savePathInput.getText();
				        Date date = new Date() ;
				        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss") ;
				        String fileName =  dateFormat.format(date)+".mp4";
				        String filePath = path + "/" + fileName;
				        System.out.println("Saving File in : " + filePath);
				        File f = new File(filePath);
				        if (!f.exists()){
				        	f.createNewFile();
				        }
				        	FileOutputStream fis = new FileOutputStream(f);
		
				            byte[] buf = new byte[1024];
				            int read;
				            while ((read = put.read(buf, 0, 1024)) != -1) {
				                fis.write(buf, 0, read);
				                fis.flush();
				            }
		
				            System.out.println("File transfered...");
				            client.close();
				            serverSocket.close();
				            fis.close();
				        }
				        catch (Exception e2){
					        e2.printStackTrace();
				        }
						}
						}}
				);
				listeningThread.start();
				
			}
				else{
					try {
			            if(!serverSocket.isClosed())
			            	serverSocket.close();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					
					listeningThread.stop();
					btnStartListening.setText("Start Listening");
				}

			}
		});
		btnStartListening.setBounds(405, 302, 117, 29);
		frame.getContentPane().add(btnStartListening);
		
		portNumberInput = new JTextField();
		portNumberInput.setText("8888");
		portNumberInput.setBounds(339, 301, 70, 28);
		frame.getContentPane().add(portNumberInput);
		portNumberInput.setColumns(10);
		
		JLabel lblPortNumber = new JLabel("Port Number");
		lblPortNumber.setBounds(249, 307, 79, 16);
		frame.getContentPane().add(lblPortNumber);
	}
}
