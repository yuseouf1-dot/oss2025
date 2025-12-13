import java.io.*;
import java.util.*;


public class Main {

    public static void createTestFile(String filename, int n, String mode) {
        System.out.println("Creating file: " + filename + " (" + n + " lines)...");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            Random rand = new Random();
            
            if (mode.equals("RANDOM")) {
                 for (int i = 0; i < n; i++) {
                    String type = rand.nextBoolean() ? "BUY" : "SELL";
                    int price = 100 + rand.nextInt(100); 
                    writer.write(type + "," + price);
                    writer.newLine();
                }
            } else if (mode.equals("WORST")) {
                for (int i = 0; i < n / 2; i++) {
                    writer.write("BUY," + i); 
                    writer.newLine();
                }
                for (int i = n / 2; i < n; i++) {
                    writer.write("SELL,0");
                    writer.newLine();
                }
            }
            System.out.println("-> Success! File created at: " + new File(filename).getAbsolutePath());
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Order> loadOrdersFromFile(String filename) {
        List<Order> orders = new ArrayList<>();
        int idCounter = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if(parts.length < 2) continue; 

                String type = parts[0];
                int price = Integer.parseInt(parts[1]);
                
                orders.add(new Order(idCounter++, type, price, System.nanoTime()));
            }
        } catch (IOException e) {
            System.err.println("파일을 찾을 수 없습니다: " + filename);
        }
        return orders;
    }

    public static void runTest(String testName, List<Order> data) {
        System.out.println("\n=========================================");
        System.out.println("Test: " + testName + " (Size=" + data.size() + ")");
        System.out.println("=========================================");

        if (data.size() > 30000) System.out.print("Running List... ");
        ListEngine listEngine = new ListEngine();
        long start = System.currentTimeMillis();
        for (Order o : data) {
            listEngine.addOrder(o);
            listEngine.matchOrders();
        }
        long listTime = System.currentTimeMillis() - start;
        System.out.println("List Time: " + listTime + " ms");

        System.out.print("Running BST... ");
        BSTEngine bstEngine = new BSTEngine();
        start = System.currentTimeMillis();
        for (Order o : data) {
            bstEngine.addOrder(o);
            bstEngine.matchOrders();
        }
        long bstTime = System.currentTimeMillis() - start;
        System.out.println("BST Time : " + bstTime + " ms");

        System.out.print("Running Heap... ");
        HeapEngine heapEngine = new HeapEngine();
        start = System.currentTimeMillis();
        for (Order o : data) {
            heapEngine.addOrder(o);
            heapEngine.matchOrders();
        }
        long heapTime = System.currentTimeMillis() - start;
        System.out.println("Heap Time: " + heapTime + " ms");
        
        System.out.println("-----------------------------------------");
        System.out.println("CSV: " + testName + ", " + data.size() + ", " + listTime + ", " + bstTime + ", " + heapTime);
    }

    public static void main(String[] args) {
    	int N = 40000; 

        String randomFileName = "data_random.txt";
        String worstFileName = "data_worst.txt";

        createTestFile(randomFileName, N, "RANDOM");
        createTestFile(worstFileName, N, "WORST");

        runTest("Random(File)", loadOrdersFromFile(randomFileName));
        runTest("Worst(File)", loadOrdersFromFile(worstFileName));
    }
}