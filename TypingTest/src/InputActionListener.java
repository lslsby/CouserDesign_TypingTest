import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JTextField;

import sun.audio.*;

public class InputActionListener extends KeyAdapter {

	public TypingJPanel typingJP;

	public double startTime;

	public int words;
	public int length;

	JTextField jtf;
	Speed_TimeThread stThread;
	// �ж��Ƿ����¿�ʼ
	public boolean isAgainStart = false;
	// �жϲ��������Ƿ������ ��ʼΪfalse
	public boolean isArticleOver = false;
	// ��¼�Ƿ�����ͣ��ť��Ĭ����û�а���״̬
	public boolean isPause = false;

	public InputActionListener(TypingJPanel typingJP) {
		this.typingJP = typingJP;
		typingJP.centerJPanel.requestFocusInWindow();
		typingJP.inputJTextField[0].requestFocusInWindow();
		setListener();
		for (int i = 0; i < typingJP.inputJTextField.length; i++) {
			if (i == 0) {
				typingJP.inputJTextField[i].requestFocusInWindow();
				typingJP.inputJTextField[i]
						.setText("���κμ���ʼ��ʱ�����������ϰ״̬(Enter������һ��)");
				typingJP.inputJTextField[i].setEnabled(true);
			} else {
				typingJP.inputJTextField[i].setEnabled(false);
			}
		}
	}

	public void setListener() {
		for (int i = 0; i < typingJP.inputJTextField.length; i++) {
			typingJP.inputJTextField[i].addKeyListener(this);
		}
	}

	// ��������ǵ�һ������ʱ������һ���̣߳���ʾ�ٶȺ�ʱ���״̬
	boolean isFirstKey = true;

	public void keyPressed(KeyEvent e) {

		try {
			InputStream isClick = new FileInputStream("./data/Click.wav");
			AudioStream clickSound = new AudioStream(isClick);
			AudioPlayer.player.start(clickSound);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		// �����߳�ȥ��ʾ�ٶȺ�ʱ��״̬
		if (isFirstKey) {
			// ����һ���ı������ʾ���
			typingJP.inputJTextField[0].setText("");
			isFirstKey = false;
			stThread = new Speed_TimeThread(this);
			stThread.start();
			startTime = System.currentTimeMillis();
			// ������ͣΪ�ɽ���״̬
			typingJP.pause_resumeJButton.setEnabled(true);
		}
		// �¼����������ֵ�ǰ����������
		jtf = (JTextField) e.getSource();
		// ��¼�����¼��ı�����к�
		int i = 0;
		for (; i < typingJP.rows; i++) {
			if (jtf == typingJP.inputJTextField[i]) {
				break;
			}
		}
		// ��¼��ǰ��֮ǰ�����е��ַ���
		words = i * typingJP.COLS;
		// ���ص�ǰ�������ַ���
		length = typingJP.inputJTextField[i].getText().length();

		if (words + length >= typingJP.wordlength - 1) {
			if (isArticleOver) {
				return;
			} else {
				isArticleOver = true;
				return;
			}
		}

		// ����Ӽ��̼�������˸��������ǰλ�õ����н���λ�õ�������ɫȫ�����ó�����
		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			// �˸�ʱ�����ǰ�ı��򲻿ɱ༭״̬
			typingJP.inputJTextField[i].setEditable(true);
			int k;
			// �ж��ǲ��Ǵ��ڱ��п�ͷ����ͷҪ���⴦����Ȼ�����Խ�����
			if (length == 0) {
				k = 0;
			} else {
				k = length - 1;
			}
			// ���������������Ļָ�ԭ����״̬
			for (; k < typingJP.COLS; k++) {
				if (words + k < typingJP.wordlength) {
					typingJP.wordJLabel[words + k]
							.setForeground(typingJP.PLAINCOLOR);
					typingJP.wordJLabel[words + k]
							.setBackground(typingJP.wordJLabel[0]
									.getBackground());
				}
			}
			return;
		}

		if (e.getKeyChar() == KeyEvent.VK_ENTER) {
			// ���¹�������λ��,����������ѡ��ֱ�߶�Ϊ500��ÿҳ��ʾ��13��
			typingJP.jsp.getVerticalScrollBar().setValue(i * 76);
			typingJP.jsp.getVerticalScrollBar().revalidate();
			typingJP.jsp.getVerticalScrollBar().repaint();
			if (i == typingJP.rows - 1) {
				return;
			}
			typingJP.inputJTextField[i + 1].requestFocusInWindow();
			typingJP.inputJTextField[i + 1].setEnabled(true);
			typingJP.inputJTextField[i].setEnabled(false);
			int k = length;
			for (; k < typingJP.COLS; k++) {
				typingJP.wordJLabel[words + k]
						.setForeground(typingJP.ERRORCOLOR);
				if (typingJP.wordJLabel[words + k].getText().equals(" ")) {
					typingJP.wordJLabel[words + k]
							.setBackground(typingJP.ERRORCOLOR);
					typingJP.wordJLabel[words + k].setOpaque(true);
				}
			}
			return;
		}

		// ���ı���ﵽ�е������ʱ���趨���ı���Ϊ���ɱ༭״̬
		if (length == typingJP.COLS) {
			typingJP.inputJTextField[i].setEditable(false);
			return;
		}

		if (typingJP.sb.charAt(words + length) == (char) e.getKeyChar()) {
			typingJP.wordJLabel[words + length]
					.setForeground(typingJP.RIGHTCOLOR);

		} else {
			typingJP.wordJLabel[words + length]
					.setForeground(typingJP.ERRORCOLOR);
			if (typingJP.wordJLabel[words + length].getText().equals(" ")) {
				typingJP.wordJLabel[words + length]
						.setBackground(typingJP.ERRORCOLOR);
				// ������Ϊ��͸���ģ����Ϊ true��������������߽��ڵ���������
				typingJP.wordJLabel[words + length].setOpaque(true);
			}
		}
	}

}
