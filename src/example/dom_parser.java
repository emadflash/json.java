package src.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import src.dom.parser.JsonParser;
import src.dom.parser.JsonObj;
import src.dom.parser.JsonType;
import src.dom.parser.JsonString;
import src.dom.parser.ParseException;

class Example {
    public static void main(String[] args) {
        try {
            String content = new String(Files.readAllBytes(Paths.get("./src/example/example.json")));

            try {
                JsonParser parser = new JsonParser(content);
                JsonObj res = parser.ParseObj();

                HashMap<String, JsonType> hm = res.GetValue();
                JsonType name = hm.get("name");
                JsonType email = hm.get("email");
                if (name instanceof JsonString) {
                    System.out.println("Name: " + name);
                } else {
                    System.out.println("error: expected a string type");
                }

                if (email instanceof JsonString) {
                    System.out.println("Email: " + email);
                } else {
                    System.out.println("error: expected a string type");
                }
            } catch (ParseException e) {
                System.out.println(e);
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
