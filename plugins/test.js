function onLoad(serverInterface){
    serverInterface.registerRequestCode(114512, "testFunc")
    serverInterface.getLogger().info("Test Plugin loaded!")
    serverInterface.getLogger().info("awa")
}

function testFunc(serverInterface, request){
    serverInterface.logger.info(request.toString())
}

function getMetadata(){
    return "{\"id\":\"test_plugin2\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"
}