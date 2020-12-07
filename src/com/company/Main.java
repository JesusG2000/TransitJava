package com.company;


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.*;

public class Main {
    private static List<String> moves;
    private static String EXIT = "exit";

    public static void main(String[] args) {
        try {
            validateArgs(args);
            String key = generateKey();
            String scriptInput = scriptMove();
            showHmac(key, scriptInput);
            while (true) {
                showMenu();
                Scanner sc = new Scanner(System.in);
                String userInput;
                System.out.println("Enter your move : ");
                if (sc.hasNextInt() && (userInput = isValidUserInput(sc.nextInt())).length() > 0) {
                    if (userInput.equals(EXIT)) {
                        break;
                    }
                    System.out.println("Your move : " + userInput);
                    System.out.println("Computer move : " + scriptInput);
                    List<String> userLoseList = getPrev(userInput);
                    List<String> userWinList = getNext(userInput);

                    if(userWinList.contains(scriptInput)){
                        System.out.println("You win");
                    }
                    if(userLoseList.contains(scriptInput)){
                        System.out.println("You lost");
                    }
                    if(userInput.equals(scriptInput)){
                        System.out.println("Draw");
                    }
                    System.out.println("HMAC KEY : " + key);
                    break;
                } else {
                    System.out.println("Incorrect input");
                }

            }

        } catch (MyException e) {
            System.out.println(e.showMessage());
        }

    }


    private static List<String> getPrev(String userInput) {
        int moveIndex = moves.indexOf(userInput);
        int movesCenter = (moves.size() - 1) / 2;
        int cycleReplaceCount = moveIndex - movesCenter;
        List<String> result;
        if (cycleReplaceCount > 0) {
            result= moveLeft(cycleReplaceCount);
        } else {
            result = moveRight(Math.abs(cycleReplaceCount));
        }
        return result.subList(0,movesCenter);
    }

    private static List<String> moveRight(int cycleReplaceCount) {
        List<String> saveMoves = new LinkedList<>(moves);
        for (int y = 0; y < cycleReplaceCount; y++) {
            for (int i = saveMoves.size()-2; i >-1; i--) {
                String save = saveMoves.get(i);
                saveMoves.set(i, saveMoves.get(i + 1));
                saveMoves.set(i + 1, save);
            }
        }
        return saveMoves;
    }

    private static List<String> moveLeft(int cycleReplaceCount) {
        List<String> saveMoves = new LinkedList<>(moves);
        for (int y = 0; y < cycleReplaceCount; y++) {
            for (int i = 0; i < saveMoves.size() - 1; i++) {
                String save = saveMoves.get(i);
                saveMoves.set(i, saveMoves.get(i + 1));
                saveMoves.set(i + 1, save);
            }
        }
        return saveMoves;
    }

    private static List<String> getNext(String userInput) {
        int moveIndex = moves.indexOf(userInput);
        int movesCenter = (moves.size() - 1) / 2;
        int cycleReplaceCount = moveIndex - movesCenter;
        List<String> result;
        if (cycleReplaceCount > 0) {
            result= moveLeft(cycleReplaceCount);
        } else {
            result = moveRight(Math.abs(cycleReplaceCount));
        }
        return result.subList(movesCenter+1,moves.size());
    }

    private static String isValidUserInput(int input) {
        if (input == 0) {
            return EXIT;
        }
        input--;
        if (input >= 0 && input < moves.size()) {
            return moves.get(input);
        }
        return "";
    }

    private static void showMenu() {
        System.out.println("Available moves");
        for (int i = 0; i < moves.size(); i++) {
            System.out.println(i + 1 + " - " + moves.get(i));
        }
        System.out.println("0 - exit");
    }

    private static void showHmac(String key, String scriptMove) {
        byte[] hmacSha256 = Main.hmac256(key, scriptMove);
        System.out.println(String.format("Hex: %032x", new BigInteger(1, hmacSha256)));
    }

    private static byte[] hmac256(String secretKey, String message) {
        try {
            return hmac256(secretKey.getBytes("UTF-8"), message.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMACSHA256 encrypt", e);
        }
    }

    private static byte[] hmac256(byte[] secretKey, byte[] message) {
        byte[] hmac256 = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec sks = new SecretKeySpec(secretKey, "HmacSHA256");
            mac.init(sks);
            hmac256 = mac.doFinal(message);
            return hmac256;
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate HMACSHA256 encrypt ");
        }
    }


    private static String scriptMove() {
        int index = (int) (Math.random() * moves.size());
        return moves.get(index);
    }

    private static void validateArgs(String[] args) throws MyException {
        if (args.length < 3) {
            throw new MyException(ShowData.NOT_AVAILABLE_ARGS_COUNT);
        }
        if (args.length % 2 == 0) {
            throw new MyException(ShowData.EVEN_ARGS_COUNT);
        }
        Set<String> set = new LinkedHashSet<>(Arrays.asList(args));
        if (args.length != set.size()) {
            throw new MyException(ShowData.NOT_UNIQUE_ARGS);
        }
        moves = new LinkedList<>(Arrays.asList(args));
    }

    private static String generateKey() {
        String key = "";
        for (int i = 0 ; i < 128/8/4 ; i++) {
            key += Integer.toHexString(new SecureRandom().nextInt());
        }
        return key;
    }

}
