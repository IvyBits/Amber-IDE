package amber.data.sparse;

import amber.data.sparse.SparseVector.SparseVectorIterator;
import java.util.Iterator;

public class SparseMatrix<T> implements Iterable<T>, Cloneable {

    private final int N;           // N-by-N matrix
    private SparseVector[] rows;   // the rows, each row is a sparse vector

    // initialize an N-by-N matrix of all 0s
    public SparseMatrix(int N) {
        this.N = N;
        rows = new SparseVector[N];
        for (int i = 0; i < N; i++) {
            rows[i] = new SparseVector(N);
        }
    }

    public SparseMatrix<T> clone() {
        SparseMatrix<T> clone = new SparseMatrix<T>(N);
        SparseVector[] clonedRows = new SparseVector[rows.length];
        for (int i = 0; i != clonedRows.length; i++) {
            clonedRows[i] = rows[i].clone();
        }
        clone.rows = clonedRows;
        return clone;
    }

    // put A[i][j] = value
    public void put(int i, int j, T value) {
        if (i < 0 || i >= N) {
            throw new IndexOutOfBoundsException("illegal index: " + i + ", " + j);
        }
        if (j < 0 || j >= N) {
            throw new IndexOutOfBoundsException("illegal index: " + i + ", " + j);
        }
        rows[i].set(j, value);
    }

    // return A[i][j]
    public T get(int i, int j) {
        if (i < 0 || i >= N) {
            throw new IndexOutOfBoundsException("illegal index: " + i + ", " + j);
        }
        if (j < 0 || j >= N) {
            throw new IndexOutOfBoundsException("illegal index: " + i + ", " + j);
        }
        return (T) rows[i].get(j);
    }

    public SparseMatrixIterator<T> iterator() {
        return new SparseMatrixIterator<T>();
    }

    public class SparseMatrixIterator<T> implements Iterator<T> {

        int index;
        SparseVectorIterator row = rows[0].iterator();

        public boolean hasNext() {
            return index < rows.length - 1 || row.hasNext();
        }

        public T next() {
            if (!row.hasNext()) {
                index++;
                row = rows[index].iterator();
            }
            return row.hasNext() ? (T) row.next() : null;
        }

        public void remove() {
            row.remove();
        }

        public int realX() {
            return index;
        }

        public int realY() {
            return row.realIndex();
        }
    }
}