package net.zhuruoling.omms.central.util.io;

import net.zhuruoling.omms.central.network.session.RateLimitEncryptedSocket;
import net.zhuruoling.omms.central.network.session.response.Response;
import net.zhuruoling.omms.central.network.session.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptedSocketPrintTarget extends PrintTarget<RateLimitEncryptedSocket> {
    private final Logger logger = LoggerFactory.getLogger("EncryptedSocketPrintTarget");
    public EncryptedSocketPrintTarget(RateLimitEncryptedSocket encryptedSocket) {
        super(encryptedSocket);
    }

    Response responseBuilder(String content){
        return new Response().withResponseCode(Result.OK).withContentPair("contentType","ContinuousLogOutput").withContentPair("content",content);
    }

    @Override
    void println(RateLimitEncryptedSocket target, String content) {
        try {
            logger.info(content);
            target.sendResponse(responseBuilder(content));
        }catch (Exception e){
            throw new RuntimeException("Error occurred while sending log to client.",e);
        }
    }


}
