package nn.basic;

/**
 * ѧϰ�������ݽṹ
 * @author che
 *
 */
public class StudyData {
	public double[] input;		// һ��ѧϰ����
	public double[] teach;		// һ�������ź�
	public int inNeural;		// �������Ԫ����
	public int outNeural;		// �������Ԫ����
	
	public StudyData(int inNeural, int outNeural) {
		this.inNeural = inNeural;
		this.outNeural = outNeural;
		this.input = new double[this.inNeural];
		this.teach = new double[this.outNeural];
	}
}
