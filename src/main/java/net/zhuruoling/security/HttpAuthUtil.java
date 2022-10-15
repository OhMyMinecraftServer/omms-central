package net.zhuruoling.security;

import io.ktor.server.auth.UserPasswordCredential;
import net.zhuruoling.main.RuntimeConstants;
import net.zhuruoling.util.Util;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;

public class HttpAuthUtil {
    public static boolean checkTokenMatches(@NotNull UserPasswordCredential userPasswordCredential){
        boolean a = Arrays.stream(Objects.requireNonNull(RuntimeConstants.INSTANCE.getConfig()).getAuthorisedController()).toList().contains(userPasswordCredential.getName());
        boolean b = Objects.equals(calculateToken(userPasswordCredential.getName()), userPasswordCredential.getPassword());
        return a && b;
    }

    public static String calculateToken(String name){
        String tk = name + "O" + Util.getTimeCode();
        return Util.calculateTokenByDate(name.hashCode()) + tk + Util.base64Encode(tk);
    }


}
