package cms.mortalchen.ihatekotlin;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * I Hate Kotlin
 * @author Mortal
 */
public class KtReaderHelper {

    /**
     * Kotlin's InputStreamReader
     * @return "pure text"
     */
    public static String readText(InputStreamReader reader) throws IOException {
        int buffer;
        StringBuilder stringBuilder = new StringBuilder();
        while ((buffer = reader.read()) != -1){
            stringBuilder.append((char) buffer);
        }
        return stringBuilder.toString();
    }
}
