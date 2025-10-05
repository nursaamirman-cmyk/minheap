package algorithms;

import metrics.PerformanceTracker;
import java.util.ArrayList;
import java.util.List;
import java.util.Collection; 
import java.util.NoSuchElementException;

/**
 * Реализация Min-Heap (двоичной кучи минимума) с поддержкой
 * отслеживания метрик (сравнения, перестановки, обращения к массиву).
 */
public class MinHeap<T extends Comparable<T>> {
    
    // Используем List для хранения кучи (массивное представление)
    private List<T> heap; 
    private PerformanceTracker tracker;

    // Конструктор 1: Для пустой кучи
    public MinHeap(PerformanceTracker tracker) {
        this.heap = new ArrayList<>();
        this.tracker = tracker;
    }
    
    // Конструктор 2: Для построения кучи из существующих элементов (используется для merge)
    public MinHeap(PerformanceTracker tracker, Collection<T> initialElements) {
        this.heap = new ArrayList<>(initialElements);
        this.tracker = tracker;
        buildHeap(); // Вызов оптимизированного построения кучи O(N)
    }

    public int size() {
        return heap.size();
    }
    
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    // Приватные методы для навигации по дереву (0-индексация)
    private int parent(int i) { return (i - 1) / 2; }
    private int left(int i) { return 2 * i + 1; }
    private int right(int i) { return 2 * i + 2; }
    
    // Метод для обмена элементов, регистрирует метрики
    private void swap(int i, int j) {
        tracker.incrementSwaps();
        // Учет обращений к массиву: два get (для чтения) и два set (для записи)
        tracker.addArrayAccesses(4); 
        T temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
    
    // Построение кучи из существующего массива за O(N)
    private void buildHeap() {
        // Начинаем с последнего родительского узла и идем к корню
        for (int i = heap.size() / 2 - 1; i >= 0; i--) {
            minHeapify(i);
        }
    }

    // Восстанавливает свойство Min-Heap, проталкивая элемент вниз (O(log N))
    private void minHeapify(int i) {
        int l = left(i);
        int r = right(i);
        int smallest = i;

        // Учет обращения к массиву: доступ к heap.size() при проверке l < size и r < size
        tracker.addArrayAccesses(2); 

        // 1. Сравнение с левым дочерним элементом
        if (l < heap.size()) {
            tracker.incrementComparisons(); // Регистрируем сравнение
            tracker.addArrayAccesses(2); // Доступ к heap.get(l) и heap.get(smallest)
            if (heap.get(l).compareTo(heap.get(smallest)) < 0) {
                smallest = l;
            }
        }

        // 2. Сравнение с правым дочерним элементом
        if (r < heap.size()) {
            tracker.incrementComparisons(); // Регистрируем сравнение
            tracker.addArrayAccesses(2); // Доступ к heap.get(r) и heap.get(smallest)
            if (heap.get(r).compareTo(heap.get(smallest)) < 0) {
                smallest = r;
            }
        }

        // 3. Если наименьший не корень, меняем и продолжаем рекурсивно
        if (smallest != i) {
            swap(i, smallest);
            minHeapify(smallest);
        }
    }

    // Операция вставки элемента (O(log N))
    public void insert(T item) {
        heap.add(item);
        tracker.addArrayAccesses(1); // Доступ для добавления в конец List
        int i = heap.size() - 1;
        
        // Проталкивание вверх (heapifyUp)
        while (i > 0) {
            int p = parent(i);
            tracker.incrementComparisons();
            tracker.addArrayAccesses(2); // Доступ к heap.get(i) и heap.get(p)
            
            if (heap.get(i).compareTo(heap.get(p)) < 0) {
                swap(i, p);
                i = p;
            } else {
                break;
            }
        }
    }

    // Операция извлечения минимального элемента (O(log N))
    public T extractMin() {
        if (heap.isEmpty()) {
            throw new NoSuchElementException("Куча пуста.");
        }

        tracker.addArrayAccesses(1); // Доступ к корневому элементу
        T min = heap.get(0);
        
        // Переносим последний элемент на место корня
        if (heap.size() > 1) {
            // Доступ: 1 для remove(last), 1 для set(0, last)
            tracker.addArrayAccesses(2); 
            T last = heap.remove(heap.size() - 1);
            heap.set(0, last);
            minHeapify(0);
        } else {
            heap.remove(0);
        }

        return min;
    }

    /**
     * Требуемая операция: Снижает ключ элемента по заданному индексу (decrease-key).
     * Эффективность O(log N).
     * @param i Индекс элемента, который нужно изменить.
     * @param newValue Новое (меньшее) значение.
     */
    public void decreaseKey(int i, T newValue) {
        if (i < 0 || i >= heap.size()) {
            throw new IndexOutOfBoundsException("Индекс вне диапазона кучи.");
        }
        
        tracker.addArrayAccesses(2); // Доступ к i для чтения и сравнения
        if (newValue.compareTo(heap.get(i)) > 0) {
            throw new IllegalArgumentException("Новое значение должно быть меньше текущего.");
        }

        // Устанавливаем новое значение
        heap.set(i, newValue);
        tracker.addArrayAccesses(1); // Доступ для записи

        // Проталкивание вверх (аналогично insert)
        while (i > 0) {
            int p = parent(i);
            tracker.incrementComparisons();
            tracker.addArrayAccesses(2); // Доступ к heap.get(i) и heap.get(p)

            if (heap.get(i).compareTo(heap.get(p)) < 0) {
                swap(i, p);
                i = p;
            } else {
                break;
            }
        }
    }

    /**
     * Требуемая операция: Слияние двух Min-Heap (merge).
     * Выполняется путем объединения списков и построения кучи за O(N_total).
     * @param otherHeap Вторая MinHeap для слияния.
     */
    public void merge(MinHeap<T> otherHeap) {
        if (otherHeap == null || otherHeap.isEmpty()) {
            return;
        }

        // Добавляем все элементы второй кучи к текущей
        heap.addAll(otherHeap.heap);
        // Доступ: N элементов добавляются + одно обращение к массиву для size()
        tracker.addArrayAccesses(otherHeap.size() + 1); 

        // Выполняем построение кучи на объединенном списке (O(N_total))
        buildHeap();
        
        // Очищаем вторую кучу после слияния
        otherHeap.heap.clear();
    }
}