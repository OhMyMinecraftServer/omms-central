package net.zhuruoling.omms.central.foo;

public class SomeClass {
    private static int anInt;
    private static String string;
    private static boolean isItAShit;

    static {
        string = "wdnmd";
        anInt = string.hashCode();
        if (anInt > 114514) {
            isItAShit = true;
        }
    }

    public void wdnmd(){
        System.out.println("wdnmd");
    }

}
