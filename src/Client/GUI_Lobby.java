package Client;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.jws.soap.SOAPBinding.Style;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;


public class GUI_Lobby extends JFrame implements ActionListener{

	private JPanel contentPane;
	private Handler handler;
	private JTextPane textPane;
	private JTextPane textPane_1;
	private javax.swing.text.Style style;
	private StyledDocument doc;
	private JScrollPane scrollPane;
	private JLabel lblPlayer;
	private JLabel lblIcon;
	private ImageIcon irdy = new ImageIcon(new ImageIcon(Main.class.getResource("check.gif")).getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH));
	private ImageIcon urdy = new ImageIcon(new ImageIcon(Main.class.getResource("cross.gif")).getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH));
	private JLabel[] playerLabels = new JLabel[12];
	private JButton btnSpielStarten;
	private JButton btnSenden = new JButton("Senden");

	/**
	 * Launch the application.
	 */
//	public static void main(String[] args) {
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
//				try {
//					GUI_Lobby frame = new GUI_Lobby();
//					frame.setVisible(true);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//	}

	/**
	 * Create the frame.
	 */
	public GUI_Lobby(Handler handler) {
		this.handler = handler;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 685, 518);
		buildWindow();
		
	}

	private void buildWindow() {
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		
		btnSenden.addActionListener(this);
		btnSenden.setBounds(474, 438, 185, 30);
		contentPane.add(btnSenden);
		
		btnSpielStarten = new JButton("Spiel starten");
		btnSpielStarten.setBounds(474, 392,  185, 30);
		contentPane.add(btnSpielStarten);
		btnSpielStarten.addActionListener(this);

		JLabel lblSpieler = new JLabel("Spieler");
		lblSpieler.setBounds(485, 10, 110, 14);
		contentPane.add(lblSpieler);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 426, 454, 42);
		contentPane.add(scrollPane_1);
		
		textPane_1 = new JTextPane();

		  int condition = JComponent.WHEN_FOCUSED;
		  InputMap iMap = textPane_1.getInputMap(condition);
		  ActionMap aMap = textPane_1.getActionMap();

		  String enter = "enter";
		  iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enter);
		  aMap.put(enter, new AbstractAction() {

		     @Override
		     public void actionPerformed(ActionEvent arg0) {
		        send();
		     }
		  });

		
		scrollPane_1.setViewportView(textPane_1);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 35, 454, 380);
		contentPane.add(scrollPane);
		
		textPane = new JTextPane();
		
		doc = textPane.getStyledDocument();
		
		style = textPane.addStyle("I'm a Style", null);


		scrollPane.setViewportView(textPane);
		textPane.setEditable(false);
	}
	
	public void send (){
		if (textPane_1.getText().equals("")) {
			
		}else{
		
		handler.sendChat(textPane_1.getText());
		textPane_1.setText("");
		}
	}
	
	public void setChat(String txt){
		String currentTxt = textPane.getText();
		if(currentTxt.equalsIgnoreCase(""))
		{
			textPane.setText(txt);
		} else {
			textPane.setText(textPane.getText()+"\n"+txt);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		
		if(src.equals(btnSenden))
		{
			send();
		} else if(src.equals(btnSpielStarten)) {
			handler.sendReady();
			btnSpielStarten.setEnabled(false);
		}
	}
}
