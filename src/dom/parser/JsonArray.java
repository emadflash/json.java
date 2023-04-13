package src.dom.parser;

import java.util.ArrayList;

public class JsonArray extends JsonType {
    private ArrayList<JsonType> value;

    public JsonArray(ArrayList<JsonType> value) {
        super(JsonType.Type.JSON_ARRAY);
        this.value = value;
    }

    @Override
    public ArrayList<JsonType> GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append('[');
        for (JsonType x : this.value) {
            res.append(x.toString());
            res.append(',');
        }
        res.append(']');
        return res.toString();
    }
}
