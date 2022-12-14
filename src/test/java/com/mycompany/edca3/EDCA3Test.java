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
import static org.junit.Assert.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class EDCA3Test {
    protected EDCA3 app;
    protected DataProcessor dataProcessor;

    @Before
    public void setUp() {
        String[] args = null;
        EDCA3.main(args);
        while (app == null) {
            app = EDCA3.getInstance();
        }
        dataProcessor = app.getDataProcessor();
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class EDCA3.
     * 
     * @throws InterruptedException
     */
    @Test
    public void testRestrictionPanel() throws InterruptedException {
        System.out.println("test Restriction Panel");

        JTextField txtFromLat = null;
        JTextField txtFromLong = null;
        JTextField txtToLat = null;
        JTextField txtToLong = null;
        JButton restrictionButton = null;
        JLabel restrictionLabel = null;

        while (txtFromLat == null) {
            Thread.sleep(300);
            txtFromLat = app.getTxtFromLat();
        }
        while (txtFromLong == null) {
            Thread.sleep(300);
            txtFromLong = app.getTxtFromLong();
        }
        while (txtToLat == null) {
            Thread.sleep(300);
            txtToLat = app.getTxtToLat();
        }
        while (txtToLong == null) {
            Thread.sleep(300);
            txtToLong = app.getTxtToLong();
        }
        while (restrictionButton == null) {
            Thread.sleep(300);
            restrictionButton = app.getBtnRestriction();
        }
        while (restrictionLabel == null) {
            Thread.sleep(300);
            restrictionLabel = app.getSRestrictionLabel();
        }

        txtFromLat.setText("40");
        txtFromLong.setText("40");
        txtToLat.setText("50");
        txtToLong.setText("50");

        restrictionButton.doClick();

        // wait render
        Thread.sleep(300);

        assertEquals(restrictionLabel.getText(),
                "Restriction Range(Latitude: 40.0000~50.0000, Longitude: 40.0000~50.0000)");
    }

}
