// استثناءات المشروع
class BookCatalogException extends Exception {
    public BookCatalogException(String message) {
        super(message);
    }
}

class InvalidISBNException extends BookCatalogException {
    public InvalidISBNException(String msg) { super(msg); }
}

class DuplicateISBNException extends BookCatalogException {
    public DuplicateISBNException(String msg) { super(msg); }
}

class MalformedBookEntryException extends BookCatalogException {
    public MalformedBookEntryException(String msg) { super(msg); }
}

class InsufficientArgumentsException extends BookCatalogException {
    public InsufficientArgumentsException(String msg) { super(msg); }
}

class InvalidFileNameException extends BookCatalogException {
    public InvalidFileNameException(String msg) { super(msg); }
}