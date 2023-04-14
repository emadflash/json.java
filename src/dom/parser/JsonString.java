package src.dom.parser;

public class JsonString extends JsonType {
    String value;

    public JsonString(String value) {
        this.value = value;
    }

    @Override
    public String GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return String.format("\"%s\"", this.value);
    }
}