package nn.elm;

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
		
		Matrix H = new Matrix(N, HN);
		Matrix HO = new Matrix(M, HN);
		Matrix K = new Matrix(M, N);
		Matrix COO = new Matrix(N, N);
		
		Matrix E = Matrix.identity(N, N);
		Matrix alfa = new Matrix(N, ON);
		
		elm.getTrainingData();
		elm.normalization(N + M);
		
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
		
		/* Test Data */
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
		}
		
		/* calculate Output Layer weight: belta = inverse(H) * T */
		// TODO
	}
}
