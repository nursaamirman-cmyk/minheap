Algorithmic Analysis of Min-Heap Data Structure
Project Overview
This repository contains the implementation of a Min-Heap data structure and a detailed Analysis Report covering the theoretical complexity and empirical validation. This submission fulfills the individual requirements for Assignment 2.

Algorithm Implemented:

Min-Heap Implementation (Student: Mirman Nursaya SE-2438)

Repository Structure
File/Folder

Content

src/
package algorithms;

import metrics.PerformanceTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection; 
import java.util.NoSuchElementException;

public class MinHeap<T extends Comparable<T>> {
    
    private List<T> heap; 
    private PerformanceTracker tracker;

    public MinHeap(PerformanceTracker tracker) {
        this.heap = new ArrayList<>();
        this.tracker = tracker;
    }
    
    
    public MinHeap(PerformanceTracker tracker, Collection<T> initialElements) {
        this.heap = new ArrayList<>(initialElements);
        this.tracker = tracker;
        buildHeap(); 
    }

    public int size() {
        return heap.size();
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    
    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }
    
   
    private void swap(int i, int j) {
        tracker.incrementSwaps();
        
        tracker.addArrayAccesses(4); 
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    
    private void buildHeap() {
        
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }
    }

   
    private void minHeapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;

       
        tracker.addArrayAccesses(2); 

        
        if (l < heap.size()) {
            tracker.incrementComparisons(); 
            tracker.addArrayAccesses(2); 
            if (heap.get(l).compareTo(heap.get(smallest)) < 0) {
                smallest = l;
            }
        }

        
        if (r < heap.size()) {
            tracker.incrementComparisons();
            tracker.addArrayAccesses(2); 
            if (heap.get(r).compareTo(heap.get(smallest)) < 0) {
                smallest = r;
            }
        }

        
        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

   
    public void insert(T item) {
        heap.add(item);
        tracker.addArrayAccesses(1); 
        int i = heap.size() - 1;
        
       
        while (i > 0) {
            int p = parent(i);
            tracker.incrementComparisons();
            tracker.addArrayAccesses(2); 
            
            if (heap.get(i).compareTo(heap.get(p)) < 0) {
                swap(i, p);
                i = p;
            } else {
                break;
            }
        }
    }

   
    public T extractMin() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Куча пуста.");
        }

        tracker.addArrayAccesses(1); 
        T min = heap.get(0);
        
        
        if (heap.size() > 1) {
            
            tracker.addArrayAccesses(2); 
            T last = heap.remove(heap.size() - 1);
            heap.set(0, last);
            minHeapify(0);
        } else {
            heap.remove(0);
        }

        return min;
    }

    
    public void decreaseKey(int i, T newValue) {
        if (i < 0 || i >= heap.size()) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона кучи.");
        }
        
        tracker.addArrayAccesses(2); 
        if (newValue.compareTo(heap.get(i)) > 0) {
            throw new IllegalArgumentException("Новое значение должно быть меньше текущего.");
        }

        
        heap.set(i, newValue);
        tracker.addArrayAccesses(1); 

        
        while (i > 0) {
            int p = parent(i);
            tracker.incrementComparisons();
            tracker.addArrayAccesses(2); 

            if (heap.get(i).compareTo(heap.get(p)) < 0) {
                swap(i, p);
                i = p;
            } else {
                break;
            }
        }
    }

   
    public void merge(MinHeap<T> otherHeap) {
        if (otherHeap == null || otherHeap.isEmpty()) {
            return;
        }

       
        heap.addAll(otherHeap.heap);
        
        tracker.addArrayAccesses(otherHeap.size() + 1); 

        buildHeap();
        
        
        otherHeap.heap.clear();
    }
}

package metrics;

public class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long arrayAccesses;

    public PerformanceTracker() {
        reset();
    }

    public void reset() {
        this.comparisons = 0;
        this.swaps = 0;
        this.arrayAccesses = 0;
    }

    
    public void incrementComparisons() {
        this.comparisons++;
    }

    public void incrementSwaps() {
        this.swaps++;
    }

    public void addArrayAccesses(long count) {
        this.arrayAccesses += count;
    }
    
   
    public long getComparisons() {
        return comparisons;
    }

    public long getSwaps() {
        return swaps;
    }

    public long getArrayAccesses() {
        return arrayAccesses;
    }
}

data/
N,DataType,Operations,InitTime_ns,OperationTime_ns,Comparisons,Swaps,ArrayAccesses
100,Random,50,902600,359600,523,251,2802
1000,Random,500,263600,679300,8474,4167,44450
10000,Random,5000,975900,6275100,118313,58308,611474
50000,Random,25000,3409500,8012000,708014,349849,3640122
100000,Random,50000,2773500,19337200,1516072,749882,7781436
100,Sorted,50,17800,15300,519,255,2818
1000,Sorted,500,10100,72800,8415,4121,44056
10000,Sorted,5000,91300,1052000,118072,58064,609528
50000,Sorted,25000,362700,5483600,707678,348178,3629424
100000,Sorted,50000,859100,12148300,1509820,746778,7750308
100,ReverseSorted,50,9800,6300,548,271,2972
1000,ReverseSorted,500,19000,72200,8846,4367,46394
10000,ReverseSorted,5000,150400,827600,122377,60742,634206
50000,ReverseSorted,25000,1329300,6581100,728372,362833,3758742
100000,ReverseSorted,50000,2958600,15795000,1556706,776284,8021116
100,Random,50,2763000,525200,523,256,2832
1000,Random,500,611400,3385800,8456,4163,44390
10000,Random,5000,865300,12742200,118291,58300,611382
50000,Random,25000,4713000,11312700,707865,349832,3639722
100000,Random,50000,4665900,22406800,1516052,749814,7780988
100,Sorted,50,23600,19800,519,255,2818
1000,Sorted,500,23800,180200,8415,4121,44056
10000,Sorted,5000,165600,2286300,118073,58065,609536
50000,Sorted,25000,668200,5739700,707678,348178,3629424
100000,Sorted,50000,737400,12188500,1509820,746779,7750314
100,ReverseSorted,50,9400,6500,546,271,2968
1000,ReverseSorted,500,17800,65600,8848,4370,46416
10000,ReverseSorted,5000,152200,841600,122375,60831,634736
50000,ReverseSorted,25000,1050000,6326500,728351,362872,3758934
100000,ReverseSorted,50000,2998900,16314600,1556735,776304,8021294



README.md

 Deliverables
1. Analysis Report  docs/analysis-report.pdf


Файл: docs/cross-review-summary.md

Empirical Data Validation
Comparison Plot
Swaps Plot
Time Plot (is in the analyssis report)
