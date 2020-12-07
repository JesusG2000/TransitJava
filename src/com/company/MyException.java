package com.company;

public class MyException extends Exception {
    private static final String EXAMPLE = " | solution : ";
    private static final String AVAILABLE_ARGS_COUNT = EXAMPLE + "(rock , paper , scissors) - count >= 3 ";
    private static final String ODD_ARGS_COUNT = AVAILABLE_ARGS_COUNT;
    private static final String UNIQUE_ARGS = EXAMPLE + "(rock , paper , scissors) or (rock , paper , scissors , lizard , spock)";
    private static final String UNSUPPORTED_ERROR = "unsupported error ";
    private String message;

    public MyException(String message) {
        this.message = message;
    }

    String showMessage() {
        switch (message){
            case ShowData.NOT_AVAILABLE_ARGS_COUNT:return message +  AVAILABLE_ARGS_COUNT;
            case ShowData.EVEN_ARGS_COUNT:return message + ODD_ARGS_COUNT;
            case ShowData.NOT_UNIQUE_ARGS:return message + UNIQUE_ARGS;
            default:return UNSUPPORTED_ERROR;
        }
    }
}
