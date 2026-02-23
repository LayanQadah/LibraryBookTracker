import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

// فئة الكتاب 
class Book implements Comparable<Book> {
    String title, author, isbn;
    int copies;

    public Book(String t, String a, String i, int c) {
        this.title = t; this.author = a; this.isbn = i; this.copies = c;
    }

    @Override
    public int compareTo(Book other) {
        return this.title.compareToIgnoreCase(other.title);
    }
}

public class LibraryBookTracker {
    static ArrayList<Book> list = new ArrayList<>();
    static int validCount = 0, searchResults = 0, booksAdded = 0, errorCount = 0;

    public static void main(String[] args) {
        String filePath = "";
        try {
            // التأكد من المدخلات
            if (args.length < 2) throw new InsufficientArgumentsException("Missing arguments.");
            
            filePath = args[0];
            if (!filePath.endsWith(".txt")) throw new InvalidFileNameException("File must be .txt");

            File f = new File(filePath);
            if (!f.exists()) {
                if (f.getParentFile() != null) f.getParentFile().mkdirs();
                f.createNewFile();
            }

           
            Scanner sc = new Scanner(f);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.trim().isEmpty()) continue;
                try {
                    list.add(parseLine(line));
                    validCount++;
                } catch (Exception e) {
                    logError(line, e);
                    errorCount++;
                }
            }
            sc.close();

            String op = args[1];
            // تنفيذ العملية المطلوبة
            if (op.contains(":")) {
                // إضافة كتاب جديد
                Book newBook = parseLine(op);
                list.add(newBook);
                Collections.sort(list);
                saveToFile(f);
                printHeader();
                printRow(newBook);
                booksAdded = 1;
            } else if (op.length() == 13 && op.matches("\\d+")) {
                // بحث برقم ISBN
                printHeader();
                int foundCount = 0;
                for (Book b : list) {
                    if (b.isbn.equals(op)) {
                        printRow(b);
                        foundCount++;
                    }
                }
                if (foundCount > 1) throw new DuplicateISBNException("ISBN duplicated in file.");
                searchResults = foundCount;
            } else {
                // بحث بالعنوان
                printHeader();
                for (Book b : list) {
                    if (b.title.toLowerCase().contains(op.toLowerCase())) {
                        printRow(b);
                        searchResults++;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            logError(args.length > 1 ? args[1] : "System", e);
            errorCount++;
        } finally {
          
            System.out.println("\n--- Statistics ---");
            System.out.println("Valid records processed: " + validCount);
            System.out.println("Search results: " + searchResults);
            System.out.println("Books added: " + booksAdded);
            System.out.println("Errors encountered: " + errorCount);
            System.out.println("Thank you for using the Library Book Tracker.");
        }
    }


    static Book parseLine(String s) throws Exception {
        String[] p = s.split(":");
        if (p.length != 4) throw new MalformedBookEntryException("Incomplete data");
        
        String t = p[0].trim();
        String a = p[1].trim();
        String i = p[2].trim();
        int c;

        if (t.isEmpty() || a.isEmpty()) throw new MalformedBookEntryException("Empty Title/Author");
        if (i.length() != 13) throw new InvalidISBNException("ISBN must be 13 digits");
        
        try {
            c = Integer.parseInt(p[3].trim());
            if (c <= 0) throw new Exception();
        } catch (Exception e) {
            throw new MalformedBookEntryException("Copies must be > 0");
        }
        return new Book(t, a, i, c);
    }

    static void saveToFile(File f) throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(f));
        for (Book b : list) {
            pw.println(b.title + ":" + b.author + ":" + b.isbn + ":" + b.copies);
        }
        pw.close();
    }

    static void printHeader() {
        System.out.printf("%-30s %-20s %-15s %5s\n", "Title", "Author", "ISBN", "Copies");
        System.out.println("---------------------------------------------------------------------------");
    }

    static void printRow(Book b) {
        System.out.printf("%-30s %-20s %-15s %5d\n", b.title, b.author, b.isbn, b.copies);
    }

    static void logError(String data, Exception e) {
        try (PrintWriter pw = new PrintWriter(new FileWriter("errors.log", true))) {
            pw.println("[" + LocalDateTime.now() + "] INVALID: " + data + " | " + e.toString());
        } catch (IOException ignored) {}
    }
}