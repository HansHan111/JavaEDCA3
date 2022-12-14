/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.edca3;

import nz.sodium.*;
import javax.swing.*;
import java.util.*;
import java.util.Timer;

public class SMyTextArea extends JTextArea {
    public SMyTextArea(Cell<String> cText) {
        this(Operational.updates(cText));
    }

    public SMyTextArea(Stream<String> sText) {
        this.rowDatas = new LinkedList<>();
        setup(sText);
    }

    private void setup(Stream<String> sText) {
        sErase = new StreamSink<>();

        l = sText.listen(text -> {
            addRow(text);
        }).append(
                sErase.listen(rowData -> {
                    this.removeRow(rowData);
                }));
    }

    private void addRow(String text) {
        RowData rowData = new RowData(text, 3000, this.sErase);
        this.rowDatas.add(rowData);
        if (this.rowDatas.size() > this.getRows()) {
            RowData firstRow = rowDatas.remove();
            firstRow.timer.cancel();
            firstRow.timer.purge();
        }
        this.setText();
    }

    private void setText() {
        String str = new String();
        Boolean isFirst = true;
        for (RowData r : this.rowDatas) {
            if (!isFirst) {
                str = str.concat("\n");
            }
            str = str.concat(r.text);
            isFirst = false;
        }
        super.setText(str);
    }

    private void removeRow(RowData rowData) {
        if (this.rowDatas.contains(rowData)) {
            this.rowDatas.remove(rowData);
        }
        setText();
    }

    public LinkedList<RowData> rowDatas;
    public Listener l;
    private StreamSink<RowData> sErase;

    private class RowData {
        public String text;
        public Timer timer;
        private int delay;
        private StreamSink<RowData> sErase;

        public RowData(String text, int delay, StreamSink<RowData> sErase) {
            this.text = text;
            this.timer = new Timer();
            this.delay = delay;
            this.sErase = sErase;
            EraseRow eraseRow = new EraseRow(this.timer, this, sErase);
            this.timer.schedule(eraseRow, delay);
        }

    }

    private class EraseRow extends TimerTask {
        public Timer timer;
        public RowData rowData;
        public StreamSink<RowData> stream;

        public EraseRow(Timer t, RowData rowData, StreamSink<RowData> stream) {
            this.timer = t;
            this.rowData = rowData;
            this.stream = stream;
        }

        public void run() {
            stream.send(this.rowData);
            this.timer.cancel();
            this.timer.purge();
        }
    }
}
