package src.dom.parser;

public class JsonNull extends JsonType {
    public JsonNull() {
    }

    @Override
    public Object GetValue() {
        return null;
    }

    public String toString() {
        return "null";
    }
}
