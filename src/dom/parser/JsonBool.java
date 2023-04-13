package src.dom.parser;

public class JsonBool extends JsonType {
    public boolean value;

    public JsonBool(boolean value) {
        super(JsonType.Type.JSON_BOOL);
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