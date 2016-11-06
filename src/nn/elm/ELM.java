package nn.elm;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

public class ELM {

	public Builder builder;
	
	public StudyData[] StD;
	public double[] P;		// ����������������
	public double[] T;		// ����������ʦ����
	
	public double[][] W;	// �����������Ȩֵ
	public double[] b;		// �������ֵ
	
	public double[][] belta;// �����������Ȩֵ
	
	public double[] X;		// ���������
	public double[] Y;		// ����������
	public double[] H;		// ��������
	public double[] O;		// ���������
	
	public double[] errM;	// ��m�������������
	
	public ELM(Builder builder) {
		this.builder = builder;
		Random random = new Random(new Date().getTime());
		
		int N = builder.N;
		int M = builder.M;
		int IN = builder.IN;
		int ON = builder.ON;
		int HN = builder.HN;
		
		this.StD = new StudyData[N + M];
		for (int i = 0; i < this.StD.length; i++) {
			this.StD[i] = new StudyData(IN);
		}
		this.P = new double[IN];
		this.T = new double[ON];
		this.belta = new double[HN][ON];
		for (int i = 0; i < HN; i++) {
			this.belta[i] = new double[ON];
		}
		this.X = new double[HN];
		this.Y = new double[ON];
		this.H = new double[HN];
		this.O = new double[ON];
		this.errM = new double[N];
		/* ����Ȩֵ����ֵ��ʼ�� */
		this.W = new double[HN][IN];
		for (int i = 0; i < HN; i++) {
			this.W[i] = new double[IN];
			for (int j = 0; j < IN; j++) {
				this.W[i][j] = random.nextDouble() - 0.5;	// ��ʼ������㵽�����Ȩֵ�����ģ��-0.5��0.5֮���ֵ
			}
		}
		this.b = new double[HN];
		for (int i = 0; i < HN; i++) {
			this.b[i] = random.nextDouble() - 0.5;			// ������ֵ��ʼ��
		}
	}
	
	/**
	 * ��ȡѵ������
	 */
	public void getTrainingData(String fileName) {
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream("resources/" + fileName);
			in = new DataInputStream(fstream);
			br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int len = this.builder.N + this.builder.M;
			int IN = this.builder.IN;
			int ON = this.builder.ON;
			for (int m = 0; (strLine = br.readLine()) != null && m < len; m++) {
				String[] records = strLine.split("	");
				for (int i = 0; i < IN; i++) {
					this.StD[m].input[i] = Double.parseDouble(records[i]);
				}
				for (int i = 0; i < ON; i++) {
					this.StD[m].teach = Double.parseDouble(records[i + IN]);
				}
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				in.close();
				fstream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ��ȡ��������
	 */
	public void getTestData() {
		
	}
	
	/**
	 * ��һ��
	 * @param X
	 */
	public void normalization(int X) {
		int IN = this.builder.IN;
		double[] dMAX = new double[IN];
		double[] dMIN = new double[IN];
		for (int i = 0; i < IN; i++) {
			dMAX[i] = Double.MIN_VALUE;
			dMIN[i] = Double.MAX_VALUE;
		}
		for (int i = 0; i < IN; i++) {
			for (int j = 0; j < X; j++) {
				if (this.StD[j].input[i] > dMAX[i]) {
					dMAX[i] = this.StD[j].input[i];
				}else if (this.StD[j].input[i] < dMIN[i]) {
					dMIN[i] = this.StD[j].input[i];
				}
			}
		}
		for (int i = 0; i < IN; i ++) {
			for (int j = 0; j < X; j ++) {
	            if (dMAX[i] - dMIN[i] == 0) {
	                this.StD[j].input[i] = dMIN[i];
	            } else {
	                this.StD[j].input[i] = (this.StD[j].input[i] - dMIN[i]) / (dMAX[i] - dMIN[i]);
	            }
	        }
		}
	}
	
	/**
	 * ��m��ѧϰ���������ӳ���
	 * @param m
	 */
	public void input_P(int m) {
		int IN = this.builder.IN;
		for (int i = 0; i < IN; i++) {
			this.P[i] = this.StD[m].input[i];
		}
	}
	
	/**
	 * ��m�����������ź��ӳ���
	 * @param m
	 */
	public void input_T(int m) {
		int ON = this.builder.ON;
		for (int i = 0; i < ON; i++) {
			this.T[i] = this.StD[m].teach;
		}
	}
	
	/**
	 * �������Ԫ���롢���ֵ�ӳ���
	 */
	public void H_I_O() {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int i = 0; i < HN; i++) {
			double sigma = 0;
			for (int j = 0; j < IN; j++) {
				sigma += this.W[i][j] * this.P[j];	// ��������ڻ�
			}
			double x = sigma + this.b[i];			// �������O[k]���
			this.X[i] = x;
			this.H[i] = 1 / (1 + Math.exp(-x));
		}
	}
	
	/**
	 * ��������Ԫ���롢���ֵ�ӳ���
	 */
	public void O_I_O() {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			double sigma = 0;
			for (int j = 0; j < HN; j++) {
				sigma += this.belta[j][k] * this.H[j];	// ��������ڻ�
			}
			this.O[k] = sigma;						// �������O[k]���
		}
	}
	
	/**
	 * N��������ȫ��������   Batch Leaning ����ѧϰ��
	 * @return
	 */
	public double errSum() {
		int N = this.builder.N;
		int ON = this.builder.ON;
		
		double[] errM = new double[N];			// ��m�������������
		double sqrErr = 0;						// ÿ��������ƽ�������㶼�Ǵ�0��ʼ
		double totalErr = 0;
		for (int m = 0; m < N; m++) {
			for (int k = 0; k < ON; k++) {
				errM[k] = this.T[k] - this.O[k];
				sqrErr += errM[k] * errM[k];	// ���m��������������ƽ�����
			}
			errM[m] = sqrErr / 2;				// ��m��������������ƽ�����
			totalErr += errM[m];
		}
		return totalErr;
	}
	
	/**
	 * ����Ȩֵ���������
	 */
	public void save() {
		// TODO
	}
}
