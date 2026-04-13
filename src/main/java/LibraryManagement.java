import java.util.HashMap;
import java.util.Scanner;

class Library {
    private HashMap<String, Integer> books = new HashMap<>();

    // Add book
    public void addBook(String name, int copies) {
        books.put(name, books.getOrDefault(name, 0) + copies);
        System.out.println(copies + " copies of '" + name + "' added.");
    }

    // View books
    public void viewBooks() {
        if (books.isEmpty()) {
            System.out.println("Library is empty.");
            return;
        }

        System.out.println("\nAvailable Books:");
        for (String book : books.keySet()) {
            System.out.println(book + " → " + books.get(book) + " copies");
        }
    }

    // Borrow book
    public void borrowBook(String name) {
        if (books.containsKey(name) && books.get(name) > 0) {
            books.put(name, books.get(name) - 1);
            System.out.println("You borrowed '" + name + "'.");
        } else {
            System.out.println("'" + name + "' is not available.");
        }
    }

    // Return book
    public void returnBook(String name) {
        books.put(name, books.getOrDefault(name, 0) + 1);
        System.out.println("You returned '" + name + "'.");
    }
}

public class LibraryManagement {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Library lib = new Library();

        while (true) {
            System.out.println("\n--- Library Menu ---");
            System.out.println("1. Add Book");
            System.out.println("2. View Books");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Exit");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // clear buffer

            switch (choice) {
                case 1:
                    System.out.print("Enter book name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter number of copies: ");
                    int copies = sc.nextInt();
                    lib.addBook(name, copies);
                    break;

                case 2:
                    lib.viewBooks();
                    break;

                case 3:
                    System.out.print("Enter book name to borrow: ");
                    String borrowName = sc.nextLine();
                    lib.borrowBook(borrowName);
                    break;

                case 4:
                    System.out.print("Enter book name to return: ");
                    String returnName = sc.nextLine();
                    lib.returnBook(returnName);
                    break;

                case 5:
                    System.out.println("Exiting...");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    }
}