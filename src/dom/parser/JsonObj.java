package src.dom.parser;

import java.util.HashMap;

public class JsonObj extends JsonType {
    private HashMap<String, JsonType> value;

    public JsonObj(HashMap<String, JsonType> value) {
        super(JsonType.Type.JSON_OBJ);
        this.value = value;
    }

    @Override
    public HashMap<String, JsonType> GetValue() {
        return this.value;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder();
        res.append('{');
        this.value.forEach(
                (k, v) -> res.append(String.format("%s : %s, ", k, v.toString())));
        res.append('}');
        return res.toString();
    }
}
