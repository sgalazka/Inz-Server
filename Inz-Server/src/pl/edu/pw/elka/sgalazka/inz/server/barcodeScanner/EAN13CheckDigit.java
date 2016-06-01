package pl.edu.pw.elka.sgalazka.inz.server.barcodeScanner;

/**
 * Created by gałązka on 2016-01-13.
 */
public class EAN13CheckDigit {
    public static int calculate(String barcode) {

        char code[] = barcode.toCharArray();

        int sum1 = Integer.parseInt(code[1] + "") + Integer.parseInt(code[3] + "") + Integer.parseInt(code[5] + "")
                + Integer.parseInt(code[7] + "") + Integer.parseInt(code[9] + "") + Integer.parseInt(code[11] + "");
        int sum2 = Integer.parseInt(code[0] + "") + Integer.parseInt(code[2] + "") + Integer.parseInt(code[4] + "")
                + Integer.parseInt(code[6] + "") + Integer.parseInt(code[8] + "") + Integer.parseInt(code[10] + "");

        int checksum_value = 3 * sum1 + sum2;
        int checksum_digit = 10 - (checksum_value % 10);
        if (checksum_digit == 10) checksum_digit = 0;

        return checksum_digit;
    }
}
