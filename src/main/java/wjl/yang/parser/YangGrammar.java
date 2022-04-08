package wjl.yang.parser;

import wjl.yang.model.YangToken;
import wjl.yang.utils.YangKeyword;

import java.util.*;

class YangGrammar {
    static class Stmt {
        // 语句的关键字
        private String key;
        // 允许的值类型
        private final Set<Integer> validTokens = new HashSet<>();
        // 枚举所有可能的取值，可选参数
        private String[] validValues;
        private int subStatementCount = 0;
        private final Map<String, SubStmt> subStatementByName = new HashMap<>();

        Stmt(String key, int...validTokens) {
            this.key = key;
            for (int token : validTokens) {
                this.validTokens.add(token);
            }
        }

        void addSub(int min, int max, int order, Stmt...stmtList) {
            for (Stmt stmt : stmtList) {
                this.subStatementByName.put(stmt.key, new SubStmt(stmt, min, max, order, subStatementCount++));
            }
        }

        void addSub(Stmt stmt, int min, int max) {
            this.subStatementByName.put(stmt.key, new SubStmt(stmt, min, max, 0, subStatementCount++));
        }

        void addSub(int min, int max, Stmt...stmtList) {
            // 标准要求多种类型的子句加在一起至少出现一次，还没有实现
            for (Stmt stmt : stmtList) {
                this.subStatementByName.put(stmt.key, new SubStmt(stmt, 0, max, 0, subStatementCount++));
            }
        }

        void setValidValues(String... validValues) {
            this.validValues = validValues;
        }

        String getKey() {
            return key;
        }

        int getSubStatementCount() {
            return subStatementCount;
        }

        Map<String, SubStmt> getSubStatements() {
            return subStatementByName;
        }

        String checkToken(int token) {
            if (validTokens.isEmpty()) {
                if (token != 0) {
                    return "does not need value.";
                }
            } else if (!validTokens.contains(token)) {
                StringBuilder sb = new StringBuilder();
                sb.append("require");
                for (Integer vt : validTokens) {
                    sb.append(" ").append(YangToken.getText(vt));
                }
                sb.append(", but is ").append(YangToken.getText(token)).append(".");
                return sb.toString();
            }
            return null;
        }

        String checkValue(String value) {
            if (validValues == null) {
                return null;
            }
            for (String vv : validValues) {
                if (Objects.equals(vv, value)) {
                    return null;
                }
            }
            StringBuilder sb = new StringBuilder();
            sb.append("require");
            for (String vv : validValues) {
                sb.append(" ").append(vv);
            }
            sb.append(".");
            return sb.toString();
        }
    }

    static class SubStmt {
        private final Stmt stmt;
        private final int min;
        private final int max;
        private final int order;
        private final int index;

        SubStmt(Stmt stmt, int min, int max, int order, int index) {
            this.stmt = stmt;
            this.min = min;
            this.max = max;
            this.order = order;
            this.index = index;
        }

        Stmt getStmt() {
            return stmt;
        }

        int getMin() {
            return min;
        }

        int getMax() {
            return max;
        }

        int getIndex() {
            return index;
        }
    }

    static final Stmt MODULE;
    static final Stmt SUB_MODULE;

