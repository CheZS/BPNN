package nn.bp;

/**
 * 学习样本数据结构
 * @author che
 *
 */
public class StudyData {
	public double[] input;		// 一个学习样本
	public double[] teach;		// 一个期望信号
	public int inNeural;		// 输入层神经元个数
	public int outNeural;		// 输出层神经元个数
	
	public StudyData(int inNeural, int outNeural) {
		this.inNeural = inNeural;
		this.outNeural = outNeural;
		this.input = new double[this.inNeural];
		this.teach = new double[this.outNeural];
	}
}
