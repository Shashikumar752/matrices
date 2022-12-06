package com.shashi.threading.matrices;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.StringJoiner;

/*
 * Generate 10 matrices of size N*N in file using random numbers between 1 to 20. step 1:- create a
 * method to get the file and get a write pointer with filewriter step 2:- create a method to save
 * the matrices in matrix format step 3:- create a method to generate the elements of matrices.
 */
public class MatricesGenerator {

    public static final String MATRICES_FILE = "..\\matrices.txt";
    public static final int ELEMENT_RANGE = 20;
    public static final int MATRIX_SIZE = 3;

    public static void main(String[] args) throws IOException {
        File mFile = new File(MATRICES_FILE);
        FileWriter writer = new FileWriter(mFile);
        createMatrices(writer);
        writer.flush();
        writer.close();
    }

    private static void createMatrices(FileWriter writer) throws IOException {
        System.out.println("createMatrices");
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int[][] matrix = createMatrix(random);
            saveMatrix(writer, matrix);
        }
    }

    public static void saveMatrix(FileWriter writer, int[][] matrix) throws IOException {
        System.out.println("saveMatrix");
        for (int r = 0; r < MATRIX_SIZE; r++) {
            StringJoiner join = new StringJoiner(", ");
            for (int c = 0; c < MATRIX_SIZE; c++) {
                join.add("" + matrix[r][c]);
            }
            writer.write(join.toString());
            writer.write("\n");
        }
        writer.write("\n");
    }

    private static int[][] createMatrix(Random random) {
        System.out.println("createMatrix");
        int[][] matrix = new int[MATRIX_SIZE][MATRIX_SIZE];
        for (int r = 0; r < MATRIX_SIZE; r++) {
            for (int c = 0; c < MATRIX_SIZE; c++) {
                matrix[r][c] = random.nextInt(ELEMENT_RANGE);
            }
        }
        return matrix;
    }

}
