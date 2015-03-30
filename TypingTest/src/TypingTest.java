import java.awt.CardLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class TypingTest extends JFrame {
	//主窗口面板
	JPanel  mainJPanel = null;
	//初始及用户名输入界面
	JPanel	userJPanel = null;	
	//窗口的总体布局
	CardLayout cardLayout = null;       
	
	public TypingTest() {
		super("打字练习");
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
