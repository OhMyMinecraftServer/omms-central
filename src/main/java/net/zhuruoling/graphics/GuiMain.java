package net.zhuruoling.graphics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class GuiMain extends Thread {
    private final Logger logger = LoggerFactory.getLogger("Gui");
    public GuiMain() {
        super();
    }

    public void show(){
        this.start();
    }

    @Override
    public void run() {
        Thread that = this;
        Frame frame = new Frame("OMMS Central");
        frame.setSize(1024, 768);
        frame.setBackground(new Color(0xff, 0xff,0xff));
        frame.setResizable(false);
        frame.setLayout(new FlowLayout());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                logger.info("Gui Window Closing.");
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}
