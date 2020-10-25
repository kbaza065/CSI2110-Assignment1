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

    public RankingPair(String pair) {
        String[] rankings = pair.split(",");
        this.employerRanking = Integer.parseInt(rankings[0]);
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
