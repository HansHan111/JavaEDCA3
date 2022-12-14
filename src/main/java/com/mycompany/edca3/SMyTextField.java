/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.edca3;

import nz.sodium.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class SMyTextField extends JTextField {
    public SMyTextField(Cell<String> cText) {
        this(Operational.updates(cText));
    }

    public SMyTextField(Stream<String> sText) {
        this.setEditable(false);
        timer = new Timer();
        setup(sText);
    }

    private void setup(Stream<String> sText) {
        // eraseRow = new EraseRow(this, timer);
        sText.listen(text -> {
            super.setText(text);
            timer.cancel();
            timer = new Timer();
            eraseRow = new EraseRow(this, timer);
            timer.schedule(eraseRow, 3000);
        });
    }

    private Timer timer;
    private EraseRow eraseRow;

    private class EraseRow extends TimerTask {
        public Timer timer;
        public SMyTextField textFiled;

        public EraseRow(SMyTextField textFiled, Timer timer) {
            this.textFiled = textFiled;
            this.timer = timer;
        }

        public void run() {
            this.textFiled.setText("");
            this.timer.cancel();
        }
    }
}
