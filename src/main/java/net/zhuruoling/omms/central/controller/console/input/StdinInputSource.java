package net.zhuruoling.omms.central.controller.console.input;

import java.util.Scanner;

public class StdinInputSource extends InputSource {
    Scanner scanner = new Scanner(System.in);

    @Override
    public String getLine() {
        if (scanner.hasNext()) {
            return scanner.nextLine();
        } else return null;
    }
}
