package json;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

public class JsonSerializer {
    private static boolean isJsonSerializable(Class<?> cls) {
        return JsonSerializable.class.isAssignableFrom(cls);
    }

    private static Object GetFieldValue(Field field, Object obj) throws IllegalAccessException {
        boolean isPrivate = field.trySetAccessible();
        if (isPrivate) {
            field.setAccessible(true);
        }
        Object ret = field.get(obj);
        if (isPrivate) {
            field.setAccessible(false);
        }
        return ret;
    }

    public static String ToJson(Object obj) throws Exception {
        Class<?> cls = obj.getClass();
        assert isJsonSerializable(cls); // NOTE: Enable assertion using '-ea' flag

        StringBuilder ret = new StringBuilder();
        ret.append("{");
        Field fields[] = cls.getDeclaredFields();
        int idx = 0;

        for (Field field : fields) {
            Object fieldValue = GetFieldValue(field, obj);
            ret.append("\"")
                    .append(field.getName())
                    .append("\"")
                    .append(":");
            ToJson(ret, fieldValue);
            idx++;
            if (idx != fields.length)
                ret.append(',');
        }
        ret.append("}");
        return ret.toString();
    }

    private static void ToJson(StringBuilder sb, Object obj) throws Exception {
        if (obj instanceof CharSequence) {
            sb.append("\"")
                    .append(obj.toString())
                    .append("\"");
        } else if (obj instanceof Boolean) {
            sb.append(obj.toString());
        } else if (obj instanceof Integer) {
            sb.append(obj.toString());
        } else if (obj instanceof Iterable) {
            ToJsonFromIterable(sb, (Iterable<Object>) obj);
        } else if (obj instanceof Map) {
            ToJsonFromMap(sb, (Map<String, Object>) obj);
        } else {
            System.out.println(obj.getClass().getName());
        }
    }

    private static void ToJsonFromIterable(StringBuilder sb, Iterable<Object> iterable) throws Exception {
        Iterator<Object> iter = iterable.iterator();
        sb.append("[");
        while (iter.hasNext()) {
            ToJson(sb, iter.next());
            if (iter.hasNext())
                sb.append(",");
        }
        sb.append("]");
    }

    public static <K, V, T extends Map<K, V>> void ToJsonFromMap(StringBuilder sb, T map) throws Exception {
        sb.append("{");
        Iterator<Map.Entry<K, V>> iter = map.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<K, V> entry = iter.next();
            K key = entry.getKey();
            V value = entry.getValue();
            sb.append("\"")
                    .append(key.toString())
                    .append("\"");
            sb.append(":");
            ToJson(sb, (Object) value);

            if (iter.hasNext())
                sb.append(",");
        }

        sb.append("}");
    }
}
