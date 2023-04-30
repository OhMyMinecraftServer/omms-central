package net.zhuruoling.omms.central.old.plugin;

import net.zhuruoling.omms.central.network.session.SessionContext;

abstract public class StageOperationProxy extends OperationProxy{
    public StageOperationProxy(SessionContext sessionContext, String name) {
        super(sessionContext, name);
    }
}
