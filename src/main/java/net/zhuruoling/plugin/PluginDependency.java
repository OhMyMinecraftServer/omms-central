package net.zhuruoling.plugin;


import java.lang.module.ModuleDescriptor;
import java.util.List;

public record PluginDependency(List<Dependency> dependencies) {

    public enum Operator {
        EQUAL, GREATER, LESS,
    }

    public abstract static class Dependency {
        protected abstract String getId();

        protected abstract Operator getOperator();

        protected abstract ModuleDescriptor.Version getVersion();

        public static Dependency of(String id, Operator operator, ModuleDescriptor.Version version){
            return new Dependency() {
                @Override
                protected String getId() {
                    return id;
                }

                @Override
                protected Operator getOperator() {
                    return operator;
                }

                @Override
                protected ModuleDescriptor.Version getVersion() {
                    return version;
                }
            };
        }

        public static Dependency of(String id, Operator operator, String version){
            return new Dependency() {
                @Override
                protected String getId() {
                    return id;
                }

                @Override
                protected Operator getOperator() {
                    return operator;
                }

                @Override
                protected ModuleDescriptor.Version getVersion() {
                    return ModuleDescriptor.Version.parse(version);
                }
            };
        }

        @Override
        public String toString() {
            return "Dependency{id=%s,operator=%s,version=%s}".formatted(getId(), getOperator().name(), getVersion());
        }
    }

    @Override
    public String toString() {
        return "PluginDependency{" +
                "dependencies=" + dependencies +
                '}';
    }
}
