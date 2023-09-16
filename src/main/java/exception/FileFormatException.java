package exception;

public class FileFormatException extends Exception {
    public FileFormatException(String line, int lineNumber) {
        System.err.println("ОШИБКА: файл не может быть правильно отформатирован. Проверьте синтаксис.");
        System.err.println("СОВЕТ: проверьте строку" + "(" + lineNumber + ") | " + line);
        System.exit(-1);
    }
}
