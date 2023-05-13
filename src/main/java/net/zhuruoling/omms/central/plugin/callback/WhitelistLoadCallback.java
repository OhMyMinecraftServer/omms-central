package net.zhuruoling.omms.central.plugin.callback;

import net.zhuruoling.omms.central.whitelist.WhitelistManager;

import java.util.function.Consumer;

public class WhitelistLoadCallback extends Callback<WhitelistManager> {
    public final WhitelistLoadCallback INSTANCE = new WhitelistLoadCallback();


    @Override
    public void register(Consumer<WhitelistManager> consumer) {
        consumers.add(consumer);
    }


}
