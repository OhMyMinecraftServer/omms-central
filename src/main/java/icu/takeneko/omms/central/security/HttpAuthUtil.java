package icu.takeneko.omms.central.security;

import icu.takeneko.omms.central.config.Config;
import icu.takeneko.omms.central.util.Util;
import io.ktor.server.auth.UserPasswordCredential;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class HttpAuthUtil {
    public static boolean checkTokenMatches(@NotNull UserPasswordCredential userPasswordCredential) {
        boolean a = Config.INSTANCE.getConfig().getAuthorisedController().contains(userPasswordCredential.getName());
        boolean b = Objects.equals(calculateToken(userPasswordCredential.getName()), userPasswordCredential.getPassword());
        return a && b;
    }

    public static @NotNull String calculateToken(@NotNull String name) {
        String tk = name + "O" + Util.getTimeCode();
        return Util.calculateTokenByDate(name.hashCode()) + tk + Util.base64Encode(tk);
    }
}