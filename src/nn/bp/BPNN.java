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

		/* ѵ������ */
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
				System.out.println("��" + study + "��ѧϰ�ľ������Ϊ = " + sumErr);
			}
		} while (sumErr > preError && study < 4e3);
		long t2 = new Date().getTime();
		
		System.out.println("�����Ѿ�ѧϰ��" + study + "�Σ�ѧϰ�ľ�����Ϊ = " + sumErr);
		System.out.println("ѵ��ʱ�� = " + (t2 - t1) + "ms");
		bptest.save("weight.txt", "bias.txt");
		
		/* Ϊѧϰ��Ĳ��Թ��� */
		System.out.println("��������Ե�����");
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
		System.out.println("������ȷ�� = " + (double) (M - count) / M);
		System.out.println("�����Ѿ�ѧϰ��" + study + "�Σ�ѧϰ�ľ�����Ϊ = " + sumErr);
		System.out.println("ѵ��ʱ�� = " + (t2 - t1) + "ms");
	}
}
