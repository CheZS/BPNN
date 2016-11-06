package nn.elm;

import java.util.Date;

import Jama.Matrix;

public class ELMNN {

	public static void main(String[] args) {
		/* init */
		Builder builder = new Builder();
		ELM elm = new ELM(builder);
		
		int IN = builder.IN;
		int ON = builder.ON;
		int HN = builder.HN;
		int N = builder.N;
		int M = builder.M;
		
		Matrix X = new Matrix(N, IN);
		Matrix XO = new Matrix(M, IN);
		Matrix T = new Matrix(N, ON);
		Matrix O = new Matrix(M, ON);
		Matrix OUT = null;
		
		Matrix H = new Matrix(N, HN);
		Matrix HO = new Matrix(M, HN);
		Matrix K = new Matrix(M, N);
		Matrix COO = new Matrix(N, N);
		Matrix belta = null;
		
		Matrix E = Matrix.identity(N, N);
		Matrix alfa = new Matrix(N, ON);
		
		elm.getTrainingData(builder.inputFileName);
		elm.normalization(N + M);
		
		long t1 = new Date().getTime();
		/* Training Data */
		for (int m = 0; m < N; m++) {
			elm.input_P(m);
			elm.input_T(m);
			for (int i = 0; i < IN; i++) {
				X.set(m, i, elm.P[i]);
			}
			// calculate Output Layer output: T
			for (int i = 0; i < ON; i++) {
				T.set(m, i, elm.T[i]);
			}
			// calculate Hidden Layer output: H
			elm.H_I_O();
			for (int i = 0; i < HN; i++) {
				H.set(m, i, elm.H[i]);
			}
		}
		
		/* calculate Output Layer weight: belta = inverse(H) * T */
		belta = H.inverse().times(T);
		for (int i = 0; i < HN; i++) {
			for (int j = 0; j < ON; j++) {
				elm.belta[i][j] = belta.get(i, j);
			}
		}
		long t2 = new Date().getTime();
		
		/* Test Data */
		int count = 0;
		for (int m = 0; m < M; m++) {
			elm.input_P(m + N);
			elm.input_T(m + N);
			for (int i = 0; i < IN; i++) {
				XO.set(m, i, elm.P[i]);
			}
			for (int i = 0; i < ON; i++) {
				O.set(m, i, elm.T[i]);
			}
			elm.H_I_O();
			for (int i = 0; i < HN; i++) {
				HO.set(m, i, elm.H[i]);
			}
			elm.O_I_O();
			if (Math.abs(O.get(m, 0) - elm.O[0]) <= 50) {
				count++;
			}
		}
		System.out.println("time: " + (t2 - t1));
		System.out.println("rate: " + ((double)count) / M);
	}
}
