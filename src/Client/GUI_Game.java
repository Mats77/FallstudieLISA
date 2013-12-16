package Client;

import java.awt.Color;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.Popup;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JTextPane;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;


public class GUI_Game extends JFrame implements ActionListener{

	private Handler handler;
	private int width = 800;
	private int heigth = 520;

	//Elements
	JLabel productionLabel = new JLabel("Produktion");
	JLabel marketingLabel = new JLabel("Marketing");
	JLabel feLabel = new JLabel("Entwicklung");
	JLabel preisLabel = new JLabel("Preis");
	
	JTextField productionTextfield = new JTextField();
	JTextField marketingTextfield = new JTextField();
	JTextField feTextfield = new JTextField();
	JTextField preisTextfield = new JTextField();
	
	JLabel marktAnteilFixLabel = new JLabel("Marktanteil: ");
	JLabel marktanteilValueLabel = new JLabel("25%");
	
	JButton sendValues = new JButton("Rundenwerte abegeben");
	
	public GUI_Game(final Handler handler) {

		this.handler = handler;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, width, heigth);
			
		buildWindow();

	}

	private void buildWindow() {
		setLayout(null);

		
		productionLabel.setBounds(50, 50, 200, 30);
		marketingLabel.setBounds(50, 100, 200, 30);
		feLabel.setBounds(50, 150, 200, 30);
		preisLabel.setBounds(50, 200, 200, 30);
		
		productionTextfield.setBounds(300, 50, 200,30);
		marketingTextfield.setBounds(300, 100, 200,30);
		feTextfield.setBounds(300, 150, 200,30);
		preisTextfield.setBounds(300, 200, 200,30);
		
		marktAnteilFixLabel.setBounds(50, 250, 200, 30);
		marktanteilValueLabel.setBounds(300, 250, 200, 30);
		
		sendValues.addActionListener(this);
		sendValues.setBounds(50, 300, 200, 30);
		
		add(productionLabel);
		add(marketingLabel);
		add(feLabel);
		add(preisLabel);
		add(productionTextfield);
		add(marketingTextfield);
		add(feTextfield);
		add(preisTextfield);
		add(marktAnteilFixLabel);
		add(marktanteilValueLabel);
		add(sendValues);
	}	
	
	public void send (){ //später für den ingame Chat
		if (productionLabel.getText().equals("")) {
			
		}else{
		
		handler.sendChat(productionLabel.getText());
		productionLabel.setText("");
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {  //kann bisher nur eingetragene Werte an den Server übermitteln
		Object src = e.getSource();
		
		if(src.equals(sendValues))
		{			
			handler.getConn().send("VALUES "+productionTextfield.getText()+";"+marketingTextfield.getText()+";"+
									feTextfield.getText()+";"+preisTextfield.getText());
		}
	}

}
