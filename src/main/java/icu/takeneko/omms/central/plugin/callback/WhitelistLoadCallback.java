package icu.takeneko.omms.central.plugin.callback;

import icu.takeneko.omms.central.whitelist.WhitelistManager;
import icu.takeneko.omms.central.whitelist.WhitelistManager;

import java.util.function.Consumer;

public class WhitelistLoadCallback extends Callback<WhitelistManager> {
    public static final WhitelistLoadCallback INSTANCE = new WhitelistLoadCallback();


    @Override
    public void register(Consumer<WhitelistManager> consumer) {
        consumers.add(consumer);
    }


}
