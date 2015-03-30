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
	// ��ͣ�߳�����
	public PauseThread pauseThread = null;
	// �����õ�ʱ��
	public double spendTime;

	public Speed_TimeThread(InputActionListener inputAL) {
		this.inputAL = inputAL;
		// ��¼�û����õĲ���ʱ��
		this.setTime = inputAL.typingJP.sjp.setTime;

	}

	public void run() {
		// ÿ�����¿�ʼʱ��Ӧ���ϴ���ͣ��ʱ������
		PauseThread.pauseTime = 0;
		double speed = 0, remainTime;
		// ʣ��ʱ�������
		int remainMin = 0, remainSec = 0;
		currentTime = System.currentTimeMillis();
		spendTime = (currentTime - inputAL.startTime) / 60000;
		while (spendTime < setTime) {
			// �������������ò˵����������ʱ�߳�
			if (inputAL.typingJP.isReturn) {
				return;
			}
			// ������½������������ʱ
			if (inputAL.isArticleOver) {
				break;
			}
			// �����¿�ʼ���ֵ��ǣ�����ǰ���ڴ��ֵ��߳���ֹ
			if (inputAL.isAgainStart) {
				break;
			}
			// ������ͣ�Ƿ񱻵�����ж��Ƿ�Ҫ�����߳���������ǰ�߳�
			if (inputAL.isPause) {
				pauseThread = new PauseThread(this);
				pauseThread.start();
				try {
					// ����ͣ�̼߳��뵱ǰ�߳��У�ֱ����ͣ�߳̽�������ǰ�̲߳��ܵõ�ִ��
					pauseThread.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			// ��¼��ȷ�ĵ�����
			int count = 0;
			// �ж��Ƿ�ֹ�����ļ�ĩβ����ָ��Խ���쳣
			for (int i = 0; i <= inputAL.words + inputAL.length; i++) {
				if (inputAL.words + inputAL.length != inputAL.typingJP.wordlength
						&& inputAL.typingJP.wordJLabel[i].getForeground() == Color.GREEN) {
					count++;
				}
			}

			double accuracy = 0;
			// ��Ϊ�����ַ�ʱ����ȷ��Ϊ0����ֹ����ı�0�������쳣
			if ((inputAL.words + inputAL.length) != 0) {
				// �ڴ˴���1����Ϊlength�õ��ĳ����ǳ���ǰ�õ��ַ�֮��ĳ���
				accuracy = (int) (count * 1000 / (inputAL.words
						+ inputAL.length + 1)) / 10.0;
			}
			// ��ǰ�е��ַ������ϵ�ǰ�е��ַ�����������ʱ��
			speed = (inputAL.words + inputAL.length) / spendTime;
			remainTime = setTime - spendTime;
			remainMin = (int) remainTime;
			remainSec = (int) Math.round((remainTime - remainMin) * 60);
			inputAL.typingJP.speedJLabel.setText("�ٶ�: " + Math.round(speed)
					+ "��/��");
			inputAL.typingJP.accuracyJLabel.setText("��ȷ��: " + accuracy + "%");
			inputAL.typingJP.timeJLabel.setText("ʣ��ʱ��: " + remainMin + "��"
					+ remainSec + "��");
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentTime = System.currentTimeMillis();
			spendTime = (currentTime - inputAL.startTime - PauseThread.pauseTime) / 60000;

		}
		// �����߳̽�������Ϊʱ�����������˾�ִ��
		if (remainMin == 0 && remainSec == 1) {
			inputAL.typingJP.timeJLabel.setText("ʣ��ʱ��: " + "0��" + "0��");
			inputAL.typingJP.speedJLabel.setText("�ٶ�: " + Math.round(speed)
					+ "��/��");
		}

		showScore();
		// ��ʱ��������½�����ʱ���������ı�����򶼲��ɱ༭
		if (!inputAL.isAgainStart) {
			for (int i = 0; i < inputAL.typingJP.rows; i++) {
				inputAL.typingJP.inputJTextField[i].setEditable(false);
			}
			JOptionPane.showMessageDialog(null, "���Խ����������������������¿�ʼ�� ���߷���",
					"��ʾ", JOptionPane.INFORMATION_MESSAGE);
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
		// ���¼����������ʱ�䣬��ֹ��ǰ�߳���ֹ����ͣ�̻߳������У����������������ֱ�ӵ�����¿�ʼ�����𣩣���ɵ�ǰ���߳��ж�
		currentTime = System.currentTimeMillis();
		spendTime = (currentTime - inputAL.startTime - PauseThread.pauseTime) / 60000;

		int useMin = (int) spendTime;
		int useSec = (int) Math.round((spendTime - useMin) * 60);
		String useTime = useMin + "�� : " + useSec + "��";
		String message = userName + "\n" + speed + "\n" + accuracy + "\n"
				+ "ʹ��ʱ��: " + useTime;
		JOptionPane.showMessageDialog(null, message, "���Խ��",
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
				pw.println("�û���" + "\t\t" + "�ٶ�" + "\t\t" + "��ȷ��" + "\t\t"
						+ "ʹ��ʱ��" + "\t\t" + "����ʱ��");
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
