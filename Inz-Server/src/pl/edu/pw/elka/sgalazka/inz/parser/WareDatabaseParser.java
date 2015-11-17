package pl.edu.pw.elka.sgalazka.inz.parser;

import pl.edu.pw.elka.sgalazka.inz.serial.DLLCommandProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by gałązka on 2015-11-15.
 */
public class WareDatabaseParser {

    public static boolean parseWareDatabaseFile(){
        String fileName = DLLCommandProcessor.databaseFileName;

        FileReader input = null;
        try {
            input = new FileReader(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader bufRead = new BufferedReader(input);
        String myLine = null;

        try {
            while ( (myLine = bufRead.readLine()) != null)
            {
                if(myLine.charAt(0)=='$'){

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

}
