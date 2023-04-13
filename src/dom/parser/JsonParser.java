package src.dom.parser;

import java.util.ArrayList;
import java.util.HashMap;

class Pair<T1, T2> {
    public final T1 first;
    public final T2 second;

    public Pair(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}

abstract class Token {
    public enum Type {
        NUMBER,
        STRING,
        BOOL,
        NULL,
        LEFT_PAREN,
        RIGHT_PAREN,
        LEFT_CURLY,
        RIGHT_CURLY,
        COMMA,
        COLON,
    }

    public Type type;

    public Token(Type type) {
        this.type = type;
    }

    abstract public String toString();

    abstract public Object GetValue();
}

class TokenBool extends Token {
    public boolean value;

    public TokenBool(boolean value) {
        super(Type.BOOL);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("BOOL(%s)", this.value);
    }

    @Override
    public Boolean GetValue() {
        return this.value;
    }
}

class TokenNumber extends Token {
    public double value;

    public TokenNumber(long value, boolean isNegative) {
        super(Type.NUMBER);
        this.value = (double) value;
        if (isNegative)
            this.value = -this.value;
    }

    public TokenNumber(long value, long mantissa, int mantissaLeadingZeroes, boolean isNegative) {
        super(Type.NUMBER);
        if (mantissa == 0)
            this.value = (double) value;
        else {
            double mant = mantissa * Math.pow(10, -(Math.floor(Math.log10(mantissa)) + 1 + mantissaLeadingZeroes));
            this.value = value + mant;
            if (isNegative)
                this.value = -this.value;
        }
    }

    @Override
    public String toString() {
        return String.format("NUMBER(%.2f)", this.value);
    }

    @Override
    public Number GetValue() {
        return this.value;
    }
}

class TokenString extends Token {
    public String value;

    public TokenString(String value) {
        super(Type.STRING);
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("STRING(%s)", this.value);
    }

    @Override
    public String GetValue() {
        return new String(this.value);
    }
}

class TokenNull extends Token {
    public String value;

    public TokenNull() {
        super(Type.NULL);
    }

    @Override
    public String toString() {
        return "NULL";
    }

    @Override
    public Object GetValue() {
        return null;
    }
}

class TokenSymbol extends Token {
    public TokenSymbol(Type type) {
        super(type);
    }

    @Override
    public String toString() {
        return String.format("SYMBOL(%s)", this.type);
    }

    @Override
    public String GetValue() {
        return new String("" + this.type);
    }
}

class Tokenizer {
    private String source;
    private Integer idx;

    public Tokenizer(String source) {
        this.source = source.trim();
        this.idx = 0;
    }

    public boolean IsEnd() {
        return this.idx == this.source.length();
    }

    private char GetCurrentChar() {
        return this.source.charAt(this.idx);
    }

    private void Next() {
        if (!this.IsEnd()) {
            this.idx += 1;
        }
    }

    private void SkipWhitespace() {
        while (!this.IsEnd()) {
            switch (this.GetCurrentChar()) {
                case ' ':
                case '\r':
                case '\n':
                case '\t':
                    break;
                default:
                    return;
            }
            this.Next();
        }
    }

    public ArrayList<Token> GetTokens() throws ParseException {
        ArrayList<Token> ret = new ArrayList<>();
        while (!this.IsEnd()) {
            ret.add(this.Tokenize());
        }
        return ret;
    }

    public Token Tokenize() throws ParseException {
        this.SkipWhitespace();
        final char cur = this.GetCurrentChar();

        if (Character.isLetter(this.source.charAt(this.idx))) {
            int oldIdx = this.idx;
            int cnt = 0;
            do {
                cnt++;
                this.Next();
                if (cnt > 5)
                    throw new ParseException("Unknown symbol");
            } while (!this.IsEnd() && Character.isLetter(this.GetCurrentChar()));

            String idenStr = this.source.substring(oldIdx, this.idx);
            if (idenStr.equals("true")) {
                return new TokenBool(true);
            } else if (idenStr.equals("false")) {
                return new TokenBool(false);
            } else if (idenStr.equals("null")) {
                return new TokenNull();
            } else {
                throw new ParseException("Unknown symbol");
            }
        } else if (Character.isDigit(cur) || cur == '-') {
            return this.TokenizeNumber();
        } else if (this.GetCurrentChar() == '"') {
            return this.TokenizeString();
        } else {
            Token.Type sym;
            switch (this.GetCurrentChar()) {
                case '[':
                    sym = Token.Type.LEFT_PAREN;
                    break;
                case ']':
                    sym = Token.Type.RIGHT_PAREN;
                    break;
                case '{':
                    sym = Token.Type.LEFT_CURLY;
                    break;
                case '}':
                    sym = Token.Type.RIGHT_CURLY;
                    break;
                case ',':
                    sym = Token.Type.COMMA;
                    break;
                case ':':
                    sym = Token.Type.COLON;
                    break;

                default:
                    throw new ParseException("Unknown symbol present." + this.GetCurrentChar());
            }
            this.Next();
            return new TokenSymbol(sym);
        }
    }

