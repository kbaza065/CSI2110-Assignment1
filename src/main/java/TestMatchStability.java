public class TestMatchStability {

    public static void main(String args[]) {

        GaleShapley galeShapley = new GaleShapley();

        int n;
        int[] students, employers;
        int[][] A, B;

        students = new int[] { 0, 1, 2 };
        employers = new int[] { 0, 1, 2 };

        A = new int[][] {
                { 1, 2, 3 },
                { 1, 3, 2 },
                { 2, 3, 1 }
        };

        B = new int[][] {
                { 1, 2, 3 },
                { 1, 2, 3 },
                { 1, 2, 3 }
        };

        boolean isStable = galeShapley.isStableMatch(students, employers, A, B);

        System.out.print(isStable);

    }

}
