/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mycompany.edca3;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import nz.sodium.*;

import static org.junit.Assert.*;

public class SMyTextAreaTest {
    protected StreamSink<String> streamSink;
    protected SMyTextArea textArea;

    @Before
    public void setUp() {
        streamSink = new StreamSink<>();
        textArea = new SMyTextArea(streamSink.hold(""));
        textArea.setRows(10);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of display streams and disappear after 3sec
     */
    @Test
    public void testDisplayStream() {
        System.out.println("Test display stream");
        streamSink.send("line1");
        streamSink.send("line2");
        assertEquals(textArea.getText(), "line1\nline2");
    }

    @Test
    public void testOverFlowRowCount() {
        System.out.println("Test OverFlow Row Count");
        streamSink.send("line1");
        streamSink.send("line2");
        streamSink.send("line3");
        streamSink.send("line4");
        streamSink.send("line5");
        streamSink.send("line6");
        streamSink.send("line7");
        streamSink.send("line8");
        streamSink.send("line9");
        streamSink.send("line10");
        streamSink.send("line11");
        assertEquals(textArea.getText(), "line2\nline3\nline4\nline5\nline6\nline7\nline8\nline9\nline10\nline11");
    }

    @Test
    public void testDisappearAfter3Sec() throws InterruptedException {
        System.out.print("Test Disappear after 3 seconds");
        streamSink.send("line1");
        Thread.sleep(1000);
        streamSink.send("line2");
        Thread.sleep(2000);
        assertEquals(textArea.getText(), "line2");
        Thread.sleep(1100);
        assertEquals(textArea.getText(), "");
    }
}
