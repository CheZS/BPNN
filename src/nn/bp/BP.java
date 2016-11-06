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

import nn.basic.StudyData;

public class BP {
	
	private Builder builder;
	
	public StudyData[] StD;
	public double[] P;		// 单个样本输入数据
	public double[] T;		// 单个样本教师数据
	
	public double[][] W;	// 输入层至隐层权重
	public double[][] V;	// 隐层至输出层权重
	
	public double[] X;		// 隐层输入
	public double[] Y;		// 输出层输入
	public double[] H;		// 隐层输出
	public double[] O;		// 输出层输出
	
	public double[] YU_HN;	// 隐层阈值
	public double[] YU_ON;	// 输出层阈值
	
	public double[] dErr;		// 输出层误差
	public double[] eErr;		// 隐层至输出层的一般化误差
	public double[] errM;		// 第M个样本的总误差
	public double[] absErr;	// 每个样本的绝对误差 从0开始
	
	public double[] oldDErr;
	public double[] oldEErr;
	
	public double[] gdErr;
	public double[] oldGdErr;
	public double[] geErr;
	public double[] oldGeErr;
	
	public double a;		// 输出层至隐层的学习效率
	public double b;		// 隐层至输入层的学习效率
	public double alpha;	// 动量系数
	
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
		
		/* 隐层权值、阈值初始化 */
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
	 * 从文本中获取训练样本
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
	 * 归一化
	 */
	public void normalization() {
		double dMax;
		double dMin;
		int IN = this.builder.IN;
		int ON = this.builder.ON;
		int stdLen = this.StD.length;
		for (int i = 0; i < IN; i++) {
			dMax = Integer.MIN_VALUE;
			dMin = Integer.MAX_VALUE;
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
	 * 输入学习+期望样本
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
	 * 隐层的输出
	 */
	public void H_I_O() {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int i = 0; i < HN; i++) {
			double sigma = 0.0;
			for (int j = 0; j < IN; j++) {
				sigma += this.W[i][j] * this.P[j];			// 求输出层内积
			}
			this.X[i] = sigma - this.YU_HN[i];
			this.H[i] = 1.0 / (1.0 + Math.exp(-this.X[i]));	// 求输出层O[k]输出
		}
	}
	
	/**
	 * 输出层的输出
	 */
	public void O_I_O() {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			double sigma = 0.0;
			for (int j = 0; j < HN; j++) {
				sigma += this.V[k][j] * this.H[j];		// 求输出层内积
			}
			double tmp = sigma - this.YU_ON[k];
			this.O[k] = 1.0 / (1.0 + Math.exp(-tmp));	// 求输出层O[k]输出
		}
	}
	
	/**
	 * 误差子程序
	 * @param m
	 */
	public void Error(int m) {
		double sqrErr = 0;			// 每个样本的平方误差计算都是从0开始
		int ON = this.builder.ON;
		for (int k = 0; k < ON; k++) {
			this.absErr[k] = this.O[k] - this.T[k];
			sqrErr += this.absErr[k] * this.absErr[k];	// 求第m个样本下输出层的平方误差
			this.oldDErr[k] = this.dErr[k];
			this.dErr[k] = this.absErr[k] * this.O[k] * (1 - this.O[k]);
		}
		
		this.errM[m] = sqrErr / 2;	// 第m个样本下输出层的平方误差
	}
	
	/**
	 * 误差子程序
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
			this.eErr[j] = sigma * this.H[j] * (1 - this.H[j]);	// eErr[j]为局部梯度  隐层各神经元的一般化误差
		}
	}
	
	/**
	 * 输出层至隐层的权值调整程序
	 * @param m
	 */
	public void Delta_O_H(int m) {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			for (int j = 0; j < HN; j++) {
				this.V[k][j] -= this.a * (this.dErr[k] * this.H[j] + 0.0001 * this.V[k][j]);	// 输出层至隐层的权值调整
			}
			this.YU_ON[k] -= this.a * this.dErr[k];		// 输出层至隐层的阈值调整
		}
	}
	
	/**
	 * 隐层至输入层的权值调整、隐层阈值调整计算子程序
	 * @param m
	 */
	public void Delta_H_I(int m) {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int j = 0; j < HN; j++) {
			for(int i = 0; i < IN; i ++) {
				this.W[j][i] -= this.b * (this.eErr[j] * this.P[i] + 0.0001 * this.W[j][i]);	// 隐层至输入层的权值调整
			}
			this.YU_HN[j] -= this.b * this.eErr[j]; //	隐层至输入层的阈值调整
		}
	}
	
	/**
	 * N个样本的全局误差计算子程序Batch Learning 批量学习法
	 * @param m
	 */
	public double ErrSum() {
		int N = this.builder.N;
		double totalErr = 0;
	    for(int m = 0; m < N; m++) {
	        totalErr += this.errM[m];	// 每个样本的均方误差加起来为全局误差
	    }
	    return totalErr;
	}
	
	/**
	 * 最后的调整
	 * @param wFile
	 * @param yFile
	 */
	public void save(String wFile, String yFile) {
		// TODO
	}
}
