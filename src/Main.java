import java.util.Scanner;

public class Main {
    static String inputLine; // Вводимая строка с числами и арифметической операцией между ними
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        inputLine = scanner.nextLine();
        System.out.println(calc(inputLine));
    }

    static final int maxPossibleRomanValue = 3999; // Максимальное допустимое значение для числа в римской записи
    static final int maxValue = 10; // Максимальное допустимое значение числа в строке ввода
    static int calcDigitMode = -1; // Режим работы калькулятора: -1: не определён, 0: арабская система, 1: римская
    static final char[][] digit = { // Система записи цифр:
            {'0','1','2','3','4','5','6','7','8','9'}, // digit[0][]: арабские цифры
            {'I','V','X','L','C','D','M'}};            // digit[1][]: римские цифры
    static final char[] operation = {'+','-','*','/'}; // Символы арифметических операций
    static final int[] romanDigitValue = {1, 5, 10, 50, 100, 500, 1000}; // Значения для римских цифр от 'I' до 'M'

    public static String calc(String input) {// Получение результата арифметической операции в строке input
        int resultValue = 0; // Результат арифметической операции
        int operationCharIndex = -1; // Индекс символа арифметической операции в operation[]
        for (char c : operation) {
            operationCharIndex = input.indexOf(c); // Поиск символа арифметической операции
            if (operationCharIndex >= 0) { // Символ арифметической операции найден
                int number1 = getNumber(0, operationCharIndex); // Определение первого числа в строке ввода
                int number2 = getNumber(operationCharIndex + 1, input.length()); // Определение второго числа
                resultValue = switch (input.charAt(operationCharIndex)) { // Проведение арифметической операции
                    case '+' -> number1 + number2;
                    case '-' -> number1 - number2;
                    case '*' -> number1 * number2;
                    case '/' -> number1 / number2;
                    default -> 0;
                };
                break;
            }
        }
        if(operationCharIndex < 0) throw new RuntimeException(); // Символ арифметической операции не найден
        if(calcDigitMode == 0) return Integer.toString(resultValue); // Арабская система записи цифр
        else return intToRoman(resultValue); // Римская система записи цифр
    }

    static int getNumber(int numberFirstCharIndex, int numberNextToLastCharIndex) { // Преобразование подстроки в значение integer
        String number = inputLine.substring(numberFirstCharIndex, numberNextToLastCharIndex).trim();
        int result = switch (getDigitMode(number.charAt(0))) {
            case 0 -> Integer.parseInt(number); // Арабская система записи цифр
            case 1 -> romanToInt(number); // Римская система записи цифр
            default -> throw new RuntimeException(); // Цифры не обнаружены
        };
        if(result < 1 || result > maxValue) throw new RuntimeException();
        return result;
    }

    static int getDigitMode(char numberFirstChar) { // Определение системы записи цифр для символа numberFirstChar
        for (int i = 0; i < digit.length; i++)      // и установление системы записи цифр для калькулятора
            for (int j = 0; j < digit[i].length; j++)
                if (numberFirstChar == digit[i][j]) {
                    if (calcDigitMode < 0) calcDigitMode = i; // Установление системы записи цифр для калькулятора
                    else if (calcDigitMode != i) return -1; // Одновременное использование арабских и римских цифр
                    return i;
                }
        return -1; // Символ numberFirstChar не является арабской или римской цифрой
    }

    static String intToRoman(int numberToConvert) { // Преобразование значения integer в строку римских цифр
        if(numberToConvert < 1 || numberToConvert > maxPossibleRomanValue) throw new RuntimeException();
        StringBuilder result = new StringBuilder();
        for(int i = digit[1].length - 1; i >= 0; i--) {
            int digitCount = numberToConvert / romanDigitValue[i]; // Счётчик последовательно повторяющихся цифр
            int remainValue = numberToConvert % romanDigitValue[i];
            if (digitCount > 0) { // Римская цифра digit[1][i] используется (записывается) digitCount раз
                result.append(String.valueOf(digit[1][i]).repeat(digitCount));
                numberToConvert %= romanDigitValue[i] * digitCount; // Получение остатка для записи меньшими цифрами
            }
            int k = 2 - i % 2; // Режим добавления вычитаемых впереди идущих меньших цифр
            if ((k == 1 || i >= 2) && remainValue >= romanDigitValue[i] - romanDigitValue[i - k]) {
                result.append(digit[1][i - k]).append(digit[1][i]);
                numberToConvert += romanDigitValue[i - k] - romanDigitValue[i];
            }
        }
        return result.toString();
    }

    static int romanToInt(String stringToConvert) { // Преобразование строки римских цифр в значение integer
        int result = 0;
        int digitCount = 1; // Счётчик последовательно повторяющихся цифр
        int maxDigitIndex = -1; // Индекс в digit[1][] максимальной римской цифры из stringToConvert
        int subtractionCount = 0; // Счётчик вычитаний для впереди стоящей меньшей цифры
        int lastDigitIndex = -1; // Индекс в digit[1][] последнего обработанного символа из stringToConvert
        for (int i = stringToConvert.length() - 1; i >= 0; i--) { // Посимвольное сканирование с конца в начало
            int digitIndex = -1; // Индекс в digit[1][] текущего символа из stringToConvert
            for (int j = 0; j < digit[1].length; j++) {
                if (stringToConvert.charAt(i) == digit[1][j]) {
                    maxDigitIndex = Integer.max(maxDigitIndex, j);
                    digitIndex = j;
                    if (j == lastDigitIndex) { // Цифра повторяется
                        if (j % 2 != 0) throw new RuntimeException(); // Повторяться могут только I, X, C, M
                        digitCount++;
                        if (digitCount > 3) // Цифры могут последовательно повторяться не более трёх раз подряд
                            throw new RuntimeException();
                        if (j < maxDigitIndex)
                            throw new RuntimeException(); // Не могут повторяться меньшие цифры, идущие перед большими
                    } else { // Цифра не повторяется
                        digitCount = 1; // Сброс счётчика повторений
                        if (j < maxDigitIndex) { // Впереди стоящая меньшая цифра вычитается
                            subtractionCount++;
                            if(j % 2 != 0 ||                  // Вычитаться могут только чётные элементы digit[1][]:
                                    j < lastDigitIndex - 2 ||      // I, X, C, (M); индекс в digit[1][] вычитаемой цифры должен
                                    subtractionCount > 1)          // быть на 1 или 2 меньше, чем последующей; вычитаться могут
                                throw new RuntimeException(); // только по одной цифре в комбинации "меньшая + большая"
                        } else subtractionCount = 0; // Сброс счётчика вычитаний
                    }
                    if (j >= maxDigitIndex) result += romanDigitValue[j]; // Цифра, бóльшая (или равная), чем последующая
                    else result -= romanDigitValue[j];                    // прибавляется, меньшая, чем последующая - вычитается
                    lastDigitIndex = j;
                    break;
                }
            }
            if (digitIndex < 0) throw new RuntimeException(); // Не является римской цифрой
        }
        return result;
    }
}