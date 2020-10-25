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

    public RankingPair(int employerRanking, int studentRanking) {
        this.employerRanking = employerRanking;
        this.studentRanking = studentRanking;
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
