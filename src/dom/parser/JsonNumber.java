package src.dom.parser;

public class JsonNumber extends JsonType {
    double value;

    public JsonNumber(double value) {
        super(JsonType.Type.JSON_NUMBER);
        this.value = value;
    }

    @Override
    public Number GetValue() {
        return this.value;
    }

    public String toString() {
        return String.format("%.2f", this.value);
    }
}
