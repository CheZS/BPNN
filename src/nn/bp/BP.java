package nn.bp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class BP {
	
	private Builder builder;
	
	public StudyData[] StD;
	public double[] P;		// ����������������
	public double[] T;		// ����������ʦ����
	
	public double[][] W;	// �����������Ȩ��
	public double[][] V;	// �����������Ȩ��
	
	public double[] X;		// ��������
	public double[] Y;		// ���������
	public double[] H;		// �������
	public double[] O;		// ��������
	
	public double[] YU_HN;	// ������ֵ
	public double[] YU_ON;	// �������ֵ
	
	public double[] dErr;		// ��������
	public double[] eErr;		// ������������һ�㻯���
	public double[] errM;		// ��M�������������
	public double[] absErr;	// ÿ�������ľ������ ��0��ʼ
	
	public double[] oldDErr;
	public double[] oldEErr;
	
	public double[] gdErr;
	public double[] oldGdErr;
	public double[] geErr;
	public double[] oldGeErr;
	
	public double a;		// ������������ѧϰЧ��
	public double b;		// ������������ѧϰЧ��
	public double alpha;	// ����ϵ��
	
	public BP(Builder builder) {
		this.builder = builder;
		
		this.a = builder.a;
		this.b = builder.b;
		this.alpha = builder.alpha;
		
		this.StD = new StudyData[builder.N + builder.M];
		for (int i = 0; i < this.StD.length; i++) {
			this.StD[i] = new StudyData(builder.IN, builder.ON);
		}
		this.P = new double[builder.IN];
		this.T = new double[builder.ON];
		this.W = new double[builder.HN][builder.IN];
		for (int i = 0; i < this.W.length; i++) {
			this.W[i] = new double[builder.IN];
		}
		this.V = new double[builder.ON][builder.HN];
		for (int i = 0; i < this.V.length; i++) {
			this.V[i] = new double[builder.HN];
		}
		this.X = new double[builder.HN];
		this.Y = new double[builder.ON];
		this.H = new double[builder.HN];
		this.O = new double[builder.ON];
		this.YU_HN = new double[builder.HN];
		this.YU_ON = new double[builder.ON];
		this.dErr = new double[builder.ON];
		this.eErr = new double[builder.HN];
		this.errM = new double[builder.N];
		this.absErr = new double[builder.ON];
		this.oldDErr = new double[builder.ON];
		this.oldEErr = new double[builder.HN];
		this.gdErr = new double[builder.N];
		this.oldGdErr = new double[builder.N];
		this.geErr = new double[builder.N];
		this.oldGeErr = new double[builder.N];
		
		/* ����Ȩֵ����ֵ��ʼ�� */
		Random random = new Random(new Date().getTime());
		for (int i = 0; i < W.length; i++) {
			for (int j = 0; j < W[0].length; j++) {
				this.W[i][j] = random.nextDouble() * 0.1;
			}
		}
		for (int i = 0; i < V.length; i++) {
			for (int j = 0; j < V[0].length; j++) {
				this.V[i][j] = random.nextDouble() * 0.1;
			}
		}
		for (int i = 0; i < this.YU_HN.length; i++) {
			this.YU_HN[i] = random.nextDouble() * 0.1;
		}
		for (int i = 0; i < this.YU_ON.length; i++) {
			this.YU_ON[i] = random.nextDouble() * 0.1;
		}
		Arrays.fill(this.oldEErr, 0);
		Arrays.fill(this.oldDErr, 0);
		Arrays.fill(this.gdErr, 0);
		Arrays.fill(this.oldGdErr, 0);
		Arrays.fill(this.geErr, 0);
		Arrays.fill(this.oldGeErr, 0);
		
		/* read data */
		this.getData(this.builder.inputFileName);
		this.normalization();
	}
	
	/**
	 * ���ı��л�ȡѵ������
	 * @param fileName
	 */
	public void getData(String fileName) {
		FileInputStream fstream = null;
		DataInputStream in = null;
		BufferedReader br = null;
		try {
			fstream = new FileInputStream("resources\\" + fileName);
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
					this.StD[m].teach[i] = Double.parseDouble(records[i + IN]);
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
	 * ��һ��
	 */
	public void normalization() {
		double dMax;
		double dMin;
		int IN = this.builder.IN;
		int ON = this.builder.ON;
		int stdLen = this.StD.length;
		for (int i = 0; i < IN; i++) {
			dMax = Double.MIN_VALUE;
			dMin = Double.MAX_VALUE;
			for (int j = 0; j < stdLen; j++) {
				double value = this.StD[j].input[i];
				if (value > dMax) {
					dMax = value;
				} else if (value < dMin) {
					dMin = value;
				}
			}
			for (int j = 0; j < stdLen; j++) {
				if (dMax == dMin) {
					this.StD[j].input[i] = 1;
				} else {
					this.StD[j].input[i] = (this.StD[j].input[i] - dMin) / (dMax - dMin);
				}
			}
		}
		
		for (int j = 0; j < stdLen; j++) {
			for (int i = 0; i < ON; i++) {
				this.StD[j].teach[i] /= 1000.0;
			}
		}
	}
	
	/**
	 * ����ѧϰ+��������
	 * @param study
	 */
	public void input_P_T(StudyData study) {
		int IN = this.builder.IN;
		int ON = this.builder.ON;
		for (int i = 0; i < IN; i++) {
			this.P[i] = study.input[i];
		}
		for (int i = 0; i < ON; i++) {
			this.T[i] = study.teach[i];
//			System.out.println("!!!T[" + i + "]: " + this.T[i] + study.teach[i]);
		}
	}
	
	/**
	 * ��������
	 */
	public void H_I_O() {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int i = 0; i < HN; i++) {
			double sigma = 0.0;
			for (int j = 0; j < IN; j++) {
				sigma += this.W[i][j] * this.P[j];			// ��������ڻ�
			}
			this.X[i] = sigma - this.YU_HN[i];
			this.H[i] = 1.0 / (1.0 + Math.exp(-this.X[i]));	// �������O[k]���
		}
	}
	
	/**
	 * ���������
	 */
	public void O_I_O() {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			double sigma = 0.0;
			for (int j = 0; j < HN; j++) {
				sigma += this.V[k][j] * this.H[j];		// ��������ڻ�
			}
			double tmp = sigma - this.YU_ON[k];
			this.O[k] = 1.0 / (1.0 + Math.exp(-tmp));	// �������O[k]���
		}
	}
	
	/**
	 * ����ӳ���
	 * @param m
	 */
	public void Error(int m) {
		double sqrErr = 0;			// ÿ��������ƽ�������㶼�Ǵ�0��ʼ
		int ON = this.builder.ON;
		for (int k = 0; k < ON; k++) {
			this.absErr[k] = this.O[k] - this.T[k];
			sqrErr += this.absErr[k] * this.absErr[k];	// ���m��������������ƽ�����
			this.oldDErr[k] = this.dErr[k];
			this.dErr[k] = this.absErr[k] * this.O[k] * (1 - this.O[k]);
		}
		
		this.errM[m] = sqrErr / 2;	// ��m��������������ƽ�����
	}
	
	/**
	 * ����ӳ���
	 * @param m
	 */
	public void Err_H_I(int m) {
		int HN = this.builder.HN;
		int ON = this.builder.ON;
		for (int j = 0; j < HN; j++) {
			double sigma = 0;
			for (int k = 0; k < ON; k++) {
				sigma += this.dErr[k] * this.V[k][j];
			}
			this.oldEErr[j] = this.eErr[j];
			this.eErr[j] = sigma * this.H[j] * (1 - this.H[j]);	// eErr[j]Ϊ�ֲ��ݶ�  �������Ԫ��һ�㻯���
		}
	}
	
	/**
	 * ������������Ȩֵ��������
	 * @param m
	 */
	public void Delta_O_H(int m) {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			for (int j = 0; j < HN; j++) {
				this.V[k][j] -= this.a * (this.dErr[k] * this.H[j] + 0.0001 * this.V[k][j]);	// ������������Ȩֵ����
			}
			this.YU_ON[k] -= this.a * this.dErr[k];		// ��������������ֵ����
		}
	}
	
	/**
	 * ������������Ȩֵ������������ֵ���������ӳ���
	 * @param m
	 */
	public void Delta_H_I(int m) {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int j = 0; j < HN; j++) {
			for(int i = 0; i < IN; i ++) {
				this.W[j][i] -= this.b * (this.eErr[j] * this.P[i] + 0.0001 * this.W[j][i]);	// ������������Ȩֵ����
			}
			this.YU_HN[j] -= this.b * this.eErr[j]; //	��������������ֵ����
		}
	}
	
	/**
	 * N��������ȫ���������ӳ���Batch Learning ����ѧϰ��
	 * @param m
	 */
	public double ErrSum() {
		int N = this.builder.N;
		double totalErr = 0;
	    for(int m = 0; m < N; m++) {
	        totalErr += this.errM[m];	// ÿ�������ľ�����������Ϊȫ�����
	    }
	    return totalErr;
	}
	
	/**
	 * ���ĵ���
	 * @param wFile
	 * @param yFile
	 */
	public void save(String wFile, String yFile) {
		// TODO
	}
}
