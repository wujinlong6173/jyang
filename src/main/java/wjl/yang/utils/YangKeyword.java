package wjl.yang.utils;

/**
 * 在Yang文件中用到的关键字，除去type语句。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public interface YangKeyword {
    String ANYDATA = "anydata";
    String ANYXML = "anyxml";
    String AUGMENT = "augment";
    String BASE = "base";
    String BELONGS_TO = "belongs-to";
    String CASE = "case";
    String CHOICE = "choice";
    String CONFIG = "config";
    String CONTAINER = "container";
    String DEFAULT = "default";
    String DESCRIPTION = "description";
    String FEATURE = "feature";
    String IF_FEATURE = "if-feature";
    String GROUPING = "grouping";
    String IDENTITY = "identity";
    String IMPORT = "import";
    String INCLUDE = "include";
    String INPUT = "input";
    String KEY = "key";
    String LEAF = "leaf";
    String LEAF_LIST = "leaf-list";
    String LIST = "list";
    String MANDATORY = "mandatory";
    String MAX_ELEMENTS = "max-elements";
    String MIN_ELEMENTS = "min-elements";
    String MODULE = "module";
    String MUST = "must";
    String NAMESPACE = "namespace";
    String NOTIFICATION = "notification";
    String OUTPUT = "output";
    String PRESENCE = "presence";
    String PREFIX = "prefix";
    String REFERENCE = "reference";
    String REFINE = "refine";
    String REVISION = "revision";
    String REVISION_DATE = "revision-date";
    String RPC = "rpc";
    String STATUS = "status";
    String SUBMODULE = "submodule";
    String TYPE = "type";
    String TYPEDEF = "typedef";
    String UNIQUE = "unique";
    String USES = "uses";
    String WHEN = "when";
    String YANG_VERSION = "yang-version";
}
