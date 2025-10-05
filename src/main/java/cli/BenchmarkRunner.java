package cli;

import algorithms.MinHeap;
import metrics.PerformanceTracker;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BenchmarkRunner {

    // Запуск бенчмарка для разных размеров N
    public static void main(String[] args) {
        // Создаем папку docs, если её нет
        new java.io.File("docs").mkdirs();
        System.out.println("Starting MinHeap Benchmarks...");
        
        // Размеры N для тестирования (от 10^2 до 10^5) 
        int[] inputSizes = {100, 1000, 10000, 50000, 100000};
        
        // Типы входных данных для тестирования
        String[] dataTypes = {"Random", "Sorted", "ReverseSorted"}; 

        for (String dataType : dataTypes) {
            System.out.println("\nTesting Data Type: " + dataType);
            for (int N : inputSizes) {
                runBenchmark(N, dataType);
            }
        }
        
        System.out.println("\nBenchmarks completed. Results saved to docs/performance-data.csv");
    }

    private static void runBenchmark(int N, String dataType) {
        // 1. Подготовка данных
        List<Integer> data = generateData(N, dataType);
        PerformanceTracker tracker = new PerformanceTracker();

        // 2. Инициализация (Построение кучи из N элементов)
        long startTime = System.nanoTime();
        // Используем конструктор с buildHeap для анализа построения кучи
        MinHeap<Integer> heap = new MinHeap<>(tracker, data); 
        long initializationTime = System.nanoTime() - startTime;

        // 3. Операция extractMin (тестирование основной работы)
        tracker.reset(); // Сбрасываем метрики для чистого измерения extractMin
        startTime = System.nanoTime();
        
        // Извлекаем M элементов (N/2)
        int M = N / 2;
        for (int i = 0; i < M; i++) {
            if (!heap.isEmpty()) {
                 heap.extractMin();
            }
        }
        long operationTime = System.nanoTime() - startTime;

        // 4. Запись результатов
        writeResults(N, dataType, M, initializationTime, operationTime, tracker);
        
        System.out.printf("   N=%d (%s): Time=%.2f ms, Comparisons=%d, Swaps=%d, ArrayAccesses=%d\n", 
            N, dataType, (double)operationTime / 1_000_000.0, 
            tracker.getComparisons(), tracker.getSwaps(), tracker.getArrayAccesses());
    }

    private static List<Integer> generateData(int N, String type) {
        List<Integer> data = new ArrayList<>(N);
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            data.add(rand.nextInt(N * 10)); // Случайные числа
        }

        switch (type) {
            case "Sorted":
                Collections.sort(data);
                break;
            case "ReverseSorted":
                Collections.sort(data, Collections.reverseOrder());
                break;
        }
        return data;
    }

    private static void writeResults(int N, String dataType, int operations, long initTime, long opTime, PerformanceTracker tracker) {
        String filename = "docs/performance-data.csv";
        boolean fileExists = new java.io.File(filename).exists();
        
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, true))) {
            // Заголовок (если файл только что создан)
            if (!fileExists) {
                pw.println("N,DataType,Operations,InitTime_ns,OperationTime_ns,Comparisons,Swaps,ArrayAccesses");
            }
            // Данные
            pw.printf("%d,%s,%d,%d,%d,%d,%d,%d\n",
                N, dataType, operations, initTime, opTime, 
                tracker.getComparisons(), tracker.getSwaps(), tracker.getArrayAccesses());
        } catch (IOException e) {
            System.err.println("Error writing to CSV file: " + e.getMessage());
        }
    }
}