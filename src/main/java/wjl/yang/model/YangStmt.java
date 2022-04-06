package wjl.yang.model;

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
    private YangModule oriModule; // 该语句是在哪个模块定义的
    private YangMainModule schemaModule; // 哪个主模块将此语句加入到模型树

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    /**
     * 如果是扩展关键字，返回扩展关键字的前缀
     *
     * @return 扩展关键字的前缀
     */
    public String getExtensionPrefix() {
        return null;
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

    public void addSubStatements(List<YangStmt> stmtList) {
        if (subStatements == null) {
            subStatements = new ArrayList<>();
        }
        subStatements.addAll(stmtList);
    }

    /**
     * 替换指定的字句
     *
     * @param del 被替换的语句
     * @param stmtList 需要插入的语句列表
     */
    public void replaceStmt(YangStmt del, List<YangStmt> stmtList) {
        if (subStatements == null) {
            subStatements = stmtList;
            return;
        }
        List<YangStmt> clone = new ArrayList<>();
        for (YangStmt sub : subStatements) {
            if (sub == del) {
                clone.addAll(stmtList);
            } else {
                clone.add(sub);
            }
        }
        subStatements = clone;
    }

    public void replaceStmt(YangStmt add) {
        if (subStatements == null) {
            subStatements = new ArrayList<>();
            subStatements.add(add);
            return;
        }

        for (int idx = 0; idx < subStatements.size(); idx++) {
            YangStmt sub = subStatements.get(idx);
            if (Objects.equals(sub.getKey(), add.getKey())) {
                subStatements.set(idx, add);
                return;
            }
        }
        subStatements.add(add);
    }

    public List<YangStmt> getSubStatements() {
        return subStatements;
    }

    public void clearSubStatements() {
        subStatements = null;
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

    /**
     * 遍历所有语句，执行输入的函数
     *
     * @param consumer 处理一条语句的函数
     */
    public void iterateAll(Consumer<YangStmt> consumer) {
        consumer.accept(this);
        if (subStatements != null) {
            for (YangStmt sub : subStatements) {
                sub.iterateAll(consumer);
            }
        }
    }

    public void setOriModule(YangModule oriModule) {
        this.oriModule = oriModule;
    }

    public YangModule getOriModule() {
        return oriModule;
    }

    public void setSchemaModule(YangMainModule schemaModule) {
        this.schemaModule = schemaModule;
    }

    public YangMainModule getSchemaModule() {
        if (schemaModule != null) {
            return schemaModule;
        } else if (oriModule != null) {
            return oriModule.getMainModule();
        } else {
            return null;
        }
    }

    public void reportError(String msg) {
        oriModule.addError(this, msg);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (oriModule != null) {
            sb.append(oriModule.getName()).append(' ');
        }
        sb.append("line ").append(line)
                .append(" : ").append(key);
        if (value != null) {
            if (valueToken == YangToken.STRING) {
                sb.append(" \"").append(value).append("\"");
            } else {
                sb.append(" ").append(value);
            }
        }
        sb.append(' ');
        return sb.toString();
    }
}
