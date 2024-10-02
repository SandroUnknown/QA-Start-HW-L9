package utils;

public class ParsingFileName {

    public static String getFileExtension (String fullFileName){
        int lastIndex = fullFileName.lastIndexOf('.');

        return (lastIndex != -1) ?
                fullFileName.substring(lastIndex + 1).toUpperCase():
                fullFileName.toUpperCase();
    }
}