import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AverageCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Integer> numbers = new ArrayList<>();
        
        try {
            System.out.print("Enter the number of elements: ");
            int n = scanner.nextInt();

            System.out.println("Enter " + n + " integers:");

            for (int i = 0; i < n; i++) {
                numbers.add(scanner.nextInt());
            }

            // Calculate average inside try block
            if (numbers.size() == 0) {
                throw new ArithmeticException("List is empty. Cannot divide by zero.");
            }

            int sum = 0;
            for (int num : numbers) {
                sum += num;
            }

            double average = (double) sum / numbers.size();
            System.out.println("Average: " + average);
        
        } catch (InputMismatchException e) {
            System.out.println("Error: Please enter only integers!");
        } catch (ArithmeticException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            scanner.close();
            System.out.println("Program finished.");
        }
    }
}
