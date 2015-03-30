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
	// 定义一行有70个标签
	public final int COLS = 70;
	// 行数是根据文章的总的词数除以COLS得到的
	public int rows;
	// 文件中单词的总个数
	public int wordlength;
	public Font fontTest = new Font("楷体", Font.BOLD, 20);
	public Font fontInput = new Font("楷体", Font.BOLD, 20);
	// 用户输入错误时，字体显示的颜色
	public final Color ERRORCOLOR = Color.RED;
	// 正确时显示的字体颜色
	public final Color RIGHTCOLOR = Color.GREEN;
	// 在未输入时字体显示的颜色
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

	public String userName = "游客1111";

	InputActionListener inputActionListener;

	public TypingJPanel(SetJPanel sjp) {
		this.sjp = sjp;
		this.fileName = sjp.fileName;
		this.userName = sjp.ujp.userName;

		createJMenuBar();

		JPanel jp = new JPanel(new BorderLayout());
		JPanel topJPanel = new JPanel(new GridLayout(1, 5));
		userNameJLabel = new JLabel("用户名: " + userName, SwingConstants.LEFT);
		speedJLabel = new JLabel("速度:", SwingConstants.LEFT);
		accuracyJLabel = new JLabel("正确率:", SwingConstants.LEFT);
		timeJLabel = new JLabel("剩余时间:", SwingConstants.LEFT);
		pause_resumeJButton = new JButton("暂停");
		// 在开始测试之前设置暂停按钮为不可接受状态
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
		// 为暂停按钮注册事件监听
		setListenerForPause();
		// 为查询记录项注册事件监听
		setListenerForSearchJMenuItem();
		// 为重新开始项注册事件监听
		setListenerForRestartJMenuItem();
		// 为退出项注册事件监听
		setListenerForExitJMenuItem();
		// 为返回项注册事件监听
		setListenerForReturnJMenuItem();
		// 为帮助项注册事件监听
		setListenerForHelpJMenuItem();
	}

	public void createJMenuBar() {
		jmb = new JMenuBar();
		fileJMenu = new JMenu("文件");
		returnJMenuItem = new JMenuItem("返回");
		exitJMenuItem = new JMenuItem("退出");
		fileJMenu.add(returnJMenuItem);
		fileJMenu.add(exitJMenuItem);
		jmb.add(fileJMenu);

		setJMenu = new JMenu("设置");
		restartJMenuItem = new JMenuItem("重新开始");
		setJMenu.add(restartJMenuItem);
		jmb.add(setJMenu);

		searchJMenu = new JMenu("查询");
		searchJMenuItem = new JMenuItem("查询记录");
		searchJMenu.add(searchJMenuItem);
		jmb.add(searchJMenu);

		helpJMenu = new JMenu("帮助");
		helpJMenuItem = new JMenuItem("如何使用");
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
		// 根据排数来定中间面板的行数
		centerJPanel = new JPanel(new GridLayout(rows * 2, 1));
		wordJLabel = new JLabel[wordlength];
		displayJPanel = new JPanel[rows];
		inputJTextField = new JTextField[rows];
		// 对每行放置JLabel的JPanel的布局形式，只有一行，和COLS列
		GridLayout layout = new GridLayout(1, COLS, 0, 0);
		int count = 0;
		for (int i = 0; i < rows; i++) {
			displayJPanel[i] = new JPanel(layout);
			for (int j = 0; j < COLS; j++) {
				// 当单词全部被取出，如果本行不满COLS的列数，全部补上空JLabel
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
			// 将输入文本框放在面板里面，和上面的JLabel放在面板里面对应，不然会产生不匹配的问题
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
				if (jb.getText().equals("暂停")) {
					inputActionListener.isPause = true;
					pause_resumeJButton.setText("恢复");
					// 暂停后使各输入框处于不可编辑状态
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setEditable(false);
					}
				} else {
					inputActionListener.isPause = false;
					pause_resumeJButton.setText("暂停");
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setEditable(true);
						// 恢复之后找到当前要输入的文本框
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
					jt.setFont(new Font("楷体", Font.BOLD, 14));
					jt.setForeground(Color.BLUE);
					JOptionPane.showMessageDialog(null, jsp, "历史记录",
							JOptionPane.INFORMATION_MESSAGE);
					br.close();
					// 查询之后找到当前要输入的文本框
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
				int ok = JOptionPane.showConfirmDialog(null, "你确定要重新开始吗?",
						"重新开始确认", JOptionPane.YES_NO_OPTION);
				if (ok == JOptionPane.OK_OPTION) {
					// 当没有击任何键，就选择重新开始项，要把上一个事件正在监听第一个设为false，让其不要另起线程
					inputActionListener.isFirstKey = false;
					// 置重新开始判断为true
					inputActionListener.isAgainStart = true;
					// 所有的输入文本框要恢复处于可编辑状态和可编辑状态
					for (int i = 0; i < rows; i++) {
						inputJTextField[i].setText("");
						inputJTextField[i].setEditable(true);
						inputJTextField[i].setEnabled(true);
					}
					// 所有字体设置成正常的状态
					for (int i = 0; i < wordlength; i++) {
						wordJLabel[i].setForeground(PLAINCOLOR);
						if (wordJLabel[i].getText().equals(" ")) {
							wordJLabel[i].setBackground(wordJLabel[0]
									.getBackground());
						}
					}
					inputJTextField[0].requestFocusInWindow();
					// 重新设置暂停和恢复按钮为暂停状态
					pause_resumeJButton.setText("暂停");
					inputActionListener = new InputActionListener(
							TypingJPanel.this);
					// 更新滚动条的位置，滚动条置顶端
					jsp.getVerticalScrollBar().setValue(0);
					// 使滚动条设置生效
					jsp.getVerticalScrollBar().validate();
					jsp.getVerticalScrollBar().repaint();
				}
			}
		});
	}

	public void setListenerForExitJMenuItem() {
		exitJMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int exit = JOptionPane.showConfirmDialog(null, "你确定要退出吗！",
						"提示", JOptionPane.OK_CANCEL_OPTION);
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
						"你确认要返回吗?", "提示", JOptionPane.OK_CANCEL_OPTION);
				if (returnSure == JOptionPane.OK_OPTION) {
					// 返回时设置菜单栏不显示
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
								"1.进入用户设置界面，默认是以游客的身份进入，你可以设置自己的用户名，然后进入设置界面  \n"
										+ "2.进入设置界面，默认的选择方式是，英文打字测试(中文暂时没有实现)，astronauts文章，时间为5分钟，你可以设置的自己的时间。时间是以分钟为单位的。\n"
										+ "3.设置完成后，进入打字界面，按任何键开始打字练习，并开始计时。\n"
										+ "4.打字进行中，你可以点击暂停按钮来进行暂停，暂停后界面将不会接受用户的输入，点击恢复，界面接受用户输入\n"
										+ "5.打字测试进行中或打字完后，你想重新开始的话，你可以点击设置菜单里面重新开始项来进行重新开始\n"
										+ "6.你可以点击文件菜单里面的返回项回到用户设置界面来重新设置你的选项。\n"
										+ "7.当打字时间结束，文章结束，或者重新开始的时候，你的上一次打字测试结果将被保存，你可以点击查询菜单里面的查询记录来查看",
								"帮助", JOptionPane.INFORMATION_MESSAGE);
			}
		});
	}

}
