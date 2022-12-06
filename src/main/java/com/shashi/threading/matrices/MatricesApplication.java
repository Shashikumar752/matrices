package com.shashi.threading.matrices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*
 * implement the producer consumer problem and solution using back pressure step 1: create a
 * producer in this case the thread to read the pair of matrix from file to put in a queue step 2:
 * create a consumer in this case read the matrix input from queue and multiply the matrices to
 * output to a new file. step 3: synchronze the queue to make thread safe and provide queue
 * implementation using LinkedList
 */
@SpringBootApplication
public class MatricesApplication {
	public static final String MATRICES_FILE = "..\\matrices.txt";
	public static final String MATRICES_RESULT_FILE = "..\\matrices_result.txt";
	public static final int N = 3;
	public static final int CAPACITY = 5;

	public static void main(String[] args) throws IOException, InterruptedException {
		SpringApplication.run(MatricesApplication.class, args);
		File inputF = new File(MATRICES_FILE);
		File outputF = new File(MATRICES_RESULT_FILE);
		ThreadSafeQueue queue = new ThreadSafeQueue();
		MatrixProducer p = new MatrixProducer(queue, new FileReader(inputF));
		FileWriter writer = new FileWriter(outputF);
		MatrixConsumer c = new MatrixConsumer(queue, writer);

		p.start();
		c.start();
	}

	public static class MatrixConsumer extends Thread {
		private ThreadSafeQueue thQ;
		private FileWriter writer;

		public MatrixConsumer(ThreadSafeQueue queue, FileWriter writer) {
			this.thQ = queue;
			this.writer = writer;
		}

		@Override
		public void run() {
			System.out.println("consumption started");
			while (true) {
				try {
					MatricesPair pair = thQ.poll();
					if (pair == null) {
						System.out.println("pair is null");
						break;
					} else {
						int[][] result = multiply(pair.matrix1, pair.matrix2);
						System.out.println("writing to file");
						MatricesGenerator.saveMatrix(writer, result);
					}
				} catch (InterruptedException | IOException e1) {
					e1.printStackTrace();
				}
			}
			try {
				System.out.println("completed");
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private int[][] multiply(int[][] matrix1, int[][] matrix2) {
			int[][] result = new int[N][N];
			for (int r = 0; r < matrix1.length; r++) {
				for (int c = 0; c < matrix1[r].length; c++) {
					for (int k = 0; k < matrix2.length; k++) {
						result[r][c] += matrix1[r][k] * matrix2[k][c];
					}
				}
			}
			return result;
		}

	}

	public static class MatrixProducer extends Thread {
		private ThreadSafeQueue thQ;
		private Scanner scan;

		public MatrixProducer(ThreadSafeQueue queue, FileReader fileReader) {
			this.thQ = queue;
			this.scan = new Scanner(fileReader);
		}

		@Override
		public void run() {
			System.out.println("producing");
			while (scan.hasNextLine()) {
				MatricesPair pair = getMatricesPair(scan);
				if (pair.matrix1 == null || pair.matrix2 == null) {
					thQ.isTerminate = true;
					return;
				}
				try {
					thQ.push(pair);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			thQ.isTerminate = true;
		}

		private MatricesPair getMatricesPair(Scanner scan) {
			System.out.println("reading matrices from file");
			int[][] matrix1 = readMatrix(scan);
			int[][] matrix2 = readMatrix(scan);

			return new MatricesPair(matrix1, matrix2);
		}

		private int[][] readMatrix(Scanner scan) {
			int[][] matrix = new int[N][N];
			for (int r = 0; r < N; r++) {
				String[] col = scan.nextLine().split(",");
				for (int c = 0; c < N; c++) {
					matrix[r][c] = Integer.parseInt(col[c].trim());
				}
			}
			scan.nextLine();
			return matrix;
		}

	}
	public static class ThreadSafeQueue {
		private Queue<MatricesPair> q = new LinkedList<>();
		private boolean isEmpty = true;
		private boolean isTerminate = false;

		public synchronized void push(MatricesPair pair) throws InterruptedException {
			while (!isTerminate && q.size() == CAPACITY) {
				wait();
			}
			q.add(pair);
			isEmpty = false;
			notify();
		}

		public synchronized MatricesPair poll() throws InterruptedException {
			MatricesPair pair = null;
			while (!isTerminate && isEmpty) {
				wait();
			}
			if (q.size() == 1) {
				isEmpty = true;
			}
			if (isTerminate && q.size() == 0) {
				return null;
			}
			pair = q.poll();
			notifyAll();
			return pair;
		}
	}

	public static class MatricesPair {
		public int[][] matrix1;
		public int[][] matrix2;

		/**
		 * @param matrix1
		 * @param matrix2
		 */
		public MatricesPair(int[][] matrix1, int[][] matrix2) {
			this.matrix1 = matrix1;
			this.matrix2 = matrix2;
		}


	}

}
