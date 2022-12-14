package com.mycompany.edca3;

import nz.sodium.*;

import java.util.LinkedList;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

public class SMyTable extends JTable {
    public SMyTable(LinkedList<LinkedList<Cell<String>>> data, String[] columnLabels) {
        setup(data, columnLabels);
    }

    private void setup(LinkedList<LinkedList<Cell<String>>> data, String[] columnLabels) {
        int rowCount = data.size();
        int columnCount = columnLabels.length;
        this.setModel(new javax.swing.table.DefaultTableModel(
                new Object[rowCount][columnCount],
                columnLabels) {
            Class[] types = new Class[] {
                    java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[] {
                    false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types[columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });

        for (int i = 0; i < data.size(); i++) {
            LinkedList<Cell<String>> rowData = data.get(i);
            for (int j = 0; j < rowData.size(); j++) {
                Cell<String> cellData = rowData.get(j);
                Cell<Integer> rowNum = new Cell<Integer>(i);
                Cell<Integer> colNum = new Cell<Integer>(j);
                Cell<UpdateAction> cellActions = cellData.lift(rowNum, colNum,
                        (str, r, c) -> new UpdateAction(r, c, str));
                cellActions.listen(action -> {
                    this.setValueAt(action.data, action.rowNum, action.columnNum);
                });
            }
        }

    }

    private class UpdateAction {
        public int rowNum;
        public int columnNum;
        public String data;

        public UpdateAction(int rowNum, int colNum, String data) {
            this.rowNum = rowNum;
            this.columnNum = colNum;
            this.data = data;
        }
    }
}
