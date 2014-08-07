package waveTools.msb.secondaryWindows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;

import waveTools.msb.MobileScriptBuilder;
import waveTools.msb.resources.Helpers;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Properties;

@SuppressWarnings("serial")
public class Settings extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private boolean dirty;
	private JTextField tbCore2Location;
	private Properties config;
	private MobileScriptBuilder mainWindow = MobileScriptBuilder.getInstance();

	public Settings() {
		setTitle("MobileScriptBuilder - Settings");
		setBounds(100, 100, 540, 128);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNgecoreLocation = new JLabel("NGECore2 Location");
			lblNgecoreLocation.setBounds(12, 23, 120, 15);
			contentPanel.add(lblNgecoreLocation);
		}
		
		tbCore2Location = new JTextField();
		tbCore2Location.setBounds(227, 16, 287, 28);
		contentPanel.add(tbCore2Location);
		tbCore2Location.setColumns(10);
		
		String coreLocation = mainWindow.getCoreLocation();
		if (coreLocation != null && !coreLocation.equals("") && !coreLocation.equals(" ")) {
			tbCore2Location.setText(coreLocation);
		}
		
		JButton btnBrowse = new JButton("Browse..");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openCoreLocationWindow();
			}
		});
		btnBrowse.setBounds(127, 16, 90, 28);
		contentPanel.add(btnBrowse);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (dirty) {
							
							Helpers.showMessageBox(contentPanel, "Settings updated!");
							mainWindow.setCoreLocation(tbCore2Location.getText());
							mainWindow.populateMobilesTree(new File(tbCore2Location.getText() + "\\scripts\\mobiles"));
							
							try {
								FileOutputStream out = new FileOutputStream("./config.cfg");
								
								config.setProperty("CoreLocation", tbCore2Location.getText());
								
								config.store(out, null);
								out.close();
							} catch (Exception e) { Helpers.showExceptionError(contentPanel, e.getLocalizedMessage()); }
							
							//setVisible(false);
							setDirty(false);
						} else {
							//setVisible(false);
						}
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if (dirty) { } // TODO: You have unsaved changes, are you sure you want to cancel?
						//setVisible(false);
						dispose();
					}
					
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		postLoad();
	}
	private void postLoad() {
		config = mainWindow.getConfig();
		if (config == null) {
			File configFile = new File("./config.cfg");
			
			if (!configFile.exists()) {
				try {
					configFile.createNewFile();
					PrintWriter writer = new PrintWriter(configFile, "UTF-8");
					writer.print("CoreLocation=");
					writer.close();
				}  catch (Exception e) { Helpers.showExceptionError(contentPanel, e.getLocalizedMessage()); }
			}
			try {
				FileInputStream inputStream = new FileInputStream(configFile);
				config.load(inputStream);
				tbCore2Location.setText(config.getProperty("CoreLocation"));
				inputStream.close();
			} catch (Exception e) { Helpers.showExceptionError(contentPanel, e.getLocalizedMessage()); }
			mainWindow.setConfig(config);
		}
	}
	private void openCoreLocationWindow() {
		JFileChooser coreFolderSelect = new JFileChooser();
		coreFolderSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		coreFolderSelect.setDialogTitle("Select NGECore2 folder");
		int success = coreFolderSelect.showOpenDialog(contentPanel);
		
		if (success == JFileChooser.APPROVE_OPTION) {
			File selectedDirectory = coreFolderSelect.getSelectedFile();
			String corePath = selectedDirectory.getAbsolutePath();
			if (!selectedDirectory.getName().equals("NGECore2")) {
				File[] childrenFolders = selectedDirectory.listFiles();
				String coreFolder = "";
				for (File childrenFolder : childrenFolders) {
					if (childrenFolder.getName().equals("NGECore2")) {
						coreFolder = childrenFolder.getAbsolutePath();
						break;
					}
				}
				if (coreFolder.equals("")) {
					Helpers.showMessageBox(coreFolderSelect, "Could not find NGECore2 in the directory " + selectedDirectory.getAbsolutePath());
					openCoreLocationWindow();
					return;
				} else { corePath = coreFolder; }
			}
			tbCore2Location.setText(corePath);
			
			if (!dirty)
				dirty = true;
		}
	}

	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
}
