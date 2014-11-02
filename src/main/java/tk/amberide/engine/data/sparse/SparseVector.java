/*
 * Copyright (C) 2003-2006 Bjørn-Ove Heimsund
 * 
 * This file is part of MTJ.
 * 
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or (at your
 * option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package tk.amberide.engine.data.sparse;

import tk.amberide.ide.os.OS;
import java.util.Iterator;

/**
 * Sparse vector
 */
public class SparseVector<T> implements Iterable<T>, Cloneable {

    Object[] data;
    int[] index;
    int used;
    int size;

    /**
     * Constructor for SparseVector.
     *
     * @param size Size of the vector
     * @param nz Initial number of non-nulls
     */
    public SparseVector(int size, int nz) {
        this.size = size;
        data = new Object[nz];
        index = new int[nz];
    }

    @Override
    public SparseVector<T> clone() {
        SparseVector<T> clone = new SparseVector<T>(size, data.length);
        clone.data = OS.deepcopy(data);
        clone.index = index.clone();
        clone.used = used;
        return clone;
    }

    /**
     * Constructor for SparseVector. Zero initial pre-allocation
     *
     * @param size Size of the vector
     */
    public SparseVector(int size) {
        this(size, 0);
    }

    public SparseVector() {
        this(Integer.MAX_VALUE);
    }

    public void set(int index, T value) {
        check(index);

        int i = getIndex(index);
        data[i] = value;
    }

    public T get(int index) {
        check(index);

        int in = binarySearch(this.index, index, 0, used);
        if (in >= 0) {
            return (T) data[in];
        }
        return null;
    }

    /**
     * Tries to find the index. If it is not found, a reallocation is done, and
     * a new index is returned.
     */
    protected int getIndex(int ind) {
        // Try to find column index
        int i = binarySearchInterval(index, ind, 0, used, true);

        // Found
        if (i < used && index[i] == ind) {
            return i;
        }

        int[] newIndex = index;
        Object[] newData = data;

        // Check available memory
        if (++used > data.length) {

            // If zero-length, use new length of 1, else double the bandwidth
            int newLength = data.length != 0 ? data.length << 1 : 1;

            // Copy existing data into new arrays
            newIndex = new int[newLength];
            newData = new Object[newLength];
            System.arraycopy(index, 0, newIndex, 0, i);
            System.arraycopy(data, 0, newData, 0, i);
        }

        // All ok, make room for insertion
        System.arraycopy(index, i, newIndex, i + 1, used - i - 1);
        System.arraycopy(data, i, newData, i + 1, used - i - 1);

        // Put in new structure
        newIndex[i] = ind;
        newData[i] = 0.;

        // Update pointers
        index = newIndex;
        data = newData;

        // Return insertion index
        return i;
    }

    /**
     * Returns the internal data
     */
    public Object[] getData() {
        return data;
    }

    /**
     * Returns the indices
     */
    public int[] getIndex() {
        if (used == index.length) {
            return index;
        }

        // could run compact, or return subarray
        // compact();
        int[] indices = new int[used];
        for (int i = 0; i < used; i++) {
            indices[i] = index[i];
        }
        return indices;
    }

    /**
     * Number of entries used in the sparse structure
     */
    public int getUsed() {
        return used;
    }

    public int nonZeroEntries() {
        int nz = 0;
        for (Object e : data) {
            if (e != null) {
                nz++;
            }
        }
        return nz;
    }

    protected void check(int index) {
        if (index < 0) {
            throw new IndexOutOfBoundsException("index is negative (" + index
                    + ")");
        }
        if (index >= size) {
            throw new IndexOutOfBoundsException("index >= size (" + index
                    + " >= " + size + ")");
        }
    }

    /**
     * Compacts the vector
     */
    public void compact() {
        int nz = nonZeroEntries(); // catches zero entries

        if (nz < data.length) {
            int[] newIndex = new int[nz];
            Object[] newData = new Object[nz];

            // Copy only non-zero entries
            for (int i = 0, j = 0; i < data.length; ++i) {
                if (data[i] != null) {
                    newIndex[j] = index[i];
                    newData[j] = data[i];
                    j++;
                }
            }

            data = newData;
            index = newIndex;
            used = data.length;
        }
    }

    protected int binarySearchInterval(int[] index, int key, int begin, int end, boolean greater) {
        // Zero length array?
        if (begin == end) {
            if (greater) {
                return end;
            } else {
                return begin - 1;
            }
        }

        end--; // Last index
        int mid = (end + begin) >> 1;

        // The usual binary search
        while (begin <= end) {
            mid = (end + begin) >> 1;

            if (index[mid] < key) {
                begin = mid + 1;
            } else if (index[mid] > key) {
                end = mid - 1;
            } else {
                return mid;
            }
        }

        // No direct match, but an inf/sup was found
        if ((greater && index[mid] >= key) || (!greater && index[mid] <= key)) {
            return mid;
        } // No inf/sup, return at the end of the array
        else if (greater) {
            return mid + 1; // One past end
        } else {
            return mid - 1; // One before start
        }
    }

    protected int binarySearch(int[] index, int key, int begin, int end) {
        end--;

        while (begin <= end) {
            int mid = (end + begin) >> 1;

            if (index[mid] < key) {
                begin = mid + 1;
            } else if (index[mid] > key) {
                end = mid - 1;
            } else {
                return mid;
            }
        }

        return -1;
    }

    public SparseVectorIterator<T> iterator() {
        return new SparseVectorIterator<T>();
    }

    public class SparseVectorIterator<T> implements Iterator<T> {

        private int pointer;

        public boolean hasNext() {
            return pointer < used;
        }

        public T next() {
            return (T) data[pointer++];
        }

        public void remove() {
            data[pointer] = null;
        }

        public int realIndex() {
            return pointer == 0 ? 0 : index[pointer - 1];
        }
    }
}
