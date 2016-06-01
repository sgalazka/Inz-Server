package pl.edu.pw.elka.sgalazka.inz.server.Log;

/**
 * Created by ga��zka on 2015-10-10.
 */
public class LogType {

    private char type;
    private String message;

    public LogType(char type, String message){
        this.type = type;
        this.message = message;
    }
    public LogType(){}

    public char getType() {
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
