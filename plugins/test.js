function onLoad(serverInterface){
    serverInterface.registerRequestCode("TEST1", "testFunc")
    serverInterface.getLogger().info("Test Plugin loaded!")
    serverInterface.getLogger().info("awa")
}

function testFunc(serverInterface, request){
    serverInterface.getLogger().info(request.toString())
    serverInterface.sendBack("HELLO",["World"])
}

function getMetadata(){
    return "{\"id\":\"test_plugin2\",\"version\":\"0.0.1\",\"author\":\"ZhuRuoLing\"}"
}

function onUnload(serverInterface){
    serverInterface.getLogger().info("Test Plugin unloaded!")
}
