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

import nz.sodium.*;

import static org.junit.Assert.*;

import java.util.LinkedList;

import javax.swing.table.TableColumn;

public class SMyTableTest {
    protected LinkedList<LinkedList<StreamSink<String>>> streamSinkArray;
    protected SMyTable table;

    protected int rowCount = 10;
    protected int colCount = 4;

    protected String[] columnLabels = { "label1", "label2", "label3", "label4" };

    @Before
    public void setUp() {
        streamSinkArray = new LinkedList<>();
        LinkedList<LinkedList<Cell<String>>> data = new LinkedList<>();
        for (int i = 0; i < rowCount; i++) {
            LinkedList<StreamSink<String>> rowStream = new LinkedList<>();
            LinkedList<Cell<String>> rowCell = new LinkedList<>();
            for (int j = 0; j < colCount; j++) {
                StreamSink<String> streamSink = new StreamSink<>();
                rowStream.add(streamSink);
                rowCell.add(streamSink.hold(""));
            }
            streamSinkArray.add(rowStream);
            data.add(rowCell);
        }
        table = new SMyTable(data, columnLabels);
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testDisplayStreamData() {
        System.out.println("Test Display Stream Data");
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                streamSinkArray.get(i).get(j).send("cell" + i + "-" + j);
                assertEquals(table.getValueAt(i, j), "cell" + i + "-" + j);
            }
        }
    }

}
