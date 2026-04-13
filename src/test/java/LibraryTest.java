public class LibraryTest {
    public static void main(String[] args) {

        Library lib = new Library();

        // Test 1: Add Books
        System.out.println("TEST 1: Add Books");
        lib.addBook("Java", 3);
        lib.addBook("Python", 2);

        // Test 2: View Books
        System.out.println("\nTEST 2: View Books");
        lib.viewBooks();

        // Test 3: Borrow Book
        System.out.println("\nTEST 3: Borrow Book");
        lib.borrowBook("Java");   // should reduce count
        lib.borrowBook("C++");    // not available

        // Test 4: View After Borrow
        System.out.println("\nTEST 4: View After Borrow");
        lib.viewBooks();

        // Test 5: Return Book
        System.out.println("\nTEST 5: Return Book");
        lib.returnBook("Java");

        // Test 6: Final State
        System.out.println("\nTEST 6: Final Library State");
        lib.viewBooks();
    }
}