package com.company;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class Main {

    private static final String[] CONSTS = {
            "428A2F98", "71374491", "B5C0FBCF", "E9B5DBA5", "3956C25B", "59F111F1", "923F82A4", "AB1C5ED5",
            "D807AA98", "12835B01", "243185BE", "550C7DC3", "72BE5D74", "80DEB1FE", "9BDC06A7", "C19BF174",
            "E49B69C1", "EFBE4786", "0FC19DC6", "240CA1CC", "2DE92C6F", "4A7484AA", "5CB0A9DC", "76F988DA",
            "983E5152", "A831C66D", "B00327C8", "BF597FC7", "C6E00BF3", "D5A79147", "06CA6351", "14292967",
            "27B70A85", "2E1B2138", "4D2C6DFC", "53380D13", "650A7354", "766A0ABB", "81C2C92E", "92722C85",
            "A2BFE8A1", "A81A664B", "C24B8B70", "C76C51A3", "D192E819", "D6990624", "F40E3585", "106AA070",
            "19A4C116", "1E376C08", "2748774C", "34B0BCB5", "391C0CB3", "4ED8AA4A", "5B9CCA4F", "682E6FF3",
            "748F82EE", "78A5636F", "84C87814", "8CC70208", "90BEFFFA", "A4506CEB", "BEF9A3F7", "C67178F2"
    };

    private static String[] H = {
            "6A09E667", "BB67AE85", "3C6EF372", "A54FF53A", "510E527F", "9B05688C", "1F83D9AB", "5BE0CD19"
    };

    public static void main(String[] args) throws NoSuchAlgorithmException {
        String message = "kazancev";
        String binaryMessage = "";
        for (int i = 0; i < message.length(); i++) {
            binaryMessage += Integer.toBinaryString(message.charAt(i));
        }
        // добавляем единичный бит в сообщение
        binaryMessage += "1";
        // дополняем сообщение 0 до длинны 448 по модулю 512
        binaryMessage = addZerosToEnd(binaryMessage);
        // добавляем длину сообщения
        binaryMessage += addZerosToStart(Integer.toBinaryString(message.length()));

        String[] arr = new String[64];
        int start = 0, end = 32;
        for (int i = 0; i < 16; i++) {
            arr[i] = binaryMessage.substring(start, end);
            start = end;
            end += 32;
        }
        for (int i = 16; i < 64; i++) {
            String s0 = XOR(XOR(rotr(arr[i - 15], 7), rotr(arr[i - 15], 18)), shr(arr[i - 15], 3));
            String s1 = XOR(XOR(rotr(arr[i - 2], 17), rotr(arr[i - 2], 19)), shr(arr[i - 2], 10));
            arr[i] = binAdd(new String[] {arr[i - 16], s0, arr[i - 7], s1});
        }

        String a = H[0];
        String b = H[1];
        String c = H[2];
        String d = H[3];
        String e = H[4];
        String f = H[5];
        String g = H[6];
        String h = H[7];
        String binA = new BigInteger(a, 16).toString(2);
        String binB = new BigInteger(b, 16).toString(2);;
        String binC = new BigInteger(c, 16).toString(2);;
        String binD = new BigInteger(d, 16).toString(2);;
        String binE = new BigInteger(e, 16).toString(2);;
        String binF = new BigInteger(f, 16).toString(2);;
        String binG = new BigInteger(g, 16).toString(2);;
        String binH = new BigInteger(h, 16).toString(2);;

        for (int i = 0; i < 64; i++) {
            String sigma0 = XOR(XOR(rotr(binA, 2), rotr(binA, 13)), rotr(binA, 22));
            String Ma = XOR(XOR(binAnd(binA, binB), binAnd(binA, binC)), binAnd(binB, binC));
            String t2 = binAdd(new String[] {sigma0, Ma});
            String sigma1 = XOR(XOR(rotr(binE, 6), rotr(binE, 11)), rotr(binE, 25));
            String Ch = XOR(binAnd(binE, binF), binAnd(binNot(binE), binG));
            String t1 = binAdd(new String[] {binH, sigma1, Ch, new BigInteger(CONSTS[i], 16).toString(2), arr[i]});

            binH = binG;
            binG = binF;
            binF = binE;
            binE = binAdd(new String[] {binD, t1});
            binD = binC;
            binC = binB;
            binB = binA;
            binA = binAdd(new String[] {t1, t2});
        }

        H[0] = binAdd(new String[] {new BigInteger(H[0], 16).toString(2), binA});
        H[1] = binAdd(new String[] {new BigInteger(H[1], 16).toString(2), binB});
        H[2] = binAdd(new String[] {new BigInteger(H[2], 16).toString(2), binC});
        H[3] = binAdd(new String[] {new BigInteger(H[3], 16).toString(2), binD});
        H[4] = binAdd(new String[] {new BigInteger(H[4], 16).toString(2), binE});
        H[5] = binAdd(new String[] {new BigInteger(H[5], 16).toString(2), binF});
        H[6] = binAdd(new String[] {new BigInteger(H[6], 16).toString(2), binG});
        H[7] = binAdd(new String[] {new BigInteger(H[7], 16).toString(2), binH});

        String sha256 = "";
        for (int i = 0; i < H.length; i++) {
            sha256 += new BigInteger(H[i], 2).toString(16);
        }

        print(message + ": " + sha256);

    }

    private static String binNot(String bin) {
        String result = "";
        for (int i = 0; i < bin.length(); i++) {
            result += bin.charAt(i) == '0' ? "1" : "0";
        }
        return result;
    }

    private static String binAnd(String binary1, String binary2) {
        while (binary1.length() != binary2.length()) {
            if (binary1.length() > binary2.length()) binary2 = "0" + binary2;
            else binary1 = "0" + binary1;
        }
        String result = "";
        boolean[] bool1 = toBoolArr(binary1);
        boolean[] bool2 = toBoolArr(binary2);
        for (int i = 0; i < bool1.length; i++) {
            result += bool1[i] & bool2[i] == false ? "0" : "1";
        }
        return result;
    }

    private static String binAdd(String[] arrayList) {
        BigInteger sum = new BigInteger("0");
        for (int i = 0; i < arrayList.length; i++) {
            BigInteger num = new BigInteger(arrayList[i], 2);
            sum = sum.add(num);
        }
        return sum.toString(2);
    }

    private static String shr(String word, int pos) {
        boolean[] arrBoolean = new boolean[16];
        for (int i = 0; i < arrBoolean.length; i++) {
            arrBoolean[i] = word.charAt(i) == '0' ? false : true;
        }
        for (int i = 0; i < pos; i++) {
            boolean[] temp = arrBoolean;
            arrBoolean = new boolean[16];
            for (int j = 1; j < arrBoolean.length; j++) {
                arrBoolean[j] = temp[j - 1];
            }
        }
        return toString(arrBoolean);
    }

    private static String rotr(String word, int pos) {
        boolean[] arrBoolean = new boolean[word.length()];
        for (int i = 0; i < arrBoolean.length; i++) {
            arrBoolean[i] = word.charAt(i) == '0' ? false : true;
        }
        for (int i = 0; i < pos; i++) {
            boolean lastItem = arrBoolean[arrBoolean.length - 1];
            boolean[] temp = arrBoolean;
            arrBoolean = new boolean[word.length()];
            for (int j = 1; j < arrBoolean.length; j++) {
                arrBoolean[j] = temp[j - 1];
            }
            arrBoolean[0] = lastItem;
        }
        return toString(arrBoolean);
    }

    private static String XOR(String binary1, String binary2) {
        while (binary1.length() != binary2.length()) {
            if (binary1.length() > binary2.length()) binary2 = "0" + binary2;
            else binary1 = "0" + binary1;
        }

        String result = "";
        for (int i = 0; i < binary1.length(); i++) {
            boolean b1 = binary1.charAt(i) == '0' ? false : true;
            boolean b2 = binary2.charAt(i) == '0' ? false : true;
            result += b1 ^ b2 == false ? "0" : "1";
        }
        return result;
    }

    private static String addZerosToStart(String binaryString) {
        while (binaryString.length() < 64) {
            binaryString = "0" + binaryString;
        }
        return binaryString;
    }

    private static String addZerosToEnd(String binaryString) {
        int k = 0;
        while (true) {
            if ((binaryString.length() + k) % 512 == 448) {
                break;
            } else k++;
        }
        for (int i = 0; i < k; i++) {
            binaryString += "0";
        }
        return binaryString;
    }

    private static boolean[] toBoolArr(String str) {
        boolean[] result = new boolean[str.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = str.charAt(i) == '0' ? false : true;
        }
        return result;
    }

    private static String toString(boolean[] arr) {
        String result = "";
        for (int i = 0; i < arr.length; i++) {
            result += arr[i] == false ? "0" : "1";
        }
        return result;
    }

    private static void print(String[] arr) {
        for (int i = 0; i < arr.length; i++) {
            print(arr[i]);
        }
    }

    private static void print(String text) {
        System.out.println(text);
    }
}
