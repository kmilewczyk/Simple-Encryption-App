package pl.milewczyk.karol.crypto;

import java.io.IOException;

public class FileFormatException extends IOException {
    FileFormatException(String context){
        super(context);
    }
}