    static {
        // description
        Stmt description = new Stmt(YangKeyword.DESCRIPTION, YangToken.STRING);

        // reference
        Stmt reference = new Stmt(YangKeyword.REFERENCE, YangToken.STRING);

        /*----------- module header -----------*/

        // yang-version
        Stmt yangVersion = new Stmt(YangKeyword.YANG_VERSION, YangToken.DECIMAL);
        yangVersion.setValidValues("1.1");

        // namespace
        Stmt namespace = new Stmt(YangKeyword.NAMESPACE, YangToken.STRING);

        // prefix
        Stmt prefix = new Stmt(YangKeyword.PREFIX, YangToken.IDENTITY);

        // revision-date
        Stmt revisionDate = new Stmt(YangKeyword.REVISION_DATE, YangToken.DATE);

        // import
        Stmt imports = new Stmt(YangKeyword.IMPORT, YangToken.IDENTITY);
        imports.addSub(prefix, 1, 1);
        imports.addSub(revisionDate, 0, 1);
        imports.addSub(description, 0, 1);
        imports.addSub(reference, 0, 1);

        // include
        Stmt include = new Stmt(YangKeyword.INCLUDE, YangToken.IDENTITY);
        include.addSub(revisionDate, 0, 1);
        include.addSub(description, 0, 1);
        include.addSub(reference, 0, 1);

        // revision
        Stmt revision = new Stmt(YangKeyword.REVISION, YangToken.DATE);
        revision.addSub(description, 0, 1);
        revision.addSub(reference, 0, 1);

        // organization
        Stmt organization = new Stmt("organization", YangToken.STRING);

        // contact
        Stmt contact = new Stmt("contact", YangToken.STRING);

        // belongs-to
        Stmt belongsTo = new Stmt(YangKeyword.BELONGS_TO, YangToken.IDENTITY);
        belongsTo.addSub(prefix, 1, 1);

        // when
        Stmt when = new Stmt(YangKeyword.WHEN, YangToken.STRING);
        when.addSub(description, 0, 1);
        when.addSub(reference, 0, 1);

        // unique
        Stmt unique = new Stmt("unique", YangToken.STRING);

        // key
        Stmt key = new Stmt("key", YangToken.IDENTITY, YangToken.STRING);

        // value
        Stmt value = new Stmt("value", YangToken.STRING);

        // min-elements
        Stmt minElements = new Stmt("min-elements", YangToken.INTEGER, YangToken.STRING);

        // max-elements
        Stmt maxElements = new Stmt("max-elements", YangToken.INTEGER, YangToken.STRING);

        // error-message
        Stmt errorMessage = new Stmt("error-message", YangToken.STRING);

        // error-app-tag
        Stmt errorAppTag = new Stmt("error-app-tag", YangToken.STRING);

        // must
        Stmt must = new Stmt(YangKeyword.MUST, YangToken.STRING);
        must.addSub(errorMessage, 0, 1);
        must.addSub(errorAppTag, 0, 1);
        must.addSub(description, 0, 1);
        must.addSub(reference, 0, 1);

        // presence
        Stmt presence = new Stmt(YangKeyword.PRESENCE, YangToken.STRING);

        // ordered-by
        Stmt orderedBy = new Stmt("ordered-by", YangToken.STRING);
        orderedBy.setValidValues("user", "system");

        // mandatory
        Stmt mandatory = new Stmt(YangKeyword.MANDATORY, YangToken.STRING);
        mandatory.setValidValues("true", "false");

        // config
        Stmt config = new Stmt(YangKeyword.CONFIG, YangToken.IDENTITY, YangToken.IDENTITY);
        config.setValidValues("true", "false");

        // status
        Stmt status = new Stmt(YangKeyword.STATUS, YangToken.STRING, YangToken.IDENTITY);
        status.setValidValues("current", "obsolete", "deprecated");

        // position
        Stmt position = new Stmt("position", YangToken.STRING);

        // if-feature
        Stmt ifFeature = new Stmt(YangKeyword.IF_FEATURE, YangToken.IDENTITY, YangToken.PREFIX_ID,
            YangToken.STRING);

        // feature
        Stmt feature = new Stmt(YangKeyword.FEATURE, YangToken.IDENTITY, YangToken.STRING);
        feature.addSub(ifFeature, 0, -1);
        feature.addSub(status, 0, 1);
        feature.addSub(description, 0, 1);
        feature.addSub(reference, 0, 1);

        // base
        Stmt base = new Stmt(YangKeyword.BASE, YangToken.IDENTITY, YangToken.PREFIX_ID);

        // identity
        Stmt identity = new Stmt(YangKeyword.IDENTITY, YangToken.IDENTITY);
        identity.addSub(ifFeature, 0, -1);
        identity.addSub(base, 0, -1);
        identity.addSub(status, 0, 1);
        identity.addSub(description, 0, 1);
        identity.addSub(reference, 0, 1);

        // extension > argument > yin-element
        Stmt yinElement = new Stmt("yin-element", YangToken.IDENTITY);
        yinElement.setValidValues("true", "false");

        // extension > argument
        Stmt argument = new Stmt("argument", YangToken.IDENTITY);
        argument.addSub(yinElement, 0, 1);

        // extension
        Stmt extension = new Stmt("extension", YangToken.IDENTITY);
        extension.addSub(argument, 0, 1);
        extension.addSub(status, 0, 1);
        extension.addSub(description, 0, 1);
        extension.addSub(reference, 0, 1);

        // units
        Stmt units = new Stmt("units", YangToken.STRING);

        // bit
        Stmt bit = new Stmt("bit", YangToken.IDENTITY);
        bit.addSub(ifFeature, 0, -1);
        bit.addSub(position, 0, 1);
        bit.addSub(position, 0, 1);
        bit.addSub(status, 0, 1);
        bit.addSub(description, 0, 1);
        bit.addSub(reference, 0, 1);

        // require-instance
        Stmt requireInstance = new Stmt("require-instance", YangToken.IDENTITY);
        requireInstance.setValidValues("true", "false");

        // path
        Stmt path = new Stmt("path", YangToken.STRING);

        // enum
        Stmt enums = new Stmt("enum", YangToken.STRING);
        enums.addSub(ifFeature, 0, -1);
        enums.addSub(value, 0, 1);
        enums.addSub(status, 0, 1);
        enums.addSub(description, 0, 1);
        enums.addSub(reference, 0, 1);

        // default
        Stmt defaults = new Stmt("default", YangToken.INTEGER, YangToken.STRING, YangToken.IDENTITY);

        // modifier
        Stmt modifier = new Stmt("modifier", YangToken.STRING);
        modifier.setValidValues("invert-match");

        // pattern
        Stmt pattern = new Stmt("pattern", YangToken.STRING);
        pattern.addSub(modifier, 0, 1);
        pattern.addSub(errorMessage, 0, 1);
        pattern.addSub(errorAppTag, 0, 1);
        pattern.addSub(description, 0, 1);
        pattern.addSub(reference, 0, 1);

        // length
        Stmt length = new Stmt("length", YangToken.STRING);
        length.addSub(errorMessage, 0, 1);
        length.addSub(errorAppTag, 0, 1);
        length.addSub(description, 0, 1);
        length.addSub(reference, 0, 1);

        // fraction-digits
        Stmt fractionDigits = new Stmt("fraction-digits", YangToken.STRING);

        // range
        Stmt range = new Stmt("range", YangToken.STRING);
        range.addSub(errorMessage, 0, 1);
        range.addSub(errorAppTag, 0, 1);
        range.addSub(description, 0, 1);
        range.addSub(reference, 0, 1);

        // type 没有严格按标准校验，下列子句只能按特定的方式组合
        Stmt type = new Stmt(YangKeyword.TYPE, YangToken.IDENTITY, YangToken.PREFIX_ID);
        type.addSub(range, 0, 1);
        type.addSub(fractionDigits, 0, 1);
        type.addSub(length, 0, 1);
        type.addSub(pattern, 0, -1);
        type.addSub(enums, 0, -1);
        type.addSub(path, 0, 1);
        type.addSub(requireInstance, 0, 1);
        type.addSub(base, 0, -1);
        type.addSub(bit, 0, -1);
        type.addSub(type, 0, -1);

        // typedef
        Stmt typedef = new Stmt(YangKeyword.TYPEDEF, YangToken.IDENTITY);
        typedef.addSub(type, 1, 1);
        typedef.addSub(units, 0, 1);
        typedef.addSub(defaults, 0, 1);
        typedef.addSub(status, 0, 1);
        typedef.addSub(description, 0, 1);
        typedef.addSub(reference, 0, 1);

        // anyxml
        Stmt anyxml = new Stmt(YangKeyword.ANYXML, YangToken.IDENTITY);
        anyxml.addSub(when, 0, 1);
        anyxml.addSub(ifFeature, 0, -1);
        anyxml.addSub(must, 0, -1);
        anyxml.addSub(config, 0, 1);
        anyxml.addSub(mandatory, 0, 1);
        anyxml.addSub(status, 0, 1);
        anyxml.addSub(description, 0, 1);
        anyxml.addSub(reference, 0, 1);

        // anydata
        Stmt anydata = new Stmt(YangKeyword.ANYDATA, YangToken.IDENTITY);
        anydata.addSub(when, 0, 1);
        anydata.addSub(ifFeature, 0, -1);
        anydata.addSub(must, 0, -1);
        anydata.addSub(config, 0, 1);
        anydata.addSub(mandatory, 0, 1);
        anydata.addSub(status, 0, 1);
        anydata.addSub(description, 0, 1);
        anydata.addSub(reference, 0, 1);

        // 这几条语句比较特别，有两个关键字，没有严格按标准做校验，放宽了限制
        // deviate-add deviate-delete deviate-replace deviate-not-supported
        Stmt deviateAll = new Stmt("deviate", YangToken.IDENTITY);
        deviateAll.setValidValues("add", "delete", "replace", "not-supported");
        deviateAll.addSub(type, 0, 1);
        deviateAll.addSub(units, 0, 1);
        deviateAll.addSub(must, 0, -1);
        deviateAll.addSub(defaults, 0, -1);
        deviateAll.addSub(unique, 0, -1);
        deviateAll.addSub(config, 0, 1);
        deviateAll.addSub(mandatory, 0, 1);
        deviateAll.addSub(minElements, 0, 1);
        deviateAll.addSub(maxElements, 0, 1);

        // deviation 和标准略有差异，标准规定not-supported不能和add/delete/replace同时出现
        Stmt deviation = new Stmt("deviation", YangToken.STRING);
        deviation.addSub(description, 0, 1);
        deviation.addSub(reference, 0, 1);
        deviation.addSub(deviateAll, 0, -1);

        // refine
        Stmt refine = new Stmt(YangKeyword.REFINE, YangToken.STRING);
        refine.addSub(ifFeature, 0, -1);
        refine.addSub(must, 0, -1);
        refine.addSub(presence, 0, 1);
        refine.addSub(defaults, 0, -1);
        refine.addSub(config, 0, 1);
        refine.addSub(mandatory, 0, 1);
        refine.addSub(minElements, 0, 1);
        refine.addSub(maxElements, 0, 1);
        refine.addSub(description, 0, 1);
        refine.addSub(reference, 0, 1);

        Stmt container = new Stmt(YangKeyword.CONTAINER, YangToken.IDENTITY);
        Stmt leaf = new Stmt(YangKeyword.LEAF, YangToken.IDENTITY);
        Stmt list = new Stmt(YangKeyword.LIST, YangToken.IDENTITY);
        Stmt leafList = new Stmt(YangKeyword.LEAF_LIST, YangToken.IDENTITY);
        Stmt grouping = new Stmt(YangKeyword.GROUPING, YangToken.IDENTITY);
        Stmt choice = new Stmt(YangKeyword.CHOICE, YangToken.IDENTITY);
        Stmt cases = new Stmt(YangKeyword.CASE, YangToken.IDENTITY);
        Stmt augment = new Stmt(YangKeyword.AUGMENT, YangToken.STRING);
        // useAugment和augment的格式是一样的，只是使用位置不同
        Stmt usesAugment = augment;
        Stmt uses = new Stmt(YangKeyword.USES, YangToken.IDENTITY, YangToken.PREFIX_ID);
        Stmt notification = new Stmt("notification", YangToken.IDENTITY);
        Stmt action = new Stmt("action", YangToken.IDENTITY);

        // leaf
        leaf.addSub(when, 0, 1);
        leaf.addSub(ifFeature, 0, -1);
        leaf.addSub(type, 1, 1);
        leaf.addSub(units, 0, 1);
        leaf.addSub(must, 0, -1);
        leaf.addSub(defaults, 0, 1);
        leaf.addSub(config, 0, 1);
        leaf.addSub(mandatory, 0, 1);
        leaf.addSub(status, 0, 1);
        leaf.addSub(description, 0, 1);
        leaf.addSub(reference, 0, 1);

        // leaf-list
        leafList.addSub(when, 0, 1);
        leafList.addSub(ifFeature, 0, -1);
        leafList.addSub(type, 1, 1);
        leafList.addSub(units, 0, 1);
        leafList.addSub(must, 0, -1);
        leafList.addSub(defaults, 0, -1);
        leafList.addSub(config, 0, 1);
        leafList.addSub(minElements, 0, 1);
        leafList.addSub(maxElements, 0, 1);
        leafList.addSub(orderedBy, 0, 1);
        leafList.addSub(status, 0, 1);
        leafList.addSub(description, 0, 1);
        leafList.addSub(reference, 0, 1);

        // container
        container.addSub(when, 0, 1);
        container.addSub(ifFeature, 0, -1);
        container.addSub(must, 0, -1);
        container.addSub(presence, 0, 1);
        container.addSub(config, 0, 1);
        container.addSub(status, 0, 1);
        container.addSub(description, 0, 1);
        container.addSub(reference, 0, 1);
        container.addSub(0, -1, typedef, grouping);
        container.addSub(0, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);
        container.addSub(0, -1, action);
        container.addSub(0, -1, notification);

        // list
        list.addSub(when, 0, 1);
        list.addSub(ifFeature, 0, -1);
        list.addSub(must, 0, -1);
        list.addSub(key, 0, 1);
        list.addSub(unique, 0, -1);
        list.addSub(config, 0, 1);
        list.addSub(minElements, 0, 1);
        list.addSub(maxElements, 0, 1);
        list.addSub(orderedBy, 0, 1);
        list.addSub(status, 0, 1);
        list.addSub(description, 0, 1);
        list.addSub(reference, 0, 1);
        list.addSub(0, -1, typedef, grouping);
        list.addSub(1, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);
        list.addSub(0, -1, action);
        list.addSub(0, -1, notification);

        // augment
        augment.addSub(when, 0, 1);
        augment.addSub(ifFeature, 0, -1);
        augment.addSub(status, 0, 1);
        augment.addSub(description, 0, 1);
        augment.addSub(reference, 0, 1);
        augment.addSub(1, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses,
                cases, action, notification);

        // uses
        uses.addSub(when, 0, 1);
        uses.addSub(ifFeature, 0, -1);
        uses.addSub(status, 0, 1);
        uses.addSub(description, 0, 1);
        uses.addSub(reference, 0, 1);
        uses.addSub(refine, 0, -1);
        uses.addSub(usesAugment, 0, -1);

        // notification
        notification.addSub(ifFeature, 0, -1);
        notification.addSub(must, 0, -1);
        notification.addSub(status, 0, 1);
        notification.addSub(description, 0, 1);
        notification.addSub(reference, 0, 1);
        notification.addSub(typedef, 0, -1);
        notification.addSub(grouping, 0, -1);
        notification.addSub(0, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);

        // input 这个语句没有值，标准要求至少有一条data-def子句
        Stmt input = new Stmt(YangKeyword.INPUT);
        input.addSub(must, 0, -1);
        input.addSub(typedef, 0, -1);
        input.addSub(grouping, 0, -1);
        input.addSub(1, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);

        // output 这个语句没有值，标准要求至少有一条data-def子句
        Stmt output = new Stmt(YangKeyword.OUTPUT);
        output.addSub(must, 0, -1);
        output.addSub(typedef, 0, -1);
        output.addSub(grouping, 0, -1);
        output.addSub(1, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);

        // action
        action.addSub(ifFeature, 0, -1);
        action.addSub(status, 0, 1);
        action.addSub(description, 0, 1);
        action.addSub(reference, 0, 1);
        action.addSub(typedef, 0, -1);
        action.addSub(grouping, 0, -1);
        action.addSub(input, 0, 1);
        action.addSub(output, 0, 1);

        // rpc
        Stmt rpc = new Stmt(YangKeyword.RPC, YangToken.IDENTITY);
        rpc.addSub(ifFeature, 0, -1);
        rpc.addSub(status, 0, 1);
        rpc.addSub(description, 0, 1);
        rpc.addSub(reference, 0, 1);
        rpc.addSub(typedef, 0, -1);
        rpc.addSub(grouping, 0, -1);
        rpc.addSub(input, 0, 1);
        rpc.addSub(output, 0, 1);

        // choice
        choice.addSub(when, 0, 1);
        choice.addSub(ifFeature, 0, -1);
        choice.addSub(defaults, 0, 1);
        choice.addSub(config, 0, 1);
        choice.addSub(mandatory, 0, 1);
        choice.addSub(status, 0, 1);
        choice.addSub(description, 0, 1);
        choice.addSub(reference, 0, 1);
        choice.addSub(0, -1, container, leaf, leafList, list, choice, anydata, anyxml, cases);

        // case
        cases.addSub(when, 0, 1);
        cases.addSub(ifFeature, 0, -1);
        cases.addSub(status, 0, 1);
        cases.addSub(description, 0, 1);
        cases.addSub(reference, 0, 1);
        cases.addSub(0, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);

        // grouping
        grouping.addSub(status, 0, 1);
        grouping.addSub(description, 0, 1);
        grouping.addSub(reference, 0, 1);
        grouping.addSub(0, -1, typedef, grouping);
        grouping.addSub(0, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);
        grouping.addSub(0, -1, action);
        grouping.addSub(0, -1, notification);

        // module
        MODULE = new Stmt(YangKeyword.MODULE,  YangToken.IDENTITY);
        // module-header
        MODULE.addSub(0, 1, 1, yangVersion, namespace, prefix);
        // linkage
        MODULE.addSub(0, -1, 2, imports, include);
        // meta
        MODULE.addSub(0, 1, 3, organization, contact, description, reference);
        // revision
        MODULE.addSub(0, -1, 4, revision);
        // body data-def
        MODULE.addSub(0, -1, 5, extension, feature, identity, typedef,
                grouping, augment, rpc, notification, deviation,
                container, leaf, leafList, list, choice, anydata, anyxml, uses);

        SUB_MODULE = new Stmt(YangKeyword.SUBMODULE, YangToken.IDENTITY);
        // submodule-header
        SUB_MODULE.addSub(0, 1, 1, yangVersion, belongsTo);
        // linkage
        SUB_MODULE.addSub(0, -1, 2, imports, include);
        // meta
        SUB_MODULE.addSub(0, 1, 3, organization, contact, description, reference);
        // revision
        SUB_MODULE.addSub(0, -1, 4, revision);
        // body data-def
        SUB_MODULE.addSub(0, -1, 5, extension, feature, identity, typedef,
                grouping, augment, rpc, notification, deviation,
                container, leaf, leafList, list, choice, anydata, anyxml, uses);
    }
}
