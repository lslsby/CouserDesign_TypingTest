public class PauseThread extends Thread {
	// ���徲̬�ģ���¼��ͣ����ʱ��
	public static double pauseTime = 0;

	public Speed_TimeThread stt;

	public PauseThread(Speed_TimeThread stt) {
		this.stt = stt;
	}

	public void run() {
		double s = System.currentTimeMillis();
		while (stt.inputAL.isPause) {
			// ���������ز˵���ֱ�ӷ���
			if (stt.inputAL.typingJP.isReturn) {
				return;
			}
			// �ж��Ƿ��������¿�ʼ��true��ʱ����ͣ�߳���ֹ��������ֱ�ӷ��أ����滹Ҫ����������Ҫ�õ���ͣʱ��
			if (stt.inputAL.isAgainStart) {
				break;
			}
		}
		double o = System.currentTimeMillis();
		pauseTime += o - s;
	}
}
