package waveTools.msb.secondaryWindows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.NumberFormatter;
import javax.swing.UIManager;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import waveTools.msb.MobileScriptBuilder;
import waveTools.msb.resources.Helpers;
import waveTools.msb.resources.Weapon;
import waveTools.msb.resources.enums.WeaponType;

import javax.swing.JFormattedTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class WeaponTemplateDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField tbWeaponTemp;
	private MobileScriptBuilder mainWnd = MobileScriptBuilder.getInstance();
	private JFormattedTextField tbAttackSpeed;
	private JComboBox<WeaponType> cmbWeaponType;
	private boolean editMode;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		try {
			WeaponTemplateDialog dialog = new WeaponTemplateDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public WeaponTemplateDialog() {
		setTitle("MobileScriptBuilder - Edit Weapon Template");
		setBounds(100, 100, 356, 187);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FormLayout(new ColumnSpec[] {
				FormFactory.RELATED_GAP_COLSPEC,
				FormFactory.DEFAULT_COLSPEC,
				FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		{
			JLabel lblWeaponTemplateIff = new JLabel("Weapon Template IFF");
			contentPanel.add(lblWeaponTemplateIff, "2, 2, right, default");
		}
		{
			tbWeaponTemp = new JTextField();
			contentPanel.add(tbWeaponTemp, "4, 2, fill, default");
			tbWeaponTemp.setColumns(10);
		}
		{
			JLabel lblWeaponType = new JLabel("Weapon Type");
			contentPanel.add(lblWeaponType, "2, 4, right, default");
		}
		{
			cmbWeaponType = new JComboBox();
			cmbWeaponType.setModel(new DefaultComboBoxModel(WeaponType.values()));
			contentPanel.add(cmbWeaponType, "4, 4, fill, default");
		}
		{
			JLabel lblAttackSpeed = new JLabel("Attack Speed");
			contentPanel.add(lblAttackSpeed, "2, 6, right, default");
		}
		{
			tbAttackSpeed = new JFormattedTextField(new NumberFormatter(new DecimalFormat()));
			contentPanel.add(tbAttackSpeed, "4, 6, fill, default");
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						String weaponTemp = tbWeaponTemp.getText();
						if (!weaponTemp.startsWith("object/weapon/") || !weaponTemp.endsWith(".iff")) {
							Helpers.showMessageBox(contentPanel, weaponTemp + " is an invalid weapon template.");
							return;
						}
						String attackSpeed = "";
						if (tbAttackSpeed.getText() == null || tbAttackSpeed.getText().isEmpty() || tbAttackSpeed.getText() == "" || tbAttackSpeed.getText() == " ") {
							attackSpeed = "0.0";
						} else {
							attackSpeed = tbAttackSpeed.getText();
						}
						Weapon weapon = new Weapon(weaponTemp, cmbWeaponType.getSelectedIndex(), Float.valueOf(attackSpeed));
						
						if (mainWnd.getWeaponTemps() != null && mainWnd.getWeaponTemps().size() == 0) {
							DefaultListModel<String> model = new DefaultListModel<String>();
							model.addElement(weapon.toString());
							mainWnd.setWeaponTemps(model);
							mainWnd.getListWeaponTemps().setModel(model);
						} else if (mainWnd.getWeaponTemps().contains(weapon.toString())) {
							// Nothing changed, nothing needs to be done.
						} else if (editMode && mainWnd.getListWeaponTemps().getSelectedValue() != null) {
							mainWnd.getWeaponTemps().removeElement(mainWnd.getListWeaponTemps().getSelectedValue());
							mainWnd.getWeaponTemps().addElement(weapon.toString());
							editMode = false;
						} else {
							mainWnd.getWeaponTemps().addElement(weapon.toString()); 
						}
						
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}

	public JTextField getTbWeaponTemp() {
		return tbWeaponTemp;
	}
	public JComboBox getCmbWeaponType() {
		return cmbWeaponType;
	}
	public JFormattedTextField getTbAttackSpeed() {
		return tbAttackSpeed;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}
}
