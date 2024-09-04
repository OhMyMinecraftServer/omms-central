package icu.takeneko.omms.central.fundation.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import lombok.Getter;

@Getter
public class Identifier {
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

    public static Identifier of(String path) {
        return new Identifier("omms", path);
    }
}
