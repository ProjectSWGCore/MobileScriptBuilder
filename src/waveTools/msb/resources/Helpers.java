package waveTools.msb.resources;

import java.awt.Component;

import javax.swing.JOptionPane;

public class Helpers {
	public static void showMessageBox(Component parent, String message) {
		JOptionPane.showMessageDialog(parent, message, "Mobile Script Builder", JOptionPane.INFORMATION_MESSAGE, null);
	}
	
	public static void showExceptionError(Component parent, String stackTrace) {
		JOptionPane.showMessageDialog(parent, "ERROR " + stackTrace, "Mobile Script Builder - ERROR", JOptionPane.ERROR_MESSAGE, null);
	}
}
