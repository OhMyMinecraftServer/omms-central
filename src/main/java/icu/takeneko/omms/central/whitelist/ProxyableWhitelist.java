package icu.takeneko.omms.central.whitelist;

import java.util.List;

public interface ProxyableWhitelist extends Whitelist{
    void onDelegateRemove(Whitelist instance);

    void onDelegateCreate(Whitelist instance);

    List<String> getAliases();
}
