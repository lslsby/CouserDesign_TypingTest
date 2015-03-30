import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class TypingJPanel extends JPanel {
	// ����һ����70����ǩ
	public final int COLS = 70;
	// �����Ǹ������µ��ܵĴ�������COLS�õ���
	public int rows;
	// �ļ��е��ʵ��ܸ���
	public int wordlength;
	public Font fontTest = new Font("����", Font.BOLD, 20);
	public Font fontInput = new Font("����", Font.BOLD, 20);
	// �û��������ʱ��������ʾ����ɫ
	public final Color ERRORCOLOR = Color.RED;
	// ��ȷʱ��ʾ��������ɫ
	public final Color RIGHTCOLOR = Color.GREEN;
	// ��δ����ʱ������ʾ����ɫ
	public final Color PLAINCOLOR = Color.BLACK;

	public SetJPanel sjp;
	JMenuBar jmb;
	public JMenu fileJMenu, setJMenu, searchJMenu, helpJMenu;
	public JMenuItem exitJMenuItem, restartJMenuItem, returnJMenuItem,
			searchJMenuItem, helpJMenuItem;
	public JLabel userNameJLabel, speedJLabel, accuracyJLabel, timeJLabel;
	public JButton pause_resumeJButton;
	public JPanel centerJPanel;
	public JScrollPane jsp;

	public JLabel[] wordJLabel;
	public JPanel[] displayJPanel;
	public JTextField[] inputJTextField;

	public String fileName = "./data/astronauts.txt";
	public StringBuffer sb;

	public String userName = "�ο�1111";

	InputActionListener inputActionListener;

	public TypingJPanel(SetJPanel sjp) {
		this.sjp = sjp;
		this.fileName = sjp.fileName;
		this.userName = sjp.ujp.userName;

		createJMenuBar();

		JPanel jp = new JPanel(new BorderLayout());
		JPanel topJPanel = new JPanel(new GridLayout(1, 5));
		userNameJLabel = new JLabel("�û���: " + userName, SwingConstants.LEFT);
		speedJLabel = new JLabel("�ٶ�:", SwingConstants.LEFT);
		accuracyJLabel = new JLabel("��ȷ��:", SwingConstants.LEFT);
		timeJLabel = new JLabel("ʣ��ʱ��:", SwingConstants.LEFT);
		pause_resumeJButton = new JButton("��ͣ");
		// �ڿ�ʼ����֮ǰ������ͣ��ťΪ���ɽ���״̬
		pause_resumeJButton.setEnabled(false);
		JPanel jp_pause_resume = new JPanel(new CardLayout(40, 2));
		jp_pause_resume.add(pause_resumeJButton);
		topJPanel.add(userNameJLabel);
		topJPanel.add(speedJLabel);
		topJPanel.add(accuracyJLabel);
		topJPanel.add(timeJLabel);
		topJPanel.add(jp_pause_resume);
		jp.add(topJPanel, BorderLayout.NORTH);

		createCenterJPanel();
		jsp = new JScrollPane(centerJPanel);
		jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jsp.setWheelScrollingEnabled(true);
		jsp.setAutoscrolls(true);
		jsp.setPreferredSize(new Dimension(830, 500));
		jp.add(jsp, BorderLayout.CENTER);
		centerJPanel.revalidate();
		this.add(jp);
		inputActionListener = new InputActionListener(this);
		// Ϊ��ͣ��ťע���¼�����
		setListenerForPause();
		// Ϊ��ѯ��¼��ע���¼�����
		setListenerForSearchJMenuItem();
		// Ϊ���¿�ʼ��ע���¼�����
		setListenerForRestartJMenuItem();
		// Ϊ�˳���ע���¼�����
		setListenerForExitJMenuItem();
		// Ϊ������ע���¼�����
		setListenerForReturnJMenuItem();
		// Ϊ������ע���¼�����
		setListenerForHelpJMenuItem();
	}

	public void createJMenuBar() {
		jmb = new JMenuBar();
		fileJMenu = new JMenu("�ļ�");
		returnJMenuItem = new JMenuItem("����");
		exitJMenuItem = new JMenuItem("�˳�");
		fileJMenu.add(returnJMenuItem);
		fileJMenu.add(exitJMenuItem);
		jmb.add(fileJMenu);

		setJMenu = new JMenu("����");
		restartJMenuItem = new JMenuItem("���¿�ʼ");
		setJMenu.add(restartJMenuItem);
		jmb.add(setJMenu);

		searchJMenu = new JMenu("��ѯ");
		searchJMenuItem = new JMenuItem("��ѯ��¼");
		searchJMenu.add(searchJMenuItem);
		jmb.add(searchJMenu);

		helpJMenu = new JMenu("����");
		helpJMenuItem = new JMenuItem("���ʹ��");
		helpJMenu.add(helpJMenuItem);
		jmb.add(helpJMenu);

		sjp.ujp.tt.setJMenuBar(jmb);
	}

	public void createCenterJPanel() {
		Pattern pattern = Pattern.compile("[^\u0000-\u007F]");
		Matcher matcher;
		boolean isContainChinese;
		File file = new File(fileName);
		String str = null;
		sb = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					new FileInputStream(file)));
			while ((str = br.readLine()) != null) {
				isContainChinese = false;
				matcher = pattern.matcher(str);
				while (matcher.find()) {
					isContainChinese = true;
					break;
				}
				if (!isContainChinese) {
					sb.append(str);
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		wordlength = sb.length();

		if (wordlength % COLS == 0) {
			rows = wordlength / COLS;
		} else {
			rows = wordlength / COLS + 1;
		}
		// �������������м���������
		centerJPanel = new JPanel(new GridLayout(rows * 2, 1));
		wordJLabel = new JLabel[wordlength];
		displayJPanel = new JPanel[rows];
		inputJTextField = new JTextField[rows];
		// ��ÿ�з���JLabel��JPanel�Ĳ�����ʽ��ֻ��һ�У���COLS��
		GridLayout layout = new GridLayout(1, COLS, 0, 0);
		int count = 0;
		for (int i = 0; i < rows; i++) {
			displayJPanel[i] = new JPanel(layout);
			for (int j = 0; j < COLS; j++) {
				// ������ȫ����ȡ����������в���COLS��������ȫ�����Ͽ�JLabel
				if (count == wordlength) {
					for (int k = j; k < COLS; k++) {
						JLabel jL = new JLabel(" ");
						jL.setFont(fontTest);
						displayJPanel[i].add(jL);
					}
					break;
				}
				wordJLabel[count] = new JLabel(Character.toString(sb
						.charAt(count)));
				wordJLabel[count].setFont(fontTest);
				displayJPanel[i].add(wordJLabel[count]);
				count++;
			}
			inputJTextField[i] = new JTextField(COLS);
			JPanel jp1 = new JPanel();
			// �������ı������������棬�������JLabel������������Ӧ����Ȼ�������ƥ�������
			jp1.add(inputJTextField[i]);
			inputJTextField[i].setFont(fontInput);
			centerJPanel.add(displayJPanel[i]);
			centerJPanel.add(jp1);
		}
	}

	public void setListenerForPause() {
		pause_resumeJButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JButton jb = (JButton) e.getSource();
				if (jb.getText().equals("��ͣ")) {
					inputActionListener.isPause = true;
					pause_resumeJButton.setText("�ָ�");
					// ��ͣ��ʹ��������ڲ��ɱ༭״̬
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setEditable(false);
					}
				} else {
					inputActionListener.isPause = false;
					pause_resumeJButton.setText("��ͣ");
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setEditable(true);
						// �ָ�֮���ҵ���ǰҪ������ı���
						if (inputActionListener.jtf == inputJTextField[i]) {
							inputJTextField[i].requestFocusInWindow();
						}
					}
				}
			}
		});
	}

	public void setListenerForSearchJMenuItem() {
		searchJMenuItem.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				try {
					BufferedReader br = new BufferedReader(
							new InputStreamReader(new FileInputStream(
									"./data/record.dat")));
					StringBuffer buffer = new StringBuffer();
					String s = null;
					while ((s = br.readLine()) != null) {
						buffer.append(s + "\n");
					}
					JTextArea jt = new JTextArea(buffer.toString());
					JScrollPane jsp = new JScrollPane(jt);
					jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
					jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
					jsp.setPreferredSize(new Dimension(790, 500));
					jt.setEditable(false);
					jt.setFont(new Font("����", Font.BOLD, 14));
					jt.setForeground(Color.BLUE);
					JOptionPane.showMessageDialog(null, jsp, "��ʷ��¼",
							JOptionPane.INFORMATION_MESSAGE);
					br.close();
					// ��ѯ֮���ҵ���ǰҪ������ı���
					for (int i = 0; i < rows; i++) {
						if (inputActionListener.jtf == inputJTextField[i]) {
							inputJTextField[i].requestFocusInWindow();
						}
					}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	public void setListenerForRestartJMenuItem() {
		restartJMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int ok = JOptionPane.showConfirmDialog(null, "��ȷ��Ҫ���¿�ʼ��?",
						"���¿�ʼȷ��", JOptionPane.YES_NO_OPTION);
				if (ok == JOptionPane.OK_OPTION) {
					// ��û�л��κμ�����ѡ�����¿�ʼ�Ҫ����һ���¼����ڼ�����һ����Ϊfalse�����䲻Ҫ�����߳�
					inputActionListener.isFirstKey = false;
					// �����¿�ʼ�ж�Ϊtrue
					inputActionListener.isAgainStart = true;
					// ���е������ı���Ҫ�ָ����ڿɱ༭״̬�Ϳɱ༭״̬
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setText("");
						inputJTextField[i].setEditable(true);
						inputJTextField[i].setEnabled(true);
					}
					// �����������ó�������״̬
					for (int i = 0; i < wordlength; i++) {
						wordJLabel[i].setForeground(PLAINCOLOR);
						if (wordJLabel[i].getText().equals(" ")) {
							wordJLabel[i].setBackground(wordJLabel[0]
									.getBackground());
						}
					}
					inputJTextField[0].requestFocusInWindow();
					// ����������ͣ�ͻָ���ťΪ��ͣ״̬
					pause_resumeJButton.setText("��ͣ");
					inputActionListener = new InputActionListener(
							TypingJPanel.this);
					// ���¹�������λ�ã��������ö���
					jsp.getVerticalScrollBar().setValue(0);
					// ʹ������������Ч
					jsp.getVerticalScrollBar().validate();
					jsp.getVerticalScrollBar().repaint();
				}
			}
		});
	}

	public void setListenerForExitJMenuItem() {
		exitJMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int exit = JOptionPane.showConfirmDialog(null, "��ȷ��Ҫ�˳���",
						"��ʾ", JOptionPane.OK_CANCEL_OPTION);
				if (exit == JOptionPane.OK_OPTION) {
					System.exit(0);
				}
			}
		});
	}

	public boolean isReturn = false;

	public void setListenerForReturnJMenuItem() {
		returnJMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int returnSure = JOptionPane.showConfirmDialog(null,
						"��ȷ��Ҫ������?", "��ʾ", JOptionPane.OK_CANCEL_OPTION);
				if (returnSure == JOptionPane.OK_OPTION) {
					// ����ʱ���ò˵�������ʾ
					jmb.setVisible(false);
					isReturn = true;
					sjp.ujp.tt.cardLayout.show(sjp.ujp.tt.mainJPanel,
							"setJPanel");
				}
			}
		});
	}

	public void setListenerForHelpJMenuItem() {
		helpJMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(
								null,
								"1.�����û����ý��棬Ĭ�������ο͵���ݽ��룬����������Լ����û�����Ȼ��������ý���  \n"
										+ "2.�������ý��棬Ĭ�ϵ�ѡ��ʽ�ǣ�Ӣ�Ĵ��ֲ���(������ʱû��ʵ��)��astronauts���£�ʱ��Ϊ5���ӣ���������õ��Լ���ʱ�䡣ʱ�����Է���Ϊ��λ�ġ�\n"
										+ "3.������ɺ󣬽�����ֽ��棬���κμ���ʼ������ϰ������ʼ��ʱ��\n"
										+ "4.���ֽ����У�����Ե����ͣ��ť��������ͣ����ͣ����潫��������û������룬����ָ�����������û�����\n"
										+ "5.���ֲ��Խ����л��������������¿�ʼ�Ļ�������Ե�����ò˵��������¿�ʼ�����������¿�ʼ\n"
										+ "6.����Ե���ļ��˵�����ķ�����ص��û����ý����������������ѡ�\n"
										+ "7.������ʱ����������½������������¿�ʼ��ʱ�������һ�δ��ֲ��Խ���������棬����Ե����ѯ�˵�����Ĳ�ѯ��¼���鿴",
								"����", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

}