    private Pair<Long, Integer> TokenizeNumberBase(int maxdigits) throws ParseException {
        long ret = 0;
        int leadingZeros = 0;
        int digits = 0;

        do {
            char c = this.GetCurrentChar();
            if (c == '.')
                break;

            int digit = -1;
            if (c >= '0' && c <= '9')
                digit = (int) c - (int) '0';
            else
                break;
            ret = (ret * 10) + digit;
            if (ret == 0)
                leadingZeros += 1;
            if (++digits - leadingZeros > maxdigits) {
                throw new ParseException("Literal too big");
            }
            this.Next();
        } while (!this.IsEnd());
        return new Pair<Long, Integer>(ret, leadingZeros);
    }

    private Token TokenizeNumber() throws ParseException {
        char cur = this.GetCurrentChar();
        boolean isNegative = false;

        if (cur == '-') {
            isNegative = true;
            this.Next();
            if (!Character.isDigit(this.GetCurrentChar()))
                throw new ParseException("Expected a digit after minus sign");
        }

        Pair<Long, Integer> x = TokenizeNumberBase(15);
        if (x.second > 0) {
            throw new ParseException("Unexpected leading zeros.");
        }
        if (this.GetCurrentChar() == '.') {
            this.Next();
            Pair<Long, Integer> mantissa = TokenizeNumberBase(15);
            if (this.GetCurrentChar() == '.') {
                throw new ParseException("Unexpected period at the end of number");
            }
            return new TokenNumber(x.first, mantissa.first, mantissa.second, isNegative);
        }
        return new TokenNumber(x.first, isNegative);
    }

    private boolean SkipEscapeChars() {
        this.Next();
        switch (this.GetCurrentChar()) {
            case '"':
            case '\\':
            case '/':
            case 'b':
            case 'f':
            case 'n':
            case 'r':
            case 't':
                return true;
            case 'u': {
                this.Next();
                int cnt = 0;
                while (!this.IsEnd()) {
                    char ch = this.GetCurrentChar();
                    if (!((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'f')))
                        break;
                    cnt++;
                    if (cnt == 4)
                        break;
                    this.Next();
                }
                return (cnt == 4);
            }
            default:
                return false;
        }
    }

    private Token TokenizeString() throws ParseException {
        boolean isComplete = false;
        this.Next();
        int oldIdx = this.idx;
        while (!this.IsEnd()) {
            char ch = this.GetCurrentChar();
            if (ch == '"') {
                this.Next(); // Eat ending quotes
                isComplete = true;
                break;
            } else if (ch == '\\') {
                if (!this.SkipEscapeChars()) {
                    throw new ParseException("Expected a valid escape sequence");
                }
            }
            this.Next();
        }
        if (!isComplete) {
            new ParseException("Encountered a incomplete string");
        }
        return new TokenString(this.source.substring(oldIdx, this.idx - 1));
    }

}

public class JsonParser {
    ArrayList<Token> tokens;
    Integer idx;

    public JsonParser(String source) throws ParseException {
        Tokenizer tokenizer = new Tokenizer(source);
        this.tokens = tokenizer.GetTokens();
        this.idx = 0;
    }

    private boolean IsEnd() {
        return this.idx == this.tokens.size();
    }

    private Token GetCurrentToken() {
        return this.tokens.get(this.idx);
    }

    private Token.Type GetCurrentTokenType() {
        return this.GetCurrentToken().type;
    }

    private void Next() {
        this.idx += 1;
    }

    private void ExpectType(Token.Type expectedType) throws ParseException {
        Token.Type curType = this.GetCurrentTokenType();
        if (curType != expectedType)
            throw new ParseException(String.format("Expected %s, got %s", expectedType, curType));
        this.Next();
    }

    public JsonType Parse() throws ParseException {
        final Token tok = this.GetCurrentToken();
        switch (tok.type) {
            case NUMBER: {
                this.Next();
                return new JsonNumber((double) (tok.GetValue()));
            }
            case STRING: {
                this.Next();
                return new JsonString((String) tok.GetValue());
            }
            case BOOL: {
                this.Next();
                return new JsonBool((Boolean) tok.GetValue());
            }
            case NULL: {
                this.Next();
                return new JsonNull();
            }
            case LEFT_PAREN:
                return this.ParseArray();
            case LEFT_CURLY:
                return this.ParseObj();
            default:
                throw new ParseException("Unexpected " + tok.type);
        }
    }

    public JsonArray ParseArray() throws ParseException {
        ArrayList<JsonType> res = new ArrayList<>();
        this.ExpectType(Token.Type.LEFT_PAREN);

        while (!this.IsEnd()) {
            res.add(this.Parse());
            if (this.GetCurrentTokenType() == Token.Type.RIGHT_PAREN)
                break;
            this.ExpectType(Token.Type.COMMA);
        }
        this.ExpectType(Token.Type.RIGHT_PAREN);
        return new JsonArray(res);
    }

    public JsonObj ParseObj() throws ParseException {
        JsonType key, value;
        HashMap<String, JsonType> res = new HashMap<>();
        this.ExpectType(Token.Type.LEFT_CURLY);

        while (!this.IsEnd()) {
            key = this.Parse();
            if (key.type != JsonType.Type.JSON_STRING) {
                throw new ParseException(String.format("%s are not allowed as key. Use string instead.", key.type));
            }
            key = (JsonString) key;
            this.ExpectType(Token.Type.COLON);
            value = this.Parse();
            res.put((String) key.GetValue(), value);
            if (this.GetCurrentTokenType() == Token.Type.RIGHT_CURLY)
                break;
            this.ExpectType(Token.Type.COMMA);
        }

        this.ExpectType(Token.Type.RIGHT_CURLY);
        return new JsonObj(res);
    }
}
