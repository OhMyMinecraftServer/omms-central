package icu.takeneko.omms.central.fundation.registry;

import com.google.common.base.Objects;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;

@Getter
public class Identifier implements Comparable<Identifier> {
    public static final Codec<Identifier> CODEC = Codec.STRING.comapFlatMap(
            Identifier::read,
            Identifier::toString
    ).stable();

    private final String namespace;
    private final String path;

    public Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public Identifier(String s) {
        this(decompose(s, ':'));
    }

    protected static String[] decompose(String location, char separator) {
        String[] strings = new String[]{"omms", location};
        int i = location.indexOf(separator);
        if (i >= 0) {
            strings[1] = location.substring(i + 1);
            if (i >= 1) {
                strings[0] = location.substring(0, i);
            }
        }
        return strings;
    }

    private Identifier(String[] decomposed) {
        this(decomposed[0], decomposed[1]);
    }

    public static DataResult<Identifier> read(String location) {
        try {
            return DataResult.success(new Identifier(location));
        } catch (Exception var2) {
            return DataResult.error(() -> "Not a valid resource location: " + location + " " + var2.getMessage());
        }
    }

    @Override
    public String toString() {
        return this.namespace + ":" + this.path;
    }

    public int compareTo(Identifier identifier) {
        int wtf = this.path.compareTo(identifier.path);
        if (wtf == 0) {
            wtf = this.namespace.compareTo(identifier.namespace);
        }

        return wtf;
    }

    public static Identifier of(String path) {
        return new Identifier("omms", path);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier that)) return false;
        return java.util.Objects.equals(namespace, that.namespace) && java.util.Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.namespace, this.path);
    }
}
