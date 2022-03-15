package wjl.yang.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class YangStmt {
    private int line; // 在源文件中的行号
    private String key;
    private String value;
    private int valueToken; // 在YangToken中定义
    private List<YangStmt> subStatements;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getValueToken() {
        return valueToken;
    }

    public void setValueToken(int valueToken) {
        this.valueToken = valueToken;
    }

    public void addSubStatement(YangStmt stmt) {
        if (subStatements == null) {
            subStatements = new ArrayList<>();
        }
        subStatements.add(stmt);
    }

    public List<YangStmt> getSubStatements() {
        return subStatements;
    }

    public YangStmt searchOne(String key) {
        if (subStatements == null) {
            return null;
        }
        for (YangStmt sub : subStatements) {
            if (Objects.equals(key, sub.getKey())) {
                return sub;
            }
        }
        return null;
    }

    public void forEach(String key, Consumer<YangStmt> consumer) {
        if (subStatements == null) {
            return;
        }

        for (YangStmt sub : subStatements) {
            if (Objects.equals(key, sub.getKey())) {
               consumer.accept(sub);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("line ").append(line)
                .append(" : ").append(key);
        if (value != null) {
            sb.append(" ").append(value);
        }
        sb.append(' ');
        return sb.toString();
    }
}
