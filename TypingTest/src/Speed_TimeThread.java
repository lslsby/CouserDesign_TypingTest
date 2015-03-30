import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JOptionPane;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class Speed_TimeThread extends Thread {

	public InputActionListener inputAL;

	public double setTime;

	public double currentTime;
	// 暂停线程引用
	public PauseThread pauseThread = null;
	// 测速用的时间
	public double spendTime;

	public Speed_TimeThread(InputActionListener inputAL) {
		this.inputAL = inputAL;
		// 记录用户设置的测试时间
		this.setTime = inputAL.typingJP.sjp.setTime;

	}

	public void run() {
		// 每次重新开始时，应把上次暂停的时间清零
		PauseThread.pauseTime = 0;
		double speed = 0, remainTime;
		// 剩余时间和秒数
		int remainMin = 0, remainSec = 0;
		currentTime = System.currentTimeMillis();
		spendTime = (currentTime - inputAL.startTime) / 60000;
		while (spendTime < setTime) {
			// 如果点击返回设置菜单，则结束计时线程
			if (inputAL.typingJP.isReturn) {
				return;
			}
			// 如果文章结束，则结束计时
			if (inputAL.isArticleOver) {
				break;
			}
			// 当重新开始打字的是，将当前正在打字的线程终止
			if (inputAL.isAgainStart) {
				break;
			}
			// 根据暂停是否被点击，判断是否要另起线程来阻塞当前线程
			if (inputAL.isPause) {
				pauseThread = new PauseThread(this);
				pauseThread.start();
				try {
					// 将暂停线程加入当前线程中，直到暂停线程结束，当前线程才能得到执行
					pauseThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// 记录正确的单词数
			int count = 0;
			// 判断是防止到达文件末尾产生指针越界异常
			for (int i = 0; i <= inputAL.words + inputAL.length; i++) {
				if (inputAL.words + inputAL.length != inputAL.typingJP.wordlength
						&& inputAL.typingJP.wordJLabel[i].getForeground() == Color.GREEN) {
					count++;
				}
			}

			double accuracy = 0;
			// 当为输入字符时，正确率为0，防止下面的被0除产生异常
			if ((inputAL.words + inputAL.length) != 0) {
				// 在此处加1是因为length得到的长度是除当前得到字符之外的长度
				accuracy = (int) (count * 1000 / (inputAL.words
						+ inputAL.length + 1)) / 10.0;
			}
			// 当前行的字符数加上当前行的字符数，除以总时间
			speed = (inputAL.words + inputAL.length) / spendTime;
			remainTime = setTime - spendTime;
			remainMin = (int) remainTime;
			remainSec = (int) Math.round((remainTime - remainMin) * 60);
			inputAL.typingJP.speedJLabel.setText("速度: " + Math.round(speed)
					+ "字/分");
			inputAL.typingJP.accuracyJLabel.setText("正确率: " + accuracy + "%");
			inputAL.typingJP.timeJLabel.setText("剩余时间: " + remainMin + "分"
					+ remainSec + "秒");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentTime = System.currentTimeMillis();
			spendTime = (currentTime - inputAL.startTime - PauseThread.pauseTime) / 60000;

		}
		// 当此线程结束是因为时间结束引起，则此句执行
		if (remainMin == 0 && remainSec == 1) {
			inputAL.typingJP.timeJLabel.setText("剩余时间: " + "0分" + "0秒");
			inputAL.typingJP.speedJLabel.setText("速度: " + Math.round(speed)
					+ "字/分");
		}

		showScore();
		// 当时间或者文章结束的时候让所得文本输入框都不可编辑
		if (!inputAL.isAgainStart) {
			for (int i = 0; i < inputAL.typingJP.rows; i++) {
				inputAL.typingJP.inputJTextField[i].setEditable(false);
			}
			JOptionPane.showMessageDialog(null, "测试结束，请在设置里面点击重新开始， 或者返回",
					"提示", JOptionPane.INFORMATION_MESSAGE);
		}
		inputAL.typingJP.pause_resumeJButton.setEnabled(false);
	}

	public void showScore() {
		try {
			InputStream isTheend = new FileInputStream("./data/Theend.wav");
			AudioStream theendSound = new AudioStream(isTheend);
			AudioPlayer.player.start(theendSound);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String userName = inputAL.typingJP.userNameJLabel.getText();
		String speed = inputAL.typingJP.speedJLabel.getText();
		String accuracy = inputAL.typingJP.accuracyJLabel.getText();
		// 重新计算测速所用时间，防止当前线程终止，暂停线程还在运行（此现象可能是由于直接点击重新开始项引起），造成当前的线程中断
		currentTime = System.currentTimeMillis();
		spendTime = (currentTime - inputAL.startTime - PauseThread.pauseTime) / 60000;

		int useMin = (int) spendTime;
		int useSec = (int) Math.round((spendTime - useMin) * 60);
		String useTime = useMin + "分 : " + useSec + "秒";
		String message = userName + "\n" + speed + "\n" + accuracy + "\n"
				+ "使用时间: " + useTime;
		JOptionPane.showMessageDialog(null, message, "测试结果",
				JOptionPane.INFORMATION_MESSAGE);

		saveScore(userName, speed, accuracy, useTime);
	}

	public void saveScore(String userName, String speed, String accuracy,
			String useTime) {
		File file = new File("./data/record.dat");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd E HH:mm:ss");
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(file, true), true);
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			if (br.readLine() == null) {
				pw.println("用户名" + "\t\t" + "速度" + "\t\t" + "正确率" + "\t\t"
						+ "使用时间" + "\t\t" + "测试时间");
			}
			pw.append(userName.substring(5) + "\t\t" + speed.substring(4)
					+ "\t\t" + accuracy.substring(5) + "\t\t" + useTime
					+ "\t\t" + dateFormat.format(new Date()) + "\n");
			pw.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
