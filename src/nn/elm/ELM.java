package nn.elm;

import java.util.Date;
import java.util.Random;

public class ELM {

	public Builder builder;
	
	public StudyData[] StD;
	public double[] P;		// 单个样本输入数据
	public double[] T;		// 单个样本教师数据
	
	public double[][] W;	// 输入层至隐层权值
	public double[] b;		// 隐层的阈值
	
	public double[][] belta;// 隐层至输出层权值
	
	public double[] X;		// 隐层的输入
	public double[] Y;		// 输出层的输入
	public double[] H;		// 隐层的输出
	public double[] O;		// 输出层的输出
	
	public double[] errM;	// 第m个样本的总误差
	
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
		/* 隐层权值、阈值初始化 */
		this.W = new double[HN][IN];
		for (int i = 0; i < HN; i++) {
			this.W[i] = new double[IN];
			for (int j = 0; j < IN; j++) {
				this.W[i][j] = random.nextDouble() - 0.5;	// 初始化输入层到隐层的权值，随机模拟-0.5到0.5之间的值
			}
		}
		this.b = new double[HN];
		for (int i = 0; i < HN; i++) {
			this.b[i] = random.nextDouble() - 0.5;			// 隐层阈值初始化
		}
	}
	
	/**
	 * 获取训练数据
	 */
	public void getTrainingData() {
		
	}
	
	/**
	 * 获取测试数据
	 */
	public void getTestData() {
		
	}
	
	/**
	 * 归一化
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
	 * 第m个学习样本输入子程序
	 * @param m
	 */
	public void input_P(int m) {
		int IN = this.builder.IN;
		for (int i = 0; i < IN; i++) {
			this.P[i] = this.StD[m].input[i];
		}
	}
	
	/**
	 * 第m个样本期望信号子程序
	 * @param m
	 */
	public void input_T(int m) {
		int ON = this.builder.ON;
		for (int i = 0; i < ON; i++) {
			this.T[i] = this.StD[m].teach;
		}
	}
	
	/**
	 * 隐层各单元输入、输出值子程序
	 */
	public void H_I_O() {
		int HN = this.builder.HN;
		int IN = this.builder.IN;
		for (int i = 0; i < HN; i++) {
			double sigma = 0;
			for (int j = 0; j < IN; j++) {
				sigma += this.W[i][j] * this.P[j];	// 求输出层内积
			}
			double x = sigma + this.b[i];			// 求输出层O[k]输出
			this.X[i] = x;
			this.H[i] = 1 / (1 + Math.exp(-x));
		}
	}
	
	/**
	 * 输出层各单元输入、输出值子程序
	 */
	public void O_I_O() {
		int ON = this.builder.ON;
		int HN = this.builder.HN;
		for (int k = 0; k < ON; k++) {
			double sigma = 0;
			for (int j = 0; j < HN; j++) {
				sigma += this.belta[k][j] * this.H[j];	// 求输出层内积
			}
			this.O[k] = sigma;						// 求输出层O[k]输出
		}
	}
	
	/**
	 * N个样本的全局误差计算   Batch Leaning 批量学习法
	 * @return
	 */
	public double errSum() {
		int N = this.builder.N;
		int ON = this.builder.ON;
		
		double[] errM = new double[N];			// 第m个样本的总误差
		double sqrErr = 0;						// 每个样本的平方误差计算都是从0开始
		double totalErr = 0;
		for (int m = 0; m < N; m++) {
			for (int k = 0; k < ON; k++) {
				errM[k] = this.T[k] - this.O[k];
				sqrErr += errM[k] * errM[k];	// 求第m个样本下输出层的平方误差
			}
			errM[m] = sqrErr / 2;				// 第m个样本下输出层的平方误差
			totalErr += errM[m];
		}
		return totalErr;
	}
	
	/**
	 * 保存权值最后调整结果
	 */
	public void save() {
		// TODO
	}
}
