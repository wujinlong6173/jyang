package wjl.yang.model;

import wjl.yang.parser.YangStmt;
import wjl.yang.utils.YangError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YangModule {
    private final String name;
    private final String prefix;
    private final String version;

    private final Map<String, ModuleNameVersion> prefixMap = new HashMap<>();
    private final List<YangError> errors = new ArrayList<>();

    /**
     * 模块
     *
     * @param name 模块的名称
     * @param prefix 前缀
     * @param version 可选的版本
     */
    public YangModule(String name, String prefix, String version) {
        this.name = name;
        this.prefix = prefix;
        this.version = version;
    }

    public void addError(YangStmt pos, String msg) {
        errors.add(new YangError(pos, msg));
    }

    public void addPrefix(String pre, ModuleNameVersion target) {
        prefixMap.put(pre, target);
    }

    public ModuleNameVersion getPrefix(String pre) {
        return prefixMap.get(pre);
    }
}
