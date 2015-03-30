import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TypingTest extends JFrame {
	//���������
	JPanel  mainJPanel = null;
	//��ʼ���û����������
	JPanel	userJPanel = null;	
	//���ڵ����岼��
	CardLayout cardLayout = null;       
	
	public TypingTest() {
		super("������ϰ");
		cardLayout = new CardLayout();
		mainJPanel = new JPanel(cardLayout);
		userJPanel = new UserJPanel(this);
		mainJPanel.add("userJPanel", userJPanel);
		
		this.setContentPane(mainJPanel);
		cardLayout.show(mainJPanel, "userJPanel");
		
		this.setBounds(300, 50, 850, 600);
		this.setVisible(true);
		this.setResizable(false);
		
		this.addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				System.exit(0);
			}
		});
	}

	public static void main(String[] args) {
		TypingTest tt = new TypingTest();
	}

}
