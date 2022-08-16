package wjl.yang.model;

import java.util.Objects;

/**
 * 模块名和版本号。
 *
 * @author wujinlong
 * @since 2022-8-16
 */
public class ModuleNameVersion {
    private final String name;
    private final String version;

    public ModuleNameVersion(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleNameVersion that = (ModuleNameVersion) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(version, that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, version);
    }

    @Override
    public String toString() {
        if (version != null) {
            return name + "@" + version;
        } else {
            return name;
        }
    }
}
