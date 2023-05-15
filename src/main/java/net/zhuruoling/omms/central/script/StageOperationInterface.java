package net.zhuruoling.omms.central.script;

import net.zhuruoling.omms.central.network.session.SessionContext;

abstract public class StageOperationInterface extends OperationInterface {
    public StageOperationInterface(SessionContext sessionContext, String name) {
        super(sessionContext, name);
    }
}
