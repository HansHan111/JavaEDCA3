/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mycompany.edca3;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nz.sodium.StreamSink;

import static org.junit.Assert.*;

public class SMyTextFieldTest {

    protected StreamSink<String> streamSink;
    protected SMyTextField textField;

    @Before
    public void setUp() {
        streamSink = new StreamSink<>();
        textField = new SMyTextField(streamSink.hold(""));
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDisplayStream() {
        System.out.print("Test Display Stream");
        streamSink.send("text1");
        assertEquals(textField.getText(), "text1");
        streamSink.send("text2");
        assertEquals(textField.getText(), "text2");
    }

    @Test
    public void testDisappearAfter3Sec() throws InterruptedException {
        System.out.print("Test Disappear after 3 seconds");
        streamSink.send("text1");
        Thread.sleep(1000);
        streamSink.send("text2");
        Thread.sleep(2000);
        assertEquals(textField.getText(), "text2");
        Thread.sleep(1100);
        assertEquals(textField.getText(), "");
    }

}
