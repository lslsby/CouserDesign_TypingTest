import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class SetJPanel extends JPanel {

	UserJPanel ujp;
	// 英文练习，还是中文练习
	JLabel testTypeJLabel;
	// 测试文章
	JLabel testArticleJLabel;
	// 测试时间
	JLabel testTimeJLabel;
	JRadioButton englishJRadioButton, chineseJRadioButton;
	// 选择测试文本下拉列表
	JComboBox<String> testArticleJComboBox;
	JTextField testTimeJTextField;
	JButton startJButton, resetJButton, returnJButton, importJButton;
	// 记录JComboBox中被选中的文件名，默认是astronauts.txt
	public String fileName = "./data/astronauts.txt";
	// 设置默认时间为五分钟
	public double setTime = 5;

	public TypingJPanel typingJPanel = null;

	public SetJPanel(UserJPanel ujp) {
		// 持有对方的引用，为了获得用户名和主面板
		this.ujp = ujp;
		// 使显示框位于整个界面的中间位置
		CardLayout setCardLayout = new CardLayout(200, 150);
		// 约束作用
		this.setLayout(setCardLayout);
		JPanel jp = new JPanel(new GridLayout(4, 3));
		// 约束组件的作用
		FlowLayout leftAlign = new FlowLayout(FlowLayout.LEFT, 3, 22);
		testTypeJLabel = new JLabel("测试类型:", SwingConstants.RIGHT);
		englishJRadioButton = new JRadioButton("英文练习");
		chineseJRadioButton = new JRadioButton("中文练习");
		englishJRadioButton.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(englishJRadioButton);
		bg.add(chineseJRadioButton);
		jp.add(testTypeJLabel);
		jp.add(englishJRadioButton);
		jp.add(chineseJRadioButton);

		testArticleJLabel = new JLabel("测试文章:", SwingConstants.RIGHT);
		JPanel jp1 = new JPanel(leftAlign);
		testArticleJComboBox = new JComboBox<String>();
		jp1.add(testArticleJComboBox);
		testArticleJComboBox.setEditable(false);
		// 设置最大显示7个，五个以上就出现下拉列表
		testArticleJComboBox.setMaximumRowCount(7);
		addEnglishArticleToJComboBox();
		JPanel jp2 = new JPanel(leftAlign);
		importJButton = new JButton("导入文章");
		jp2.add(importJButton);
		jp.add(testArticleJLabel);
		jp.add(jp1);
		jp.add(jp2);

		testTimeJLabel = new JLabel("测试时间:", SwingConstants.RIGHT);
		JPanel jp3 = new JPanel(leftAlign);
		testTimeJTextField = new JTextField(10);
		testTimeJTextField.setText("5");
		testTimeJTextField.setToolTipText("测试时间默认为五分钟");
		jp3.add(testTimeJTextField);
		jp.add(testTimeJLabel);
		jp.add(jp3);
		// 最后一个网格不够，补一个空标签
		jp.add(new JLabel(""));

		FlowLayout centerAlign = new FlowLayout(FlowLayout.CENTER, 3, 22);

		JPanel jp4 = new JPanel(centerAlign);
		startJButton = new JButton("开始");
		jp4.add(startJButton);
		JPanel jp5 = new JPanel(centerAlign);
		resetJButton = new JButton("重置");
		jp5.add(resetJButton);
		JPanel jp6 = new JPanel(centerAlign);
		returnJButton = new JButton("返回");
		jp6.add(returnJButton);
		jp.add(jp4);
		jp.add(jp5);
		jp.add(jp6);

		this.add(jp);

		setListenerForTestArticleJComboBox();
		setListenerForResetJButton();
		setListenerForStartJButton();
		setListenerForReturnJButton();
		setListenerForImportJButton();
	}

	public void addEnglishArticleToJComboBox() {

		File f = new File("./data");
		File[] files = f.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].toString().endsWith(".txt")) {
				testArticleJComboBox.addItem(files[i].getName().toString());
			}
		}
	}

	public void setListenerForTestArticleJComboBox() {
		testArticleJComboBox.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					fileName = "./data/"
							+ testArticleJComboBox.getSelectedItem();
				}
			}
		});
	}

	public void setListenerForResetJButton() {
		resetJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				englishJRadioButton.setSelected(true);
				testArticleJComboBox.setSelectedItem("astronauts.txt");
				testTimeJTextField.setText("5");
			}
		});
	}

	public void setListenerForStartJButton() {
		startJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setTime = Double.parseDouble(testTimeJTextField.getText());
				} catch (NumberFormatException e1) {
					JOptionPane.showMessageDialog(null,
							"您输入时间格式不对，请重新输入，时间以分钟为单位", "提示",
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				if (typingJPanel != null) {
					ujp.tt.mainJPanel.remove(typingJPanel);
				}
				typingJPanel = new TypingJPanel(SetJPanel.this);
				ujp.tt.mainJPanel.add("typingJPanel", typingJPanel);
				ujp.tt.cardLayout.show(ujp.tt.mainJPanel, "typingJPanel");
			}
		});
	}

	public void setListenerForReturnJButton() {
		returnJButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (typingJPanel != null) {
					ujp.tt.mainJPanel.remove(typingJPanel);
				}
				ujp.tt.cardLayout.show(ujp.tt.mainJPanel, "userJPanel");
			}
		});
	}

	String[] pureTextSuffix = { ".txt", ".ini", ".log", ".c", ".cpp", ".h",
			".hpp", ".mak", ".prj", "html", "htm", ".java", ".js", ".xml",
			".css", ".rb", "php" };

	public void setListenerForImportJButton() {
		importJButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				boolean isPureText = false;
				String getPath;
				fileChooser.setMultiSelectionEnabled(false);
				if (fileChooser.showOpenDialog(null) == JFileChooser.CANCEL_OPTION) {
					return;
				}
				getPath = fileChooser.getSelectedFile().toString();
				for (int i = 0; i < pureTextSuffix.length; i++) {
					if (getPath.endsWith(pureTextSuffix[i])) {
						isPureText = true;
						break;
					}
				}
				if (isPureText) {
					fileName = getPath;
					JOptionPane.showMessageDialog(null, "导入" + getPath
							+ "文件成功。", "提示", JOptionPane.INFORMATION_MESSAGE);
				} else {
					JOptionPane.showMessageDialog(null, "你没有选择纯文本文件，无法导入！",
							"警告", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.drawRect(220, 150, 400, 300);
		repaint();
	}

}
