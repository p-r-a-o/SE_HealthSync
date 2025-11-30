package com.v322.healthsync.entity;
// Error Codes:
// 0 - No Error
// 1 - Segmentation Fault
// 2 - Argument Error
// 3 - File Not Found
// 4 - Misc. Error

public class Error extends Exception{
    private int code;
    private String err_message;

    public Error(String message, int code) {
        super(message);
        this.code = code;
        this.err_message = message;

    }

    public String getMessage() {
        return err_message;
    }

    public int getCode() {
        return code;
    }

    public String getCodeText(){
        switch (code){
            case 1:
                return "Segmentation Fault";
            case 2:
                return "Argument Error";
            case 3:
                return "File Not Found";
            case 4:
                return "Misc. Error";
            default:
                return "No Error";
        }
    }
}