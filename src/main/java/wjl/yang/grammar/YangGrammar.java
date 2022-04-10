package wjl.yang.grammar;

import wjl.yang.model.YangToken;
import wjl.yang.utils.YangKeyword;

class YangGrammar {
    static final StmtGrammar MODULE;
    static final StmtGrammar SUB_MODULE;

    static {
        // description
        StmtGrammar description = GrammarConst.DESCRIPTION;

        // reference
        StmtGrammar reference = GrammarConst.REFERENCE;

        /*----------- module header -----------*/

        // yang-version
        StmtGrammar yangVersion = new StmtGrammar(YangKeyword.YANG_VERSION, YangToken.DECIMAL);
        yangVersion.setValidValues("1.1");

        // namespace
        StmtGrammar namespace = new StmtGrammar(YangKeyword.NAMESPACE, YangToken.STRING);

        // prefix
        StmtGrammar prefix = new StmtGrammar(YangKeyword.PREFIX, YangToken.IDENTITY);

        // revision-date
        StmtGrammar revisionDate = new StmtGrammar(YangKeyword.REVISION_DATE, YangToken.DATE);

        // import
        StmtGrammar imports = new StmtGrammar(YangKeyword.IMPORT, YangToken.IDENTITY);
        imports.addSub(prefix, 1, 1);
        imports.addSub(revisionDate, 0, 1);
        imports.addSub(description, 0, 1);
        imports.addSub(reference, 0, 1);

        // include
        StmtGrammar include = new StmtGrammar(YangKeyword.INCLUDE, YangToken.IDENTITY);
        include.addSub(revisionDate, 0, 1);
        include.addSub(description, 0, 1);
        include.addSub(reference, 0, 1);

        // revision
        StmtGrammar revision = new StmtGrammar(YangKeyword.REVISION, YangToken.DATE);
        revision.addSub(description, 0, 1);
        revision.addSub(reference, 0, 1);

        // organization
        StmtGrammar organization = new StmtGrammar("organization", YangToken.STRING);

        // contact
        StmtGrammar contact = new StmtGrammar("contact", YangToken.STRING);

        // belongs-to
        StmtGrammar belongsTo = new StmtGrammar(YangKeyword.BELONGS_TO, YangToken.IDENTITY);
        belongsTo.addSub(prefix, 1, 1);

        // when
        StmtGrammar when = new StmtGrammar(YangKeyword.WHEN, YangToken.STRING);
        when.addSub(description, 0, 1);
        when.addSub(reference, 0, 1);

        // unique
        StmtGrammar unique = GrammarConst.UNIQUE;

        // key
        StmtGrammar key = new StmtGrammar("key", YangToken.IDENTITY, YangToken.STRING);

        // min-elements
        StmtGrammar minElements = GrammarConst.MIN_ELEMENTS;

        // max-elements
        StmtGrammar maxElements = GrammarConst.MAX_ELEMENTS;

        // must
        StmtGrammar must = GrammarConst.MUST;

        // presence
        StmtGrammar presence = new StmtGrammar(YangKeyword.PRESENCE, YangToken.STRING);

        // ordered-by
        StmtGrammar orderedBy = new StmtGrammar("ordered-by", YangToken.STRING);
        orderedBy.setValidValues("user", "system");

        // mandatory
        StmtGrammar mandatory = GrammarConst.MANDATORY;

        // config
        StmtGrammar config = GrammarConst.CONFIG;

        // status
        StmtGrammar status = GrammarConst.STATUS;
        // if-feature
        StmtGrammar ifFeature = GrammarConst.IF_FEATURE;

        // feature
        StmtGrammar feature = new StmtGrammar(YangKeyword.FEATURE, YangToken.IDENTITY, YangToken.STRING);
        feature.addSub(ifFeature, 0, -1);
        feature.addSub(status, 0, 1);
        feature.addSub(description, 0, 1);
        feature.addSub(reference, 0, 1);

        // base
        StmtGrammar base = GrammarConst.BASE;

        // identity
        StmtGrammar identity = new StmtGrammar(YangKeyword.IDENTITY, YangToken.IDENTITY);
        identity.addSub(ifFeature, 0, -1);
        identity.addSub(base, 0, -1);
        identity.addSub(status, 0, 1);
        identity.addSub(description, 0, 1);
        identity.addSub(reference, 0, 1);

        // extension > argument > yin-element
        StmtGrammar yinElement = new StmtGrammar("yin-element", YangToken.IDENTITY);
        yinElement.setBooleanValues();

        // extension > argument
        StmtGrammar argument = new StmtGrammar("argument", YangToken.IDENTITY);
        argument.addSub(yinElement, 0, 1);

        // extension
        StmtGrammar extension = new StmtGrammar("extension", YangToken.IDENTITY);
        extension.addSub(argument, 0, 1);
        extension.addSub(status, 0, 1);
        extension.addSub(description, 0, 1);
        extension.addSub(reference, 0, 1);

        // units
        StmtGrammar units = GrammarConst.UNITS;

        // default
        StmtGrammar defaults = GrammarConst.DEFAULT;

        // type 没有严格按标准校验，下列子句只能按特定的方式组合
        StmtGrammar type = GrammarConst.TYPE;

        // typedef
        StmtGrammar typedef = new StmtGrammar(YangKeyword.TYPEDEF, YangToken.IDENTITY);
        typedef.addSub(type, 1, 1);
        typedef.addSub(units, 0, 1);
        typedef.addSub(defaults, 0, 1);
        typedef.addSub(status, 0, 1);
        typedef.addSub(description, 0, 1);
        typedef.addSub(reference, 0, 1);

        // anyxml
        StmtGrammar anyxml = new StmtGrammar(YangKeyword.ANYXML, YangToken.IDENTITY);
        anyxml.addSub(when, 0, 1);
        anyxml.addSub(ifFeature, 0, -1);
        anyxml.addSub(must, 0, -1);
        anyxml.addSub(config, 0, 1);
        anyxml.addSub(mandatory, 0, 1);
        anyxml.addSub(status, 0, 1);
        anyxml.addSub(description, 0, 1);
        anyxml.addSub(reference, 0, 1);

        // anydata
        StmtGrammar anydata = new StmtGrammar(YangKeyword.ANYDATA, YangToken.IDENTITY);
        anydata.addSub(when, 0, 1);
        anydata.addSub(ifFeature, 0, -1);
        anydata.addSub(must, 0, -1);
        anydata.addSub(config, 0, 1);
        anydata.addSub(mandatory, 0, 1);
        anydata.addSub(status, 0, 1);
        anydata.addSub(description, 0, 1);
        anydata.addSub(reference, 0, 1);

        StmtGrammar deviate = new DeviateGrammar("deviate", YangToken.IDENTITY);

        // deviation
        StmtGrammar deviation = new StmtGrammar("deviation", YangToken.STRING);
        deviation.addSub(description, 0, 1);
        deviation.addSub(reference, 0, 1);
        deviation.addSub(deviate, 0, -1);

        // refine
        StmtGrammar refine = new StmtGrammar(YangKeyword.REFINE, YangToken.STRING);
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

        StmtGrammar container = new StmtGrammar(YangKeyword.CONTAINER, YangToken.IDENTITY);
        StmtGrammar leaf = new StmtGrammar(YangKeyword.LEAF, YangToken.IDENTITY);
        StmtGrammar list = new StmtGrammar(YangKeyword.LIST, YangToken.IDENTITY);
        StmtGrammar leafList = new StmtGrammar(YangKeyword.LEAF_LIST, YangToken.IDENTITY);
        StmtGrammar grouping = new StmtGrammar(YangKeyword.GROUPING, YangToken.IDENTITY);
        StmtGrammar choice = new StmtGrammar(YangKeyword.CHOICE, YangToken.IDENTITY);
        StmtGrammar cases = new StmtGrammar(YangKeyword.CASE, YangToken.IDENTITY);
        StmtGrammar augment = new StmtGrammar(YangKeyword.AUGMENT, YangToken.STRING);
        // useAugment和augment的格式是一样的，只是使用位置不同
        StmtGrammar usesAugment = augment;
        StmtGrammar uses = new StmtGrammar(YangKeyword.USES, YangToken.IDENTITY, YangToken.PREFIX_ID);
        StmtGrammar notification = new StmtGrammar("notification", YangToken.IDENTITY);
        StmtGrammar action = new StmtGrammar("action", YangToken.IDENTITY);

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
        StmtGrammar input = new StmtGrammar(YangKeyword.INPUT);
        input.addSub(must, 0, -1);
        input.addSub(typedef, 0, -1);
        input.addSub(grouping, 0, -1);
        input.addSub(1, -1, container, leaf, leafList, list, choice, anydata, anyxml, uses);

        // output 这个语句没有值，标准要求至少有一条data-def子句
        StmtGrammar output = new StmtGrammar(YangKeyword.OUTPUT);
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
        StmtGrammar rpc = new StmtGrammar(YangKeyword.RPC, YangToken.IDENTITY);
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
        MODULE = new StmtGrammar(YangKeyword.MODULE,  YangToken.IDENTITY);
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

        SUB_MODULE = new StmtGrammar(YangKeyword.SUBMODULE, YangToken.IDENTITY);
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
