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
	// 判断是否重新开始
	public boolean isAgainStart = false;
	// 判断测试文章是否结束， 初始为false
	public boolean isArticleOver = false;
	// 记录是否按了暂停按钮，默认是没有按的状态
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
						.setText("按任何键开始计时，进入打字练习状态(Enter进入下一行)");
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

	// 当键入的是第一个按键时，另起一个线程，显示速度和时间和状态
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
		// 另起线程去显示速度和时间状态
		if (isFirstKey) {
			// 将第一行文本框的提示清空
			typingJP.inputJTextField[0].setText("");
			isFirstKey = false;
			stThread = new Speed_TimeThread(this);
			stThread.start();
			startTime = System.currentTimeMillis();
			// 设置暂停为可接受状态
			typingJP.pause_resumeJButton.setEnabled(true);
		}
		// 事件触发，保持当前输入框的引用
		jtf = (JTextField) e.getSource();
		// 记录触发事件文本框的行号
		int i = 0;
		for (; i < typingJP.rows; i++) {
			if (jtf == typingJP.inputJTextField[i]) {
				break;
			}
		}
		// 记录当前行之前所有行的字符数
		words = i * typingJP.COLS;
		// 返回当前输入框的字符数
		length = typingJP.inputJTextField[i].getText().length();

		if (words + length >= typingJP.wordlength - 1) {
			if (isArticleOver) {
				return;
			} else {
				isArticleOver = true;
				return;
			}
		}

		// 如果从键盘键入的是退格键，将当前位置到本行结束位置的字体颜色全部设置成正常
		if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
			// 退格时解除当前文本框不可编辑状态
			typingJP.inputJTextField[i].setEditable(true);
			int k;
			// 判断是不是处于本行开头，开头要特殊处理，不然会出现越界情况
			if (length == 0) {
				k = 0;
			} else {
				k = length - 1;
			}
			// 将由输入错误引起的恢复原来的状态
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
			// 更新滚动条的位置,滚动条的首选垂直高度为500，每页显示有13行
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

		// 当文本框达到列的最大数时，设定此文本框为不可编辑状态
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
				// 设置其为不透明的，如果为 true，则该组件绘制其边界内的所有像素
				typingJP.wordJLabel[words + length].setOpaque(true);
			}
		}
	}

}
