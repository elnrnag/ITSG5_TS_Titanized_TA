/**
 * @author	ETSI
 * @version	$id$
 */
package org.etsi.common;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicOptionPaneUI;


/**
 * 
 */
public class OperatorGUI extends JDialog implements ActionListener {

	/**
     * 
     */
    private static final long serialVersionUID = 5803780216264271353L;

    private boolean result = false;
    private JButton passButton = new JButton("Pass");
    private JButton failButton = new JButton("Fail");
    
    /**
	 * Constructor
	 */
	public OperatorGUI() {
		//empty
	}
	
    public boolean pop(String msg) {

        // Get the size of the screen
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        // Dialog settings
        setTitle("Operator Action on SUT");
        setMinimumSize(new Dimension(300, 80));
        setAlwaysOnTop(true);
        setModal(true);        
        setResizable(false);
        int x = (dim.width - getWidth()) / 2;
        int y = (dim.height - getHeight()) / 2;
        setLocation(x, y);
        
        // Content        
        setLayout(new GridLayout(2, 1));
        add(new JLabel(msg));        
        JPanel panel = new JPanel();
        panel.setLayout(new BasicOptionPaneUI.ButtonAreaLayout(isModal(), 2));
        panel.add(passButton);        
        panel.add(failButton);
        add(panel);
        passButton.addActionListener(this);
        failButton.addActionListener(this);
        
        // Show !
        pack();
        setVisible(true);
        return result;
    }
    
	@Override
	public void actionPerformed(ActionEvent arg0) {
	    if (arg0.getSource().equals(passButton)) {
	        result = true;
	    }
	    setVisible(false);
	}

}
