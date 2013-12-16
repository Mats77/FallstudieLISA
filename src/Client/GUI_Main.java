package Client;

import java.awt.EventQueue;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class GUI_Main extends JFrame {

	private Handler handler = new Handler(this);
	private JFrame frame;

	/**
	 * Launch the application.
	 */

	public void connected() {
		frame.setVisible(false);
		frame = new GUI_Lobby(handler);
		frame.setVisible(true);
	}

	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					frame = new GUI_Connect(handler);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

	public JFrame getCurrentFrame() {
		return frame;
	}

	public void gamestarted() {
		frame.setVisible(false);
		frame = new GUI_Game(handler);
		frame.setVisible(true);
	}

}
