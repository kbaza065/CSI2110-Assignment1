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

    private String[] stableMatches;

    private static final String INPUT_DIRECTORY_PATH = "./src/main/resources/input/";
    private static final String OUTPUT_DIRECTORY_PATH = "./src/main/resources/output/";

    public static void main (String[] args) {

        GaleShapley stableMatch = new GaleShapley();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter filename: ");
        String fileName = scanner.nextLine();

        stableMatch.initialize(fileName);
        stableMatch.execute();
        stableMatch.save("matches_" + fileName);
    }

    /**
     * Initializes the data structures required to run the Gale-Shapley algorithm for
     * matching employers with students for co-op positions.
     *
     * @param fileName the name of the input text file
     */
    public void initialize(String fileName) {

        try {
            // Access input file, setup scanner for reading from file
            File file = new File(INPUT_DIRECTORY_PATH + fileName);
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

    /**
     * Executes the Gale-Shapley algorithm to match the specified employers with students,
     * according to their given rankings from the input file.
     *
     * @return stableMatches the set of stable matches
     */
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
        // Store text in String array, with stableMatches[i] representing the text to be written to line 'i' in the output file.
        // Order is kept identical to that of the employer names from the input file.

        stableMatches = new String[size];

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
            stableMatches[i] = sb.toString().trim();

            // Reset StringBuilder for next line
            sb.setLength(0);

        }

        // Return set of stable matches as specified.
        return stableMatches;

    }

    /**
     * Saves the stable matches generated by the Gale-Shapley algorithm into a new output file.
     *
     * @param fileName name of output file (in the format "matches_ABC.txt")
     */
    public void save(String fileName) {
        // Create a new file to write stable matches to
        createOutputFile(fileName);
        // Write stable matches to output file
        writeMatchesToFile(fileName);

    }

    /**
     * Creates the output file which stores the stable matches generated.
     *
     * @param fileName name of output file (in the format "matches_ABC.txt")
     */
    private void createOutputFile(String fileName) {

        try {
            File file = new File(OUTPUT_DIRECTORY_PATH + fileName);

            if (file.createNewFile()) {
                // File does not exist - create it and print its name and location
                System.out.println("\nOutput file created: " + file.getName());
                System.out.println("File location: " + file.getPath());
            } else {
                System.out.println("\n Output file already exists.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the stable matches into the new output file, in the order of the employers
     * given in the original input file.
     *
     * @param fileName name of output file (in the format "matches_ABC.txt")
     */
    private void writeMatchesToFile(String fileName) {

        try {
            // Get writer for output file
            BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT_DIRECTORY_PATH + fileName));

            for (int i = 0; i < stableMatches.length; i++) {
                // Write output for match i to file
                writer.write(stableMatches[i]);
                if (i < stableMatches.length - 1) {
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

    /**
     * Checks if a given set of matches is stable.
     *
     * A stable match is defined such that neither party to the match has a
     * preferred party that also prefers them over their current match.
     *
     * @param students The students' current matches
     *                 (if student s is matched to employer e, then students[s] = e)
     * @param employers The employers' current matches
     *                 (if employer e is matched to student s, then employers[e] = s)
     * @param A The ranking score given by student s to employer e
     * @param B The ranking score given by employer e to student s
     * @return True if the set of matches is stable, otherwise false
     */
    public boolean isStableMatch(int[] students, int[] employers, int[][] A, int[][] B) {

        int e, s, n;
        n = students.length;

        for (int i=0; i < n; i++) {

            // Looking at Employer i
            e = i;
            // Get Employer e's current match
            s = employers[e];

            // Check match stability only if current match != most preferred match
            if (B[e][s] > 1) {
                for (int j = 0; j < n; j++) {
                    // Make sure Employer e does not prefer Student s over Student j
                    // and Student s is not Student j
                    if ((B[e][j] < B[e][s]) && (j != s)) {
                        // Get Student j's current match
                        int e_current = students[j];
                        // Check if Student j prefers Employer e over Employer e_current
                        if (A[j][e] < A[j][e_current]) {
                            // Employer e prefers Student j over current match s
                            // Student j prefers Employer e over current match e_current
                            // The match is not stable, thus the set of matches is not stable
                            return false;
                        }
                    }
                }
            }
        }

        // All the match pairs are stable
        // Thus the solution IS stable
        return true;

    }

}
