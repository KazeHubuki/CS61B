package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        int N = 1000;
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> times = new AList<Double>();
        AList<Integer> opCounts = new AList<Integer>();
        for (int i = 0; i < 8; i += 1) {
            SLList<Integer> s = new SLList<Integer>();
            for (int j = 0; j < N; j += 1) {
                s.addLast(1);
            }
            Stopwatch t = new Stopwatch();
            for (int k = 0; k < 10000; k += 1) {
                s.getLast();
            }
            times.addLast(t.elapsedTime());
            Ns.addLast(N);
            opCounts.addLast(10000);
            N = N * 2;
        }
        printTimingTable(Ns, times, opCounts);
    }

}
