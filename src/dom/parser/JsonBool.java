package src.dom.parser;

public class JsonBool extends JsonType {
    public boolean value;

    public JsonBool(boolean value) {
        this.value = value;
    }

    @Override
    public Boolean GetValue() {
        return this.value;
    }

    public String toString() {
        return this.value ? "true" : "false";
    }
}