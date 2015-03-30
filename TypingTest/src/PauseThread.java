public class PauseThread extends Thread {
	// 定义静态的，记录暂停的总时间
	public static double pauseTime = 0;

	public Speed_TimeThread stt;

	public PauseThread(Speed_TimeThread stt) {
		this.stt = stt;
	}

	public void run() {
		double s = System.currentTimeMillis();
		while (stt.inputAL.isPause) {
			// 如果点击返回菜单，直接返回
			if (stt.inputAL.typingJP.isReturn) {
				return;
			}
			// 判断是否点击了重新开始，true的时候将暂停线程终止，但不能直接返回，后面还要保存结果，需要用到暂停时间
			if (stt.inputAL.isAgainStart) {
				break;
			}
		}
		double o = System.currentTimeMillis();
		pauseTime += o - s;
	}
}
