package nn.bp;

import java.util.Date;

public class BPNN {

	public static void main(String[] args) {
		
		Builder builder = new Builder();
		BP bptest = new BP(builder);
		
		double sumErr = 0;
		int study = 0;
		double preError = 1;
		int N = builder.N;

		/* 训练过程 */
		long t1 = new Date().getTime();
		
		do {
			study++;
			for (int m = 0; m < N; m++) {
				bptest.input_P_T(bptest.StD[m]);
				bptest.H_I_O();
	            bptest.O_I_O();
	            bptest.Error(m);
	            bptest.Err_H_I(m);
	            bptest.Delta_O_H(m);
	            bptest.Delta_H_I(m);
			}
			sumErr = bptest.ErrSum();
			if (builder.mode.equals("debug") == true) {
				System.out.println("第" + study + "次学习的均方误差为 = " + sumErr);
			}
		} while (sumErr > preError && study < 4e3);
		long t2 = new Date().getTime();
		
		System.out.println("网络已经学习了" + study + "次，学习的均方差为 = " + sumErr);
		System.out.println("训练时间 = " + (t2 - t1) + "ms");
		bptest.save("weight.txt", "bias.txt");
		
		/* 为学习后的测试过程 */
		System.out.println("输入待测试的样本");
		int count = 0;
		int M = builder.M;
		int ON = builder.ON;
		for(int m = 0; m < M; m++) {
	        bptest.input_P_T(bptest.StD[N + m]);
	        bptest.H_I_O();
	        bptest.O_I_O();
	        for(int k = 0; k < ON; k ++) {
	        	if (builder.mode.equals("debug") == true) {
	        		System.out.println("T = " + bptest.T[k]);
	        		System.out.println("O = " + bptest.O[k]);
	        	}
	            if(Math.abs(bptest.T[k] - bptest.O[k]) * 1000 > 50) {
	                count++;
	                break;
	            }
	        }
	    }
		System.out.println("==========\nSummary\n==========");
		System.out.println("测试正确率 = " + (double) (M - count) / M);
		System.out.println("网络已经学习了" + study + "次，学习的均方差为 = " + sumErr);
		System.out.println("训练时间 = " + (t2 - t1) + "ms");
	}
}
