package com.mycompany.edca3;

import nz.sodium.*;
import javax.swing.*;
import javax.swing.SwingUtilities;

public class SLabel extends JLabel {
    public SLabel(Cell<String> text) {
        super("");
        l = Operational.updates(text).listen(t -> {
            if (SwingUtilities.isEventDispatchThread())
                setText(t);
            else
                SwingUtilities.invokeLater(() -> {
                    setText(t);
                });
        });
        // Set the text at the end of the transaction so SLabel works
        // with CellLoops.
        Transaction.post(
                () -> SwingUtilities.invokeLater(() -> {
                    setText(text.sample());
                }));
    }

    private final Listener l;

    public void removeNotify() {
        l.unlisten();
        super.removeNotify();
    }
}
