package net.zhuruoling.omms.central.security;

import io.ktor.server.auth.UserPasswordCredential;
import net.zhuruoling.omms.central.main.RuntimeConstants;
import net.zhuruoling.omms.central.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class HttpAuthUtil {
    public static boolean checkTokenMatches(@NotNull UserPasswordCredential userPasswordCredential){
        boolean a = Arrays.stream(Objects.requireNonNull(RuntimeConstants.INSTANCE.getConfig()).getAuthorisedController()).toList().contains(userPasswordCredential.getName());
        boolean b = Objects.equals(calculateToken(userPasswordCredential.getName()), userPasswordCredential.getPassword());
        return a && b;
    }

    public static @NotNull String calculateToken(@NotNull String name){
        String tk = name + "O" + Util.getTimeCode();
        return Util.calculateTokenByDate(name.hashCode()) + tk + Util.base64Encode(tk);
    }


}