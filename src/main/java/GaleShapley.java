import java.io.*;
import java.util.*;

import datastructures.ArrayStack;
import datastructures.HeapPriorityQueue;
import datastructures.Stack;

/**
 *
 * CSI 2110 (Fall 2020)
 * Programming Assignment 1, Part 1
 *
 * By: Kian Bazarjani
 * Student Number: 300063160
 *
 */

public class GaleShapley {

    private Stack<Integer> Sue;

    private int size;

    private int[] students;
    private int[] employers;

    private int[][] A;

    private HeapPriorityQueue[] PQ;

    private RankingPair[][] rankingMatrix;

    private String[] studentNames;
    private String[] employerNames;

    private String[] matches;

    public static void main (String[] args) {

        GaleShapley stableMatch = new GaleShapley();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter filename: ");
        String fileName = scanner.nextLine();

        stableMatch.initialize(fileName);
        stableMatch.execute();
        stableMatch.save("matches_" + fileName);
    }

    public void initialize(String fileName) {

        try {
            // Access input file, setup scanner for reading from file
            File file = new File("./src/main/resources/input/" + fileName);
            Scanner scanner = new Scanner(file);

            // Read number of match pairs from file, then store in "size"
            size = Integer.parseInt(scanner.nextLine());

            // Read employer names from file, then store in Strings array
            employerNames = new String[size];

            for (int i=0; i < size; i++) {
                employerNames[i] = scanner.nextLine();
            }

            // Read student names from file, then store in Strings array
            studentNames = new String[size];

            for (int i=0; i < size; i++) {
                studentNames[i] = scanner.nextLine();
            }

            // Read ranking matrix from file, then store in 2D array
            rankingMatrix = new RankingPair[size][size];

            for (int i=0; i < size; i++) {
                String row = scanner.nextLine();
                String[] pair = row.split(" ");

                for (int j=0; j < size; j++) {
                    rankingMatrix[i][j] = new RankingPair(pair[j]);
                }

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Create stack of unmatched employers
        Sue = new ArrayStack<>();

        // Push all employers in this stack (starting from employer 0)
        for (int i=0; i < size; i++) {
            Sue.push(i);
        }

        // Create arrays to represent current matches
        students = new int[size];
        employers = new int[size];

        // Initialize array entries to -1
        Arrays.fill(students, -1);
        Arrays.fill(employers, -1);

        // Create 2D array of size nxn
        // A[s][e] is the ranking score given by student s to company e
        A = new int[size][size];

        for (int i=0; i < size; i++) {

            for (int j=0; j < size; j++) {
                A[i][j] = rankingMatrix[j][i].getStudentRanking();

            }

        }

        // Create array of priority queues
        PQ = new HeapPriorityQueue[size];

        // Initialize each priority queue in array
        for (int i=0; i < size; i++) {
            PQ[i] = new HeapPriorityQueue<>();
        }

        // Initialize each employer's priority queue with employer rankings from ranking matrix
        for (int s=0; s < size; s++) {
            for (int e=0; e < size; e++) {
                int employerRating = rankingMatrix[e][s].getEmployerRanking();
                PQ[e].insert(employerRating, s);
            }
        }

    }

    public String[] execute() {

        // Run the Gale-Shapley algorithm as described in assignment documentation
        int e, e_not, s;

        while (!Sue.isEmpty()) {

            e = Sue.pop();
            s = (int) PQ[e].removeMin().getValue();
            e_not = students[s];

            if (students[s] == -1) {
                // Student is unmatched
                students[s] = e;
                employers[e] = s; // match (e, s)
            } else if (A[s][e] < A[s][e_not]) {
                // s prefers e to employer e_not
                students[s] = e;
                employers[e] = s; // Replace the match
                employers[e_not] = -1; // Now unmatched
                Sue.push(e_not);
            } else {
                // s rejects offer from e
                Sue.push(e);
            }

        }

        // Generate required text for output file before writing results to file.
        // Store text in String array, with matches[i] representing the text to be written to line 'i' in the output file.
        // Order is kept identical to that of the employer names from the input file.

        matches = new String[size];

        StringBuilder sb = new StringBuilder();
        int match;

        for (int i=0; i < size; i++) {

            sb.append("Match ").append(i).append(": ");
            sb.append(employerNames[i]).append(" - ");

            // Find student matched to this employer
            match = employers[i];

            // Lookup student name and append to string
            sb.append(studentNames[match]);

            // Insert final text in array
            matches[i] = sb.toString().trim();

            // Reset StringBuilder for next line
            sb.setLength(0);

        }

        // Return set of stable matches as specified.
        return matches;

    }

    public void save(String fileName) {
        createOutputFile(fileName);
        writeMatchesToFile(fileName);

    }

    private void createOutputFile(String fileName) {

        try {
            File file = new File("./src/main/resources/output/" + fileName);

            if (file.createNewFile()) {
                System.out.println("\nOutput file created: " + file.getName());
                System.out.println("File location: " + file.getPath());
            } else {
                System.out.println("\n Output file already exists.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeMatchesToFile(String fileName) {

        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("./src/main/resources/output/" + fileName));

            for (int i=0; i < matches.length; i++) {
                // Write output for match i to file
                writer.write(matches[i]);
                if (i < matches.length - 1) {
                    // More matches left to write
                    // Go to next line
                    writer.newLine();
                } else {
                    // Done writing matches
                    writer.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
