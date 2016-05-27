package MSP.file.mapping.parity.compute;

import Jama.Matrix;

public class BasicMatrix {
	
	public double[] mutiply(double[][] A, double[] B) {
		Matrix matrixA = new Matrix(A);
		Matrix matrixB = new Matrix(B, B.length);
		
		Matrix C = matrixA.times(matrixB);
		double[] res = C.getColumnPackedCopy();
		roundMatrix(res);
//		System.out.println(matrixA.det());
		return res;
		
	}
	
	public double[][] mutiply(double[][] A, double B) {
		Matrix matrixA = new Matrix(A);
		Matrix C = matrixA.times(B);
		double[][] res = C.getArray();
		roundMatrix(res);
		
		return res;
		
	}

	public double[] divide(double[][] A, double[] B) {
		Matrix matrixA = new Matrix(A);
		Matrix matrixB = new Matrix(B, B.length);
		
//		for (int i = 0; i < A.length; i++) {
//			for (int j = 0; j < A[0].length; j++) {
//				System.out.print(A[i][j] + "  ");
//			}
//			System.out.println();
//		}
		
		if (A.length == A[0].length) {
			if (matrixA.det() == 0) {
				return null;
			}
		}
		
		Matrix C = matrixA.solve(matrixB);
		double[] res = C.getColumnPackedCopy();
		
		for (int i = 0; i < res.length; i++) {
			System.out.println(res[i]  + " | " + Math.round(res[i]));
		}
		System.out.println("-----------");
		
		roundMatrix(res);
		
//		System.out.println(matrixA.det());
		
		
		return res;
	}
	
	private void roundMatrix(double[][] A) {
		int row = A.length;
		int column = A[0].length;
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				A[i][j] = Math.round(A[i][j]);
			}
		}	
	}
	private void roundMatrix(double[] A) {
		int row = A.length;
		for (int i = 0; i < row; i++) {
			A[i] = Math.round(A[i]);
		}	
	}
	
	public static void main(String[] args){
		double[][] A = new double[][]{{1, 2, 7}, {2, 3, 5}, {3, 2, 1}, {4, 5, 2}, {1, 3, 5}};
//		double[][] A = new double[][]{{0.1, 0.2, 0.7}, {0.2, 0.3, 0.5}, {0.5, 0.4, 0.1}};
//		double[][] A = new double[][]{{0.1, 0.2, 0}, {0, 1, 0}, {0, 0, 1}};
//		double[] B = new double[]{21, 21, 19};
		
		double[] B = new double[]{1, 2, 3};
		
		
		
		BasicMatrix basic = new BasicMatrix();
		
//		double[] C = basic.divide(A, B);
		double[] C = basic.mutiply(A, B);		
//		double[][] D = basic.mutiply(A, 100);
		for (int i = 0; i < C.length; i++) {
			System.out.println(C[i]  + " | " + Math.round(C[i]));
		}
		
//		for (int i = 0; i < D.length; i++) {
//			for (int j = 0; j < D[0].length; j++) {
//				System.out.println(D[i][j]);
//			}			
//		}
	}
}
