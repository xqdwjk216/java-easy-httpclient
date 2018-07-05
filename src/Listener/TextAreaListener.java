/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.undo.UndoManager;

/**
 *
 * @author gavin
 */
public class TextAreaListener implements KeyListener {

	private JTextArea textarea;
	private JLabel console;
	private UndoManager um;

	public TextAreaListener(JTextArea textarea, JLabel console) {
		this.textarea = textarea;
		this.console = console;
		this.um = new UndoManager();
		textarea.getDocument().addUndoableEditListener(um);
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		String key = Integer.toString(keyCode);

		if (e.isControlDown()) {
			switch (keyCode) {
				case KeyEvent.VK_Z:
					console.setText("撤销");
					if (um.canUndo()) {
						um.undo();
					}
					break;
				case KeyEvent.VK_Y:
					console.setText("重做");
					if (um.canRedo()) {
						um.redo();
					}
					break;
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

}
