package src.dom.parser;

public class JsonNull extends JsonType {
    public JsonNull() {
        super(JsonType.Type.JSON_NULL);
    }

    @Override
    public Object GetValue() {
        return null;
    }

    public String toString() {
        return "null";
    }
}
