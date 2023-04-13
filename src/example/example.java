package src.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import src.dom.parser.JsonParser;
import src.dom.parser.ParseException;

class Example {
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("./src/example/example.json")));

            try {
                JsonParser parser = new JsonParser(content);
                Object res = parser.ParseObj();
                System.out.println(res);
            } catch (ParseException e) {
                System.out.println(e);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
