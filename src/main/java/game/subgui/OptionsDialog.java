package game.subgui;

import game.gui.StartMenu;

import javax.swing.JDialog;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dialog;

public class OptionsDialog extends JDialog {
    public OptionsDialog(StartMenu parentFrame, boolean isModal) {
        super(parentFrame, isModal);

        setTitle("Options:");
        setModalExclusionType(Dialog.ModalExclusionType.NO_EXCLUDE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setBackground(Color.DARK_GRAY);
        getRootPane().setBorder(new EmptyBorder(5, 10, 5, 10));

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
