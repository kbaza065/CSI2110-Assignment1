/**
 *
 * CSI 2110 (Fall 2020)
 * Programming Assignment 1, Part 1
 *
 * By: Kian Bazarjani
 * Student Number: 300063160
 *
 */

public class RankingPair {

    private final int employerRanking;
    private final int studentRanking;

    /**
     * @param pair String containing a ranking pair of integers in the format "employer_ranking, student_ranking"
     */
    public RankingPair(String pair) {
        // Split the ranking to store each integer as a String in an array
        String[] rankings = pair.split(",");
        // Parse String array at first index to retrieve employer_ranking
        this.employerRanking = Integer.parseInt(rankings[0]);
        // Parse String array at second index to retrieve student_ranking
        this.studentRanking = Integer.parseInt(rankings[1]);

    }

    public int getEmployerRanking() {
        return employerRanking;
    }

    public int getStudentRanking() {
        return studentRanking;
    }

    public String toString() {
        return "(" + employerRanking + "," + studentRanking + ")";
    }

}
