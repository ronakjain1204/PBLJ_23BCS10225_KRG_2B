import java.util.ArrayList;
import java.util.Scanner;

class EmptyListException extends Exception {
    public EmptyListException(String message) {
        super(message);
    }
}

public class StudentList {

    private ArrayList<String> students = new ArrayList<>();

    public void addStudent(String name) {
        students.add(name);
        System.out.println("Added: " + name);
    }

    public void removeStudent(String name) throws EmptyListException {
        if (students.isEmpty()) {
            throw new EmptyListException("Cannot remove from an empty list!");
        }
        if (students.remove(name)) {
            System.out.println("Removed: " + name);
        } else {
            System.out.println("Name not found: " + name);
        }
    }

    public void display() {
        System.out.println("Student List: " + students);
    }

    public static void main(String[] args) {
        StudentList list = new StudentList();
        Scanner sc = new Scanner(System.in);

        try {
            list.addStudent("Ayush");
            list.addStudent("Anish");
            list.display();

            list.removeStudent("Ronak");
            list.display();

            list.removeStudent("Ayush"); 

            list.removeStudent("Danish");
            
            list.removeStudent("Swastik");  
            
        } catch (EmptyListException e) {
            System.err.println("Error: " + e.getMessage());
        }

        sc.close();
    }
}
