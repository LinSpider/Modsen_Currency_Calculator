import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;

public class CurrencyCalculator {
    private static final String DOLLAR_SYMBOL = "$";
    private static final String RUBLE_SYMBOL = "p";

    private static BigDecimal exchangeRate;

    public static void main(String[] args) {
        loadExchangeRate();

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter expression: ");
        String expression = scanner.nextLine();

        BigDecimal result = evaluateExpression(expression);
        String formattedResult = formatCurrency(result, DOLLAR_SYMBOL);

        System.out.println("Result: " + formattedResult);
    }

    private static void loadExchangeRate() {
        try {
            Scanner scanner = new Scanner(new File("D:\\NewLabs\\prakt\\calc\\src\\config.txt"));
            exchangeRate = new BigDecimal(scanner.nextLine());
        } catch (FileNotFoundException e) {
            System.err.println("Error: config file not found");
            System.exit(1);
        }
    }

    private static BigDecimal evaluateExpression(String expression) {
        String[] tokens = expression.split("\\s+");
        BigDecimal result = null;

        for (String token : tokens) {
            if (token.startsWith(DOLLAR_SYMBOL)) {
                BigDecimal value = parseCurrency(token.substring(1));
                result = (result == null) ? value : result.add(value);
            } else if (token.endsWith(RUBLE_SYMBOL)) {
                BigDecimal value = parseCurrency(token.substring(0, token.length() - 1));
                result = (result == null) ? value : result.add(convertToDollars(value));
            } else if (token.equals("+")) {
                // do nothing
            } else if (token.equals("-")) {
                // negate the next value
                result = (result == null) ? BigDecimal.ZERO : result;
                result = result.subtract(evaluateExpression(tokens[1])).setScale(2, RoundingMode.HALF_UP);
            } else {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        return result;
    }

    private static BigDecimal parseCurrency(String s) {
        return new BigDecimal(s.replaceAll(",", ""));
    }

    private static BigDecimal convertToDollars(BigDecimal rubles) {
        return rubles.divide(exchangeRate, 2, RoundingMode.HALF_UP);
    }

    private static String formatCurrency(BigDecimal value, String symbol) {
        return symbol + value.setScale(2, RoundingMode.HALF_UP).toString();
    }
}