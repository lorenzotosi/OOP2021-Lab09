package it.unibo.oop.lab.workers02;

import java.util.ArrayList;
import java.util.List;
/**
 * 
 * 
 *
 */
public class MultiThreadedSumMatrix implements SumMatrix {
    private final int nthreads;
    /**
     * 
     * @param n a
     */
    public MultiThreadedSumMatrix(final int n) {
        this.nthreads = n;
    }

    private static class Worker extends Thread {
        private final double[][] list;
        private final int startpos;
        private final int nelem;
        private long res;

        /**
         * Build a new worker.
         * 
         * @param list
         *            the list to sum
         * @param startpos
         *            the initial position for this worker
         * @param nelem
         *            the no. of elems to sum up for this worker
         */
        Worker(final double[][] list, final int startpos, final int nelem) {
            super();
            this.list = list.clone();
            this.startpos = startpos;
            this.nelem = nelem;
        }

        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + nelem - 1));
            for (int i = startpos; i < list.length && i < startpos + nelem; i++) {
                for (final double val : list[i]) {
                    this.res += val;
                }
            }
        }

        /**
         * Returns the result of summing up the integers within the list.
         * 
         * @return the sum of every element in the array
         */
        public long getResult() {
            return this.res;
        }
    }
    /**
     * @return a
     * 
     * @param list a
     */
    public double sum(final double[][] list) {
        final int size = list.length % nthreads + list.length / nthreads;
        final List<Worker> workers = new ArrayList<>(nthreads);
        for (int start = 0; start < list.length; start += size) {
            workers.add(new Worker(list, start, size));
        }
        for (final Worker w: workers) {
            w.start();
        }
        long sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }
        return sum;
    }
}
