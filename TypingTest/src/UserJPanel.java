import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class UserJPanel extends JPanel {

	TypingTest tt = null;
	// ����1000��9999�������
	public int random = (int) (Math.random() * 9000) + 1000;

	public String userName;

	JLabel welcomJLabel, userNameJLabel;
	JTextField userNameJTextField;
	JButton submitJButton, resetJButton;

	public UserJPanel(TypingTest tt) {
		this.tt = tt;
		// ʹ��ʾ��λ������������м�λ��
		CardLayout userCardLayout = new CardLayout(200, 150);
		this.setLayout(userCardLayout);
		JPanel jp = new JPanel(new GridLayout(3, 1));
		welcomJLabel = new JLabel("���ֲ�����ϰ", SwingConstants.CENTER);
		welcomJLabel.setFont(new Font("����", Font.BOLD, 20));
		jp.add(welcomJLabel);
		JPanel jp1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		userNameJLabel = new JLabel("�û���:");
		userNameJTextField = new JTextField(12);
		userNameJTextField.setText("�ο�" + random);
		userNameJTextField.setToolTipText("Ĭ��������������ο͵�½�������趨�û���");
		jp1.add(userNameJLabel);
		jp1.add(userNameJTextField);
		jp.add(jp1);
		JPanel jp2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
		submitJButton = new JButton("�ύ");
		resetJButton = new JButton("����");
		jp2.add(submitJButton);
		jp2.add(resetJButton);
		jp.add(jp2);

		this.add(jp);

		setListenerForSubmitJButton();
		setListenerForResetJButton();
	}

	public void setListenerForSubmitJButton() {
		submitJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (userNameJTextField.getText() != null
						&& !userNameJTextField.getText().trim().equals("")) {
					userName = userNameJTextField.getText();
				} else {
					JOptionPane.showMessageDialog(null, "��û�������û��������û���Ϊ�գ�",
							"��ʾ", JOptionPane.OK_OPTION);
					return;
				}
				SetJPanel setJPanel = new SetJPanel(UserJPanel.this);
				tt.mainJPanel.add("setJPanel", setJPanel);
				tt.cardLayout.show(tt.mainJPanel, "setJPanel");
			}
		});
	}

	public void setListenerForResetJButton() {
		resetJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				random = (int) (Math.random() * 9000) + 1000;
				userNameJTextField.setText("�ο�" + random);
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.drawRect(220, 150, 400, 300);
		// �ػ���������֮����໥Ӱ��
		repaint();
	}

}
