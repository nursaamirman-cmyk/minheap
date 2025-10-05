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

    // Методы для увеличения счетчиков
    public void incrementComparisons() {
        this.comparisons++;
    }

    public void incrementSwaps() {
        this.swaps++;
    }

    // Множественное добавление для обращений к массиву (используется в MinHeap)
    public void addArrayAccesses(long count) {
        this.arrayAccesses += count;
    }
    
    // Методы для получения счетчиков (используются в BenchmarkRunner)
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