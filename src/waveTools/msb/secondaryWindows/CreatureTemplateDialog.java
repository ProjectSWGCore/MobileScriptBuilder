package waveTools.msb.secondaryWindows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;

import javax.swing.JTextField;

import waveTools.msb.MobileScriptBuilder;
import waveTools.msb.resources.Helpers;

import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CreatureTemplateDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private MobileScriptBuilder mainWnd = MobileScriptBuilder.getInstance();
	private JTextField tbCreatureTempName;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			CreatureTemplateDialog dialog = new CreatureTemplateDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public CreatureTemplateDialog() {
		setTitle("Creature Template Editor - MSB");
		setBounds(100, 100, 294, 110);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{33, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblCreatureTemplate = new JLabel("Creature Template");
			GridBagConstraints gbc_lblCreatureTemplate = new GridBagConstraints();
			gbc_lblCreatureTemplate.insets = new Insets(0, 0, 0, 5);
			gbc_lblCreatureTemplate.anchor = GridBagConstraints.EAST;
			gbc_lblCreatureTemplate.gridx = 0;
			gbc_lblCreatureTemplate.gridy = 0;
			contentPanel.add(lblCreatureTemplate, gbc_lblCreatureTemplate);
		}
		{
			tbCreatureTempName = new JTextField();
			GridBagConstraints gbc_tbCreatureTempName = new GridBagConstraints();
			gbc_tbCreatureTempName.fill = GridBagConstraints.HORIZONTAL;
			gbc_tbCreatureTempName.gridx = 1;
			gbc_tbCreatureTempName.gridy = 0;
			contentPanel.add(tbCreatureTempName, gbc_tbCreatureTempName);
			tbCreatureTempName.setColumns(10);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String template = tbCreatureTempName.getText();
						
						if (!template.startsWith("object/mobile/") || !template.endsWith(".iff")) {
							Helpers.showMessageBox(contentPanel, template + " is an invalid weapon template.");
							return;
						}
						
						if (mainWnd.getCreatureTemps() != null && mainWnd.getCreatureTemps().size() == 0) {
							DefaultListModel<String> model = new DefaultListModel<String>();
							model.addElement(template);
							mainWnd.setCreatureTemps(model);
							mainWnd.getListWeaponTemps().setModel(model);
						} else if (mainWnd.getCreatureTemps().contains(template)) {
							// Nothing changed, nothing needs to be done.
						}  else {
							mainWnd.getCreatureTemps().addElement(template); 
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
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void setTbCreatureTempNameText(String text) {
		this.tbCreatureTempName.setText(text);
	}
}
