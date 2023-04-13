package src.dom.parser;

public abstract class JsonType {
    public enum Type {
        JSON_STRING,
        JSON_NUMBER,
        JSON_NULL,
        JSON_BOOL,
        JSON_OBJ,
        JSON_ARRAY,
    }

    public Type type;

    public JsonType(Type type) {
        this.type = type;
    }

    abstract public Object GetValue();

    abstract public String toString();
}