package waveTools.msb;

import java.awt.Cursor;
import java.awt.EventQueue;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTree;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.text.NumberFormatter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import waveTools.msb.secondaryWindows.CreatureTemplateDialog;
import waveTools.msb.secondaryWindows.Settings;
import waveTools.msb.secondaryWindows.WeaponTemplateDialog;

import javax.swing.JScrollPane;

import java.awt.Label;

import javax.swing.DefaultListModel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

import waveTools.msb.resources.Helpers;
import waveTools.msb.resources.Mobile;
import waveTools.msb.resources.Weapon;

import java.awt.Color;

import javax.swing.border.BevelBorder;

import java.awt.Font;

import javax.swing.JFormattedTextField;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JLabel;

import waveTools.msb.resources.enums.Difficulty;
import waveTools.msb.resources.enums.WeaponType;

import javax.swing.JProgressBar;
import javax.swing.JCheckBox;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JPopupMenu;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.GridLayout;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.SoftBevelBorder;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class MobileScriptBuilder {

	private JFrame frmPswgToolsMbs;
	private JTree mobilesTree;
	private JTextField tbCreatureName;
	private JTextField tbScriptLocation;
	private JFormattedTextField tbCreatureLevel;
	private DefaultMutableTreeNode rootMobileTreeNode;

	private String coreLocation = "";
	private JComboBox<Difficulty> cmbDifficulty;

	private Mobile activeMobile;
	
	private JButton btnSave;
	private JList<String> listCreatureTemps;
	private JList<String> listWeaponTemps;
	private JFormattedTextField tbAttackRange;

	private JFormattedTextField tbAttackSpeed;
	private JList<String> listAttacks;

	private boolean mobilesLoaded;
	private JComboBox<WeaponType> cmbWeaponType;
	private Properties config;
	private JProgressBar prgMobilesLoading;
	private JComboBox<String> cmbDefaultAttack;
	private JFormattedTextField tbMinLevel;

	private JFormattedTextField tbMaxLevel;

	private JCheckBox chckbxDeathblowEnabled;
	
	private Vector<Mobile> modifiedTemplates = new Vector<Mobile>();
	@SuppressWarnings("rawtypes")
	private DefaultListModel weaponTemps;
	@SuppressWarnings("rawtypes")
	private DefaultListModel creatureTemps;
	@SuppressWarnings("rawtypes")
	private DefaultListModel attacks;
	private JButton btnBuildAll;
	private JButton btnBuildCurrent;
	
	public static MobileScriptBuilder instance;
	private WeaponTemplateDialog weaponTempDialog;
	private JButton btnRemoveWeapTemp;
	private JButton btnAddNewCreatureTemp;
	private JButton btnAddNewWeapTemp;
	private JButton btnEditWeapTemp;
	private JButton btnRemoveCreatureTemp;
	private JButton btnEditCreatureTemp;
	private JTextField tbSocialGroup;
	private JFormattedTextField tbMinSpawnDistance;
	private JFormattedTextField tbMaxSpawnDistance;
	private JFormattedTextField tbRespawnTime;
	private JComboBox cmbFaction;
	private JComboBox cmbFactionStatus;
	private JFormattedTextField tbAssistRange;
	private JCheckBox chckbxStalker;
	private JPanel tpResourceSettings;
	private JCheckBox chckbxHarvestable;
	private JCheckBox chckbxAgressive;
	private JCheckBox chckbxAttackable;
	private JCheckBox chckbxInvulnerable;
	
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					MobileScriptBuilder window = new MobileScriptBuilder();
					window.frmPswgToolsMbs.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MobileScriptBuilder() {
		initialize();

		// Perform these after UI setup
		try { createDependencies(); } catch (Exception e) { Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());}

		// Populate Default Attacks combobox
		Vector<String> defaultAttacks = new Vector<String>();
		try(BufferedReader br = new BufferedReader(new FileReader("./defaultAttacks.txt"))) {
			for(String line; (line = br.readLine()) != null; ) {
				if (line.equals("") || line.equals(" "))
					continue;

				defaultAttacks.add(line);
			}
		} catch (Exception e) { Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());}

		cmbDefaultAttack.setModel(new DefaultComboBoxModel<String>(defaultAttacks));

		// Populate Mobiles Tree
		if (coreLocation != null && coreLocation != "" && !mobilesLoaded) { populateMobilesTree(new File(coreLocation + "\\scripts\\mobiles")); }
		
		instance = this;
		
		// Load additional UI's
		weaponTempDialog = new WeaponTemplateDialog();
		
	}

	private void createDependencies() throws Exception {
		// Generate Configuration File
		config = new Properties();
		File configFile = new File("./config.cfg");

		if (!configFile.exists()) {
			Helpers.showMessageBox(frmPswgToolsMbs, "No config file detected. You must setup your settings to generate one (File -> Settings)");
		} else {
			try {
				FileInputStream configInput = new FileInputStream(configFile);
				config.load(configInput);
				configInput.close();

				coreLocation = config.getProperty("CoreLocation");
			} catch (Exception e) { Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage()); }
		}

		// Generate defaultAttacks.txt file
		File dAttacksFile = new File("./defaultAttacks.txt");
		if (!dAttacksFile.exists()) {
			dAttacksFile.createNewFile();
			PrintWriter writer = new PrintWriter(dAttacksFile, "UTF-8");
			Vector<String> dAttacks = new Vector<String>(Arrays.asList(new String[] {
					"creatureMeleeAttack", "creatureRangedAttack", "meleeHit", "rangedShot", "saberHit"
			}));

			dAttacks.stream().forEach(a -> writer.println(a));
			writer.close();
		}
	}

	public int fileCount(File folder, int count) {
		for (File file : folder.listFiles()) {
			if (file.isFile())
				count++;
			else
				count = fileCount(file, count);
		}
		return count;
	}

	public Mobile getActiveMobile() {
		return activeMobile;
	}

	public Properties getConfig() {
		return config;
	}

	public String getCoreLocation() {
		return coreLocation;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void initialize() {
		NumberFormatter basicIntFormat = new NumberFormatter();
		//basicIntFormat.setAllowsInvalid(false);
		basicIntFormat.setCommitsOnValidEdit(true);

		NumberFormatter decFormat = new NumberFormatter(new DecimalFormat());
		decFormat.setCommitsOnValidEdit(true);

		frmPswgToolsMbs = new JFrame();
		frmPswgToolsMbs.setResizable(false);
		frmPswgToolsMbs.setTitle("PSWGTools - Mobile Script Builder by Waverunner");
		frmPswgToolsMbs.setBounds(100, 100, 643, 499);
		frmPswgToolsMbs.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frmPswgToolsMbs.setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmLoadMobiles = new JMenuItem("Load Mobiles...");
		mntmLoadMobiles.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				JFileChooser mobilesFolderSelect = new JFileChooser();
				mobilesFolderSelect.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				mobilesFolderSelect.setDialogTitle("Select directory containing mobile scripts");
				if (coreLocation != null && !coreLocation.equals(" ") && !coreLocation.equals(""))
					mobilesFolderSelect.setCurrentDirectory(new File(coreLocation + "\\scripts"));
				
				int success = mobilesFolderSelect.showOpenDialog(frmPswgToolsMbs);

				if (success == JFileChooser.APPROVE_OPTION) {
					if (mobilesFolderSelect.getSelectedFile().getName().equals("mobiles"))
						populateMobilesTree(mobilesFolderSelect.getSelectedFile());
					else
						Helpers.showMessageBox(frmPswgToolsMbs, "Not a valid mobiles folder!");
					//mntmLoadMobiles.setEnabled(false);
				}
			}
		});
		mnFile.add(mntmLoadMobiles);

		JMenuItem mntmSettings = new JMenuItem("Settings..");
		mntmSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Settings sDialog = new Settings();
				sDialog.setVisible(true);
			}
		});
		mnFile.add(mntmSettings);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmAbout = new JMenuItem("About...");
		mnHelp.add(mntmAbout);

		frmPswgToolsMbs.getContentPane().setLayout(null);

		JScrollPane mobilesScrollPane = new JScrollPane();
		mobilesScrollPane.setBounds(12, 6, 200, 400);
		frmPswgToolsMbs.getContentPane().add(mobilesScrollPane);

		rootMobileTreeNode = new DefaultMutableTreeNode("No mobiles loaded");

		mobilesTree = new JTree(rootMobileTreeNode);
		mobilesTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				if (e.getNewLeadSelectionPath() == null || e.getNewLeadSelectionPath().getLastPathComponent() == null)
					return;
				
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
				DefaultMutableTreeNode priorNode = null;

				if (e.getOldLeadSelectionPath() != null)
					priorNode = (DefaultMutableTreeNode) e.getOldLeadSelectionPath().getLastPathComponent();

				if (priorNode != null && priorNode.isLeaf() && priorNode.getUserObject() instanceof Mobile) {
					//System.out.println("Prior selected node was " + ((Mobile)priorNode.getUserObject()).getCreatureName());
				}

				if (node != null && node.isLeaf() && node.getUserObject() instanceof Mobile) {
					Mobile mobile = (Mobile) node.getUserObject();
					populateScriptCreator(mobile);
					activeMobile = mobile;
					
					tbAttackRange.setEnabled(true);
					tbCreatureLevel.setEnabled(true);
					tbCreatureName.setEnabled(true);
					tbAttackSpeed.setEnabled(true);
					tbMaxLevel.setEnabled(true);
					tbMinLevel.setEnabled(true);
					cmbDifficulty.setEnabled(true);
					cmbWeaponType.setEnabled(true);
					cmbDefaultAttack.setEnabled(true);
					chckbxDeathblowEnabled.setEnabled(true);
					
					btnAddNewCreatureTemp.setEnabled(true);
					btnAddNewWeapTemp.setEnabled(true);
				}
			}
		});
		mobilesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		mobilesScrollPane.setViewportView(mobilesTree);
		
		JPopupMenu popupMenu = new JPopupMenu();
		addPopup(mobilesTree, popupMenu);
		
		JMenuItem mntmRefreshMobiles = new JMenuItem("Refresh Mobiles");
		mntmRefreshMobiles.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (mobilesLoaded)
					populateMobilesTree(new File(coreLocation + "\\scripts\\mobiles"));
			}
		});
		popupMenu.add(mntmRefreshMobiles);

		JPanel buttonsPane = new JPanel();
		buttonsPane.setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
		buttonsPane.setBounds(224, 400, 391, 42);
		frmPswgToolsMbs.getContentPane().add(buttonsPane);

		JButton btnAddMobile = new JButton("New Mobile");
		btnAddMobile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (!mobilesLoaded) {
					Helpers.showMessageBox(frmPswgToolsMbs, "You cannot do that because you have not loaded the mobiles folder yet!");
					return;
				}
				FileFilter pyFilter = new FileNameExtensionFilter("Mobile Script File", "py");
				JFileChooser newMobileDialog = new JFileChooser();
				newMobileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
				newMobileDialog.setDialogTitle("Select where to save the mobile script");
				newMobileDialog.setCurrentDirectory(new File(coreLocation + "\\scripts\\mobiles"));
				newMobileDialog.setDialogType(JFileChooser.SAVE_DIALOG);
				newMobileDialog.setFileFilter(pyFilter);
				int selectedIndex = newMobileDialog.showSaveDialog(frmPswgToolsMbs);
				if (selectedIndex == JFileChooser.APPROVE_OPTION) {
					File file = newMobileDialog.getSelectedFile();
					
					if (!file.getAbsolutePath().endsWith(".py")) {
						file = new File(file.getAbsolutePath() + ".py");
					}
					if (file.exists()) {
						Helpers.showMessageBox(frmPswgToolsMbs, "That script already exists! Please choose a different name!");
						return;
					}
					try {
						file.createNewFile();
						populateMobilesTree(new File(coreLocation + "\\scripts\\mobiles"));
					} catch (IOException e) {
						Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());
					}
				}
				/*DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) mobilesTree.getLeadSelectionPath().getLastPathComponent();
				
				if (currentNode.getUserObject() instanceof Mobile) {
					Mobile currentMobile = (Mobile) currentNode.getUserObject();

					String path = currentMobile.getScriptLocation();
					newMobile.setScriptLocation(path);
					
					DefaultMutableTreeNode newNode = new DefaultMutableTreeNode();
					newNode.setUserObject(newMobile);
					((DefaultMutableTreeNode)currentNode.getParent()).add(newNode);
					mobilesTree.updateUI();
				}*/
			}
		});
		buttonsPane.add(btnAddMobile);

		btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				saveCurrentValues();
			}
		});
		btnSave.setEnabled(false);
		buttonsPane.add(btnSave);
		
		btnBuildCurrent = new JButton("Build");
		btnBuildCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) mobilesTree.getSelectionPath().getLastPathComponent();
				if (currentNode == null || !(currentNode.getUserObject() instanceof Mobile))
					return;
				
				buildMobileScript((Mobile) currentNode.getUserObject());
				
				Helpers.showMessageBox(frmPswgToolsMbs, "Successfully Built (1) Mobile Script.");
			}
		});
		btnBuildCurrent.setEnabled(false);
		buttonsPane.add(btnBuildCurrent);
		
		btnBuildAll = new JButton("Build All");
		btnBuildAll.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				modifiedTemplates.stream().forEach(mobile -> { buildMobileScript(mobile); });
				Helpers.showMessageBox(frmPswgToolsMbs, "Successfully Built (" + modifiedTemplates.size() + ") Mobile Scripts.");
			}
		});
		btnBuildAll.setEnabled(false);
		buttonsPane.add(btnBuildAll);

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setBounds(224, 6, 391, 392);
		frmPswgToolsMbs.getContentPane().add(tabbedPane);

		JPanel tpGenSettings = new JPanel();
		tpGenSettings.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		tpGenSettings.setForeground(Color.BLACK);
		tabbedPane.addTab("General Settings", null, tpGenSettings, null);
		tpGenSettings.setLayout(null);

		Label label = new Label("Creature Name");
		label.setBounds(10, 55, 90, 23);
		tpGenSettings.add(label);

		Label label_1 = new Label("Script Location");
		label_1.setBounds(10, 18, 90, 23);
		tpGenSettings.add(label_1);

		tbCreatureName = new JTextField();
		tbCreatureName.setEnabled(false);
		tbCreatureName.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tbCreatureName.setBounds(106, 53, 279, 28);
		tpGenSettings.add(tbCreatureName);
		tbCreatureName.setColumns(10);

		tbScriptLocation = new JTextField();
		tbScriptLocation.setFont(new Font("SansSerif", Font.PLAIN, 12));
		tbScriptLocation.setEnabled(false);
		tbScriptLocation.setBounds(106, 13, 279, 28);
		tpGenSettings.add(tbScriptLocation);
		tbScriptLocation.setColumns(10);

		Label label_2 = new Label("Level");
		label_2.setBounds(10, 86, 33, 23);
		tpGenSettings.add(label_2);

		Label label_3 = new Label("Difficulty");
		label_3.setBounds(10, 120, 47, 23);
		tpGenSettings.add(label_3);

		cmbDifficulty = new JComboBox();
		cmbDifficulty.setEnabled(false);
		cmbDifficulty.setModel(new DefaultComboBoxModel(Difficulty.values()));
		cmbDifficulty.setSelectedIndex(0);
		cmbDifficulty.setBounds(59, 118, 74, 26);
		tpGenSettings.add(cmbDifficulty);

		tbCreatureLevel = new JFormattedTextField(basicIntFormat);
		tbCreatureLevel.setEnabled(false);
		tbCreatureLevel.setBounds(49, 84, 33, 28);
		tpGenSettings.add(tbCreatureLevel);

		JLabel lblAttackRange = new JLabel("Attack Range");
		lblAttackRange.setBounds(10, 161, 81, 16);
		tpGenSettings.add(lblAttackRange);

		tbAttackRange = new JFormattedTextField(decFormat);
		tbAttackRange.setEnabled(false);
		tbAttackRange.setBounds(88, 155, 33, 28);
		tpGenSettings.add(tbAttackRange);

		JLabel lblAttackSpeed = new JLabel("Attack Speed");
		lblAttackSpeed.setBounds(133, 161, 74, 16);
		tpGenSettings.add(lblAttackSpeed);

		tbAttackSpeed = new JFormattedTextField(decFormat);
		tbAttackSpeed.setEnabled(false);
		tbAttackSpeed.setBounds(205, 155, 33, 28);
		tpGenSettings.add(tbAttackSpeed);

		JScrollPane scrollPane_Attacks = new JScrollPane();
		scrollPane_Attacks.setBounds(10, 211, 207, 124);
		tpGenSettings.add(scrollPane_Attacks);

		listAttacks = new JList();
		listAttacks.setEnabled(false);
		listAttacks.setModel(new DefaultListModel());
		scrollPane_Attacks.setViewportView(listAttacks);

		JLabel lblAttacks = new JLabel("Attacks:");
		lblAttacks.setBounds(10, 189, 55, 16);
		tpGenSettings.add(lblAttacks);

		JButton btnAddAttack = new JButton("Add Attack");
		btnAddAttack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnAddAttack.setEnabled(false);
		btnAddAttack.setBounds(220, 221, 114, 28);
		tpGenSettings.add(btnAddAttack);

		JButton btnRemoveAttack = new JButton("Remove Attack");
		btnRemoveAttack.setEnabled(false);
		btnRemoveAttack.setBounds(220, 256, 114, 28);
		tpGenSettings.add(btnRemoveAttack);

		JLabel lblDefaultAttack = new JLabel("Default Attack");
		lblDefaultAttack.setBounds(150, 122, 81, 16);
		tpGenSettings.add(lblDefaultAttack);

		JLabel lblWeaponType = new JLabel("Weapon Type");
		lblWeaponType.setBounds(160, 189, 90, 16);
		tpGenSettings.add(lblWeaponType);

		cmbWeaponType = new JComboBox();
		cmbWeaponType.setEnabled(false);
		cmbWeaponType.setModel(new DefaultComboBoxModel(WeaponType.values()));
		cmbWeaponType.setBounds(244, 184, 141, 26);
		tpGenSettings.add(cmbWeaponType);

		cmbDefaultAttack = new JComboBox();
		cmbDefaultAttack.setEnabled(false);
		cmbDefaultAttack.setEditable(true);
		cmbDefaultAttack.setBounds(230, 117, 155, 26);
		tpGenSettings.add(cmbDefaultAttack);

		tbMinLevel = new JFormattedTextField(basicIntFormat);
		tbMinLevel.setEnabled(false);
		tbMinLevel.setBounds(150, 84, 33, 28);
		tpGenSettings.add(tbMinLevel);

		tbMaxLevel = new JFormattedTextField(basicIntFormat);
		tbMaxLevel.setEnabled(false);
		tbMaxLevel.setBounds(254, 84, 33, 28);
		tpGenSettings.add(tbMaxLevel);

		JLabel lblMinLevel = new JLabel("Min. Level");
		lblMinLevel.setBounds(94, 86, 55, 23);
		tpGenSettings.add(lblMinLevel);

		JLabel lblMaxLevel = new JLabel("Max Level");
		lblMaxLevel.setBounds(195, 86, 55, 23);
		tpGenSettings.add(lblMaxLevel);

		chckbxDeathblowEnabled = new JCheckBox("Deathblow Enabled");
		chckbxDeathblowEnabled.setEnabled(false);
		chckbxDeathblowEnabled.setSelected(true);
		chckbxDeathblowEnabled.setBounds(250, 160, 135, 18);
		tpGenSettings.add(chckbxDeathblowEnabled);

		JPanel tpCreatureTemplates = new JPanel();
		tpCreatureTemplates.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		tabbedPane.addTab("Creature Temps", null, tpCreatureTemplates, null);
		tpCreatureTemplates.setLayout(null);

		JScrollPane scrollPane_CreatureTemps = new JScrollPane();
		scrollPane_CreatureTemps.setBounds(10, 10, 375, 234);
		tpCreatureTemplates.add(scrollPane_CreatureTemps);

		creatureTemps = new DefaultListModel();
		listCreatureTemps = new JList();
		listCreatureTemps.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				try {
					if (creatureTemps.get(event.getFirstIndex()) != null) {
						btnRemoveCreatureTemp.setEnabled(true);
						btnEditCreatureTemp.setEnabled(true);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					btnRemoveCreatureTemp.setEnabled(false);
					btnEditCreatureTemp.setEnabled(false);
				}
			}
		});
		listCreatureTemps.setModel(creatureTemps);
		scrollPane_CreatureTemps.setViewportView(listCreatureTemps);
		
		JPanel panelCreatureTempBtns = new JPanel();
		panelCreatureTempBtns.setBounds(6, 255, 379, 42);
		tpCreatureTemplates.add(panelCreatureTempBtns);
		
				btnAddNewCreatureTemp = new JButton("Add");
				panelCreatureTempBtns.add(btnAddNewCreatureTemp);
				btnAddNewCreatureTemp.setEnabled(false);
				
						btnRemoveCreatureTemp = new JButton("Remove");
						panelCreatureTempBtns.add(btnRemoveCreatureTemp);
						btnRemoveCreatureTemp.setEnabled(false);
						
						btnEditCreatureTemp = new JButton("Edit");
						btnEditCreatureTemp.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								CreatureTemplateDialog dialog = new CreatureTemplateDialog();
								String template = listCreatureTemps.getSelectedValue();
								dialog.setTbCreatureTempNameText(template);
								
								dialog.setVisible(true);
							}
						});
						btnEditCreatureTemp.setEnabled(false);
						panelCreatureTempBtns.add(btnEditCreatureTemp);
						btnRemoveCreatureTemp.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								int index = listCreatureTemps.getSelectedIndex();
								creatureTemps.remove(index);
							}
						});
				btnAddNewCreatureTemp.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						CreatureTemplateDialog dialog = new CreatureTemplateDialog();
						dialog.setVisible(true);
					}
				});

		JPanel tpWeaponTemplates = new JPanel();
		tpWeaponTemplates.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		tabbedPane.addTab("Weapon Temps", null, tpWeaponTemplates, null);
		tpWeaponTemplates.setLayout(null);

		JScrollPane scrollPane_WeaponTemps = new JScrollPane();
		scrollPane_WeaponTemps.setBounds(6, 6, 375, 234);
		tpWeaponTemplates.add(scrollPane_WeaponTemps);
		
		weaponTemps = new DefaultListModel();
		listWeaponTemps = new JList();
		listWeaponTemps.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent event) {
				try {
					if (weaponTemps.get(event.getFirstIndex()) != null) {
						btnRemoveWeapTemp.setEnabled(true);
						btnEditWeapTemp.setEnabled(true);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					btnRemoveWeapTemp.setEnabled(false);
					btnEditWeapTemp.setEnabled(false);
				}
			}
		});
		listWeaponTemps.setModel(weaponTemps);
		scrollPane_WeaponTemps.setViewportView(listWeaponTemps);
		
		JPanel wpTmpBtnsPanel = new JPanel();
		wpTmpBtnsPanel.setBounds(6, 252, 375, 43);
		tpWeaponTemplates.add(wpTmpBtnsPanel);
		
				btnAddNewWeapTemp = new JButton("Add");
				btnAddNewWeapTemp.setEnabled(false);
				wpTmpBtnsPanel.add(btnAddNewWeapTemp);
				
						btnRemoveWeapTemp = new JButton("Remove");
						btnRemoveWeapTemp.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								int index = listWeaponTemps.getSelectedIndex();
								
								try {
									weaponTemps.remove(index);
								} catch (Exception e) { Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());}
							}
						});
						btnRemoveWeapTemp.setEnabled(false);
						wpTmpBtnsPanel.add(btnRemoveWeapTemp);
						
						btnEditWeapTemp = new JButton("Edit");
						btnEditWeapTemp.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent arg0) {
								String base = listWeaponTemps.getSelectedValue();
								String[] values = base.replace(" ", "").split(",");
								
								weaponTempDialog.getTbWeaponTemp().setText(values[0]);
								weaponTempDialog.getCmbWeaponType().setSelectedIndex(Integer.valueOf(values[1]));
								weaponTempDialog.getTbAttackSpeed().setText(values[2]);
								
								weaponTempDialog.setEditMode(true);
								
								weaponTempDialog.setVisible(true);
							}
						});
						btnEditWeapTemp.setEnabled(false);
						wpTmpBtnsPanel.add(btnEditWeapTemp);
						
						JPanel tpMiscSettings = new JPanel();
						tpMiscSettings.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
						tabbedPane.addTab("Misc. Settings", null, tpMiscSettings, null);
						tpMiscSettings.setLayout(null);
						
						JPanel panelSpawnSettings = new JPanel();
						panelSpawnSettings.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
						panelSpawnSettings.setBounds(18, 27, 324, 127);
						tpMiscSettings.add(panelSpawnSettings);
						panelSpawnSettings.setLayout(null);
						
						JLabel lblMinSpawnDistance = new JLabel("Min. Spawn Distance");
						lblMinSpawnDistance.setBounds(6, 16, 120, 16);
						panelSpawnSettings.add(lblMinSpawnDistance);
						
						JLabel lblMaxSpawnDistance = new JLabel("Max. Spawn Distance");
						lblMaxSpawnDistance.setBounds(6, 46, 120, 16);
						panelSpawnSettings.add(lblMaxSpawnDistance);
						
						tbMinSpawnDistance = new JFormattedTextField();
						tbMinSpawnDistance.setBounds(129, 10, 36, 28);
						panelSpawnSettings.add(tbMinSpawnDistance);
						
						tbMaxSpawnDistance = new JFormattedTextField();
						tbMaxSpawnDistance.setBounds(129, 40, 36, 28);
						panelSpawnSettings.add(tbMaxSpawnDistance);
						
						JLabel lblSocialGroup = new JLabel("Social Group");
						lblSocialGroup.setBounds(6, 74, 79, 16);
						panelSpawnSettings.add(lblSocialGroup);
						
						tbSocialGroup = new JTextField();
						tbSocialGroup.setBounds(82, 68, 98, 28);
						panelSpawnSettings.add(tbSocialGroup);
						tbSocialGroup.setColumns(10);
						
						JLabel lblRespawnTime = new JLabel("Respawn Time");
						lblRespawnTime.setBounds(6, 102, 89, 16);
						panelSpawnSettings.add(lblRespawnTime);
						
						tbRespawnTime = new JFormattedTextField();
						tbRespawnTime.setBounds(92, 96, 51, 28);
						panelSpawnSettings.add(tbRespawnTime);
						
						chckbxStalker = new JCheckBox("Follows Enemy");
						chckbxStalker.setBounds(195, 15, 112, 18);
						panelSpawnSettings.add(chckbxStalker);
						
						JLabel lblAssistRange = new JLabel("Assist Range");
						lblAssistRange.setBounds(187, 46, 79, 16);
						panelSpawnSettings.add(lblAssistRange);
						
						tbAssistRange = new JFormattedTextField();
						tbAssistRange.setBounds(266, 40, 41, 28);
						panelSpawnSettings.add(tbAssistRange);
						
						JLabel lblSpawnSettings = new JLabel("Spawn Settings");
						lblSpawnSettings.setBounds(18, 6, 94, 16);
						tpMiscSettings.add(lblSpawnSettings);
						lblSpawnSettings.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
						
						JLabel lblFactionSettings = new JLabel("Faction Settings");
						lblFactionSettings.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
						lblFactionSettings.setBounds(18, 161, 94, 16);
						tpMiscSettings.add(lblFactionSettings);
						
						JPanel panelFactionSettings = new JPanel();
						panelFactionSettings.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
						panelFactionSettings.setBounds(18, 178, 177, 75);
						tpMiscSettings.add(panelFactionSettings);
						panelFactionSettings.setLayout(null);
						
						JLabel lblFaction = new JLabel("Faction");
						lblFaction.setBounds(6, 12, 40, 16);
						panelFactionSettings.add(lblFaction);
						
						cmbFaction = new JComboBox();
						cmbFaction.setBounds(53, 7, 115, 26);
						cmbFaction.setModel(new DefaultComboBoxModel(new String[] {"Imperial", "Rebel", "Neutral"}));
						panelFactionSettings.add(cmbFaction);
						
						JLabel lblFactionStatus = new JLabel("Status");
						lblFactionStatus.setBounds(6, 43, 56, 16);
						panelFactionSettings.add(lblFactionStatus);
						
						cmbFactionStatus = new JComboBox();
						cmbFactionStatus.setBounds(53, 40, 115, 26);
						cmbFactionStatus.setModel(new DefaultComboBoxModel(new String[] {"On Leave", "Combatant", "Special Forces"}));
						panelFactionSettings.add(cmbFactionStatus);
						
						JLabel lblOptions = new JLabel("Options");
						lblOptions.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 12));
						lblOptions.setBounds(218, 161, 56, 16);
						tpMiscSettings.add(lblOptions);
						
						JPanel panel = new JPanel();
						panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
						panel.setBounds(218, 178, 124, 117);
						tpMiscSettings.add(panel);
						
						chckbxHarvestable = new JCheckBox("Harvestable");
						panel.add(chckbxHarvestable);
						
						chckbxAgressive = new JCheckBox("Agressive");
						chckbxAgressive.setEnabled(false);
						panel.add(chckbxAgressive);
						
						chckbxAttackable = new JCheckBox("Attackable");
						chckbxAttackable.setEnabled(false);
						chckbxAttackable.setSelected(true);
						panel.add(chckbxAttackable);
						
						chckbxInvulnerable = new JCheckBox("Invulnerable");
						chckbxInvulnerable.setEnabled(false);
						panel.add(chckbxInvulnerable);
						
						tpResourceSettings = new JPanel();
						tabbedPane.addTab("Resource Settings", null, tpResourceSettings, null);
						tabbedPane.setEnabledAt(4, false);
				btnAddNewWeapTemp.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						weaponTempDialog.getTbWeaponTemp().setText("");
						weaponTempDialog.getCmbWeaponType().setSelectedIndex(0);
						weaponTempDialog.getTbAttackSpeed().setText("");
						
						weaponTempDialog.setVisible(true);
					}
				});

		prgMobilesLoading = new JProgressBar();
		prgMobilesLoading.setBounds(12, 410, 200, 26);
		frmPswgToolsMbs.getContentPane().add(prgMobilesLoading);
	}
	private void populateMobileObject(Mobile baseMobile, File script) {
		try(BufferedReader br = new BufferedReader(new FileReader(script))) {
			for(String line; (line = br.readLine()) != null; ) {
				line = line.replaceAll("\\s", "");
				if (line.isEmpty() || line.equals("") || line.equals("\treturn") || line.startsWith("from") || line.startsWith("import") || line.startsWith("def"))
					continue;
				else if (line.startsWith("mobileTemplate.setCreatureName")) { baseMobile.setCreatureName(line.replace("mobileTemplate.setCreatureName('", "").replace("')", "")); }
				else if (line.startsWith("mobileTemplate.setLevel")) { baseMobile.setLevel(Integer.valueOf(line.replace("mobileTemplate.setLevel(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setMinLevel")) { baseMobile.setMinLevel(Integer.valueOf(line.replace("mobileTemplate.setMinLevel(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setMaxLevel")) { baseMobile.setMinLevel(Integer.valueOf(line.replace("mobileTemplate.setMaxLevel(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setDifficulty")) { baseMobile.setDifficulty(Integer.valueOf(line.replace("mobileTemplate.setDifficulty(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setAttackRange")) { baseMobile.setAttackRange(Integer.valueOf(line.replace("mobileTemplate.setAttackRange(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setAttackSpeed")) { baseMobile.setAttackSpeed(Float.valueOf(line.replace("mobileTemplate.setAttackSpeed(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setWeaponType")) { baseMobile.setWeaponType(Integer.valueOf(line.replace("mobileTemplate.setWeaponType(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setMinSpawnDistance")) { baseMobile.setMinSpawnDistance(Integer.valueOf(line.replace("mobileTemplate.setMinSpawnDistance(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setMaxSpawnDistance")) { baseMobile.setMaxSpawnDistance(Integer.valueOf(line.replace("mobileTemplate.setMaxSpawnDistance(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setDeathblow")) { 
					switch(line.replace("mobileTemplate.setDeathblow(", "").replace(")", "")) {
						case "True":
							baseMobile.setDeathblowEnabled(true);
							break;
						case "False":
							baseMobile.setDeathblowEnabled(false);
							break;
					}
				}
				//mobileTemplate.setScale
				//else if (line.startsWith("mobileTemplate.setMeatType(\"")) { baseMobile.setMeatType(line.replace("mobileTemplate.setMeatType(\"", "").replace("\")", "")); }
				//else if (line.startsWith("mobileTemplate.setMeatAmount(")) { baseMobile.setMeatAmount(Integer.valueOf(line.replace("mobileTemplate.setMeatAmount(", "").replace(")", ""))); }
				//else if (line.startsWith("mobileTemplate.setHideType(\"")) { baseMobile.setHideType(line.replace("mobileTemplate.setHideType(\"", "").replace("\")", "")); }
				//else if (line.startsWith("mobileTemplate.setHideAmount(")) { baseMobile.setHideAmount(Integer.valueOf(line.replace("mobileTemplate.setHideAmount(", "").replace(")", ""))); }
				//else if (line.startsWith("mobileTemplate.setBoneType(\"")) { baseMobile.setBoneType(line.replace("mobileTemplate.setBoneType(\"", "").replace("\")", "")); }
				//else if (line.startsWith("mobileTemplate.setBoneAmount(")) { baseMobile.setBoneAmount(Integer.valueOf(line.replace("mobileTemplate.setBoneAmount(", "").replace(")", ""))); }
				
				else if (line.startsWith("mobileTemplate.setSocialGroup(\"")) { baseMobile.setSocialGroup(line.replace("mobileTemplate.setSocialGroup(\"", "").replace("\")", "")); }
				else if (line.startsWith("mobileTemplate.setAssistRange")) { baseMobile.setAssistRange(Integer.valueOf(line.replace("mobileTemplate.setAssistRange(", "").replace(")", ""))); }
				else if (line.startsWith("mobileTemplate.setRespawnTime")) { baseMobile.setRespawnTime(Integer.valueOf(line.replace("mobileTemplate.setRespawnTime(", "").replace(")", ""))); }

				else if (line.startsWith("mobileTemplate.setStalker")) { 
					switch(line.replace("mobileTemplate.setStalker(", "").replace(")", "")) {
						case "True":
							baseMobile.setStalker(true);
							break;
						case "False":
							baseMobile.setStalker(false);
							break;
					}
				}
				else if (line.startsWith("templates.add")) { baseMobile.addCreatureTemplate(line.replace("templates.add('", "").replace("')", "")); }
				else if (line.startsWith("weapontemplate=WeaponTemplate")) {
					String baseTemp = line.replace("weapontemplate=WeaponTemplate('", "").replace("'", "").replace(")", "");
					String[] values = baseTemp.split(",");
					baseMobile.getWeaponTemplates().add(new Weapon(values[0], Integer.valueOf(values[1]), Float.valueOf(values[2])));
				}
				else if (line.startsWith("attacks.add")) { baseMobile.addAttack(line.replace("attacks.add('", "")); }
				else if (line.startsWith("mobileTemplate.setDefaultAttack('")) { baseMobile.setDefaultAttack(line.replace("mobileTemplate.setDefaultAttack('", "").replace("')", "")); }
			}
		} catch (IOException e) {
			Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());
		}
	}

	// TODO: Progress bar updating
	public void populateMobilesTree(File mobilesDirectory) {
		if (mobilesDirectory.isFile())
			return;
		rootMobileTreeNode.removeAllChildren();

		SwingWorker<DefaultMutableTreeNode, Void> task = new SwingWorker<DefaultMutableTreeNode, Void>() {

			@Override
			public DefaultMutableTreeNode doInBackground() throws Exception {
				DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
				DefaultMutableTreeNode node = null;
				rootNode.setUserObject("Mobile Scripts");

				frmPswgToolsMbs.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				int fileCount = fileCount(mobilesDirectory, 0);
				System.out.println("File count: " + fileCount);
				int currentFileNumber = 0;
				for (File file : mobilesDirectory.listFiles()) {

					if (file.isFile()) {
						Mobile mobile = new Mobile(file.getName().split(".py")[0], file.getPath());
						node = new DefaultMutableTreeNode(mobile);
						rootNode.add(node);
						setProgress(currentFileNumber++);
					} else if (file.isDirectory()) {
						String folderName = file.getName();
						if (folderName.equals("spawnareas") || folderName.equals("lairs") || folderName.equals("lairgroups") || folderName.equals("dynamicgroups"))
							continue;
						node = new DefaultMutableTreeNode(file.getName());
						rootNode.add(node);

						populateSubFolders(node, file);
					}
				}
				return rootNode;
			}
			@Override
			public void done() {
				frmPswgToolsMbs.setCursor(null);

				try {
					mobilesTree.setModel(new DefaultTreeModel(get()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				mobilesTree.updateUI();

				mobilesLoaded = true;
			}
			private void populateSubFolders(DefaultMutableTreeNode parentNode, File directory) {

				DefaultMutableTreeNode node = null;

				for (File file : directory.listFiles()) {

					if (file.isFile()) {
						if (!file.getAbsolutePath().endsWith(".py"))
							continue;

						Mobile mobile = new Mobile(file.getName().replace(".py", ""), file.getPath());
						//System.out.println("Path for mobile is " + mobile.getScriptLocation());
						populateMobileObject(mobile, file);

						node = new DefaultMutableTreeNode(mobile);

						parentNode.add(node);
					} else if (file.isDirectory()) {
						node = new DefaultMutableTreeNode(file.getName());
						parentNode.add(node);

						populateSubFolders(node, file);
					}
				}
			}
		};
		task.execute();
	}

	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	private void populateScriptCreator(Mobile mobileTemplate) {
		creatureTemps.clear();
		
		tbCreatureName.setText(mobileTemplate.getCreatureName());
		tbScriptLocation.setText(mobileTemplate.getScriptLocation());
		tbCreatureLevel.setValue(mobileTemplate.getLevel());
		tbMinLevel.setValue(mobileTemplate.getMinLevel());
		tbMaxLevel.setValue(mobileTemplate.getMaxLevel());
		tbMinSpawnDistance.setValue(mobileTemplate.getMinSpawnDistance());
		tbMaxSpawnDistance.setValue(mobileTemplate.getMaxSpawnDistance());
		tbAttackRange.setValue(mobileTemplate.getAttackRange());
		tbAttackSpeed.setValue(mobileTemplate.getAttackSpeed());
		tbAssistRange.setValue(mobileTemplate.getAssistRange());
		tbSocialGroup.setText(mobileTemplate.getSocialGroup());
		tbRespawnTime.setValue(mobileTemplate.getRespawnTime());
		cmbDefaultAttack.setSelectedItem(mobileTemplate.getDefaultAttack());
		cmbDifficulty.setSelectedIndex(mobileTemplate.getDifficulty());
		cmbWeaponType.setSelectedIndex(mobileTemplate.getWeaponType());
		cmbFaction.setSelectedItem(mobileTemplate.getFaction());
		cmbFactionStatus.setSelectedIndex(mobileTemplate.getFactionStatus());

		chckbxDeathblowEnabled.setSelected(mobileTemplate.isDeathblowEnabled());
		chckbxStalker.setSelected(mobileTemplate.isStalker());

		if (mobileTemplate.getCreatureTemplates().size() != 0) {
			for (String s : mobileTemplate.getCreatureTemplates()) { creatureTemps.addElement(s); }
			listCreatureTemps.setModel(creatureTemps);
		}
		else {
			listCreatureTemps.setModel(new AbstractListModel() {
				String[] values = new String[] {"No Creature Templates found."};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
		}
		
		updateWeaponTemplatesList(mobileTemplate);

		if (mobileTemplate.getAttacks().size() != 0)
			listAttacks.setListData(mobileTemplate.getAttacks());
		else
			listAttacks.setListData(new String[] { "Template has no attacks." });

		btnSave.setEnabled(true);
	}
	
	private void buildMobileScript(Mobile mobile) {
		try {
			
			tryBackup(new File(mobile.getScriptLocation()), 1);
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(mobile.getScriptLocation()));
			writer.write("import sys\n");
			writer.write("from services.spawn import MobileTemplate\n");
			if (mobile.getWeaponTemplates().size() > 0) 
				writer.write("from services.spawn import WeaponTemplate\n");
			writer.write("from java.util import Vector\n");
			writer.newLine();
			
			writer.write("def addTemplate(core):\n");
			writer.write("\tmobileTemplate = MobileTemplate()\n");
			writer.newLine();
			
			writer.write("\tmobileTemplate.setCreatureName('" + mobile.getCreatureName() + "')\n");
			writer.write("\tmobileTemplate.setLevel(" + String.valueOf(mobile.getLevel()) + ")\n");
			writer.write("\tmobileTemplate.setDifficulty(" + String.valueOf(mobile.getDifficulty()) + ")\n");
			writer.write("\tmobileTemplate.setAttackRange(" + String.valueOf(mobile.getAttackRange()) + ")\n");
			writer.write("\tmobileTemplate.setAttackSpeed(" + String.valueOf(mobile.getAttackSpeed()) + ")\n");
			writer.write("\tmobileTemplate.setWeaponType(" + String.valueOf(mobile.getWeaponType()) + ")\n");
			writer.write("\tmobileTemplate.setMinSpawnDistance(" + String.valueOf(mobile.getMinSpawnDistance()) + ")\n");
			writer.write("\tmobileTemplate.setMaxSpawnDistance(" + String.valueOf(mobile.getMaxSpawnDistance()) + ")\n");
			writer.write("\tmobileTemplate.setDeathblow(" + (mobile.isDeathblowEnabled() ? "True" : "False") + ")\n");
			//writer.write("\tmobileTemplate.setScale(" + 1 + ")\n");
			if (mobile.getSocialGroup() != null && !mobile.getSocialGroup().isEmpty())
				writer.write("\tmobileTemplate.setSocialGroup('" + mobile.getSocialGroup() + "')\n");
			writer.write("\tmobileTemplate.setAssistRange(" + String.valueOf(mobile.getAssistRange()) + ")\n");
			writer.write("\tmobileTemplate.setStalker(" + (mobile.isStalker() ? "True" : "False") + ")\n");
			
			if (mobile.getFaction() != null && !mobile.getFaction().equals("") && !mobile.getFaction().equalsIgnoreCase("neutral")) {
				writer.write("\tmobileTemplate.setFaction('" + mobile.getFaction().toLowerCase() + "')\n");
				writer.write("\tmobileTemplate.setFactionStatus(" + String.valueOf(mobile.getFactionStatus()) + ")\n");
			}
			writer.newLine();
			
			writer.write("\ttemplates = new Vector()\n");
			if (mobile.getCreatureTemplates().size() > 0) {
				for (String temp : mobile.getCreatureTemplates()) {
					writer.write("\ttemplates.add('" + temp + "')\n");
				}
			}
			writer.write("\tmobileTemplate.setTemplates(templates)\n");
			writer.newLine();
			
			writer.write("\tweaponTemplates = Vector()\n");
			if (mobile.getWeaponTemplates().size() > 0) {
				for (Weapon weapon : mobile.getWeaponTemplates()) {
					writer.write("\tweapontemplate = WeaponTemplate('" + weapon.getTemplate() + "', " + weapon.getWeaponType() + ", " 
							+ weapon.getAttackSpeed() +")\n");
					writer.write("\tweaponTemplates.add(weapontemplate)\n");
				}
			}
			writer.write("\tmobileTemplate.setWeaponTemplateVector(weaponTemplates)\n");
			writer.newLine();
			
			writer.write("\tattacks = new Vector()\n");
			if (mobile.getAttacks().size() > 0) {
				for (String attack : mobile.getAttacks()) {
					writer.write("\tattacks.add('" + attack + "')\n");
				}
			}
			writer.write("\tmobileTemplate.setDefaultAttack('" + mobile.getDefaultAttack() + "')\n");
			writer.write("\tmobileTemplate.setAttacks(attacks)");
			writer.newLine();
			
			writer.write("\tcore.spawnService.addMobileTemplate('" + mobile.getTemplateName() + "', mobileTemplate)\n\treturn");
			
			writer.close();
			
			mobile.setDirty(false);
			mobilesTree.updateUI();
		} catch (Exception e) {
			Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());
			e.printStackTrace();
		}
	}
	
	private void saveCurrentValues() {
		try {
		Mobile mobileTemplate = (Mobile) ((DefaultMutableTreeNode) mobilesTree.getSelectionPath().getLastPathComponent()).getUserObject();
		
		mobileTemplate.setCreatureName(tbCreatureName.getText());
		mobileTemplate.setScriptLocation(tbScriptLocation.getText());
		
		mobileTemplate.setLevel((int) tbCreatureLevel.getValue());
		mobileTemplate.setMinLevel(Integer.valueOf(tbMinLevel.getText()));
		mobileTemplate.setMaxLevel(Integer.valueOf(tbMaxLevel.getText()));
		mobileTemplate.setAttackRange(Integer.valueOf(tbAttackRange.getText()));
		mobileTemplate.setAttackSpeed(Float.valueOf(tbAttackSpeed.getText()));
		mobileTemplate.setAssistRange(Integer.valueOf(tbAssistRange.getText()));

		mobileTemplate.setDifficulty(cmbDifficulty.getSelectedIndex());
		mobileTemplate.setDefaultAttack((String) cmbDefaultAttack.getSelectedItem()); 
		mobileTemplate.setWeaponType(cmbWeaponType.getSelectedIndex());
		
		mobileTemplate.setSocialGroup(tbSocialGroup.getText());
		mobileTemplate.setFaction((String) cmbFaction.getSelectedItem());
		mobileTemplate.setFactionStatus(cmbFactionStatus.getSelectedIndex());
		mobileTemplate.setMinSpawnDistance(Integer.valueOf(tbMinSpawnDistance.getText()));
		mobileTemplate.setMaxSpawnDistance(Integer.valueOf(tbMaxSpawnDistance.getText()));
		
		mobileTemplate.setDeathblowEnabled(chckbxDeathblowEnabled.isSelected());
		mobileTemplate.setStalker(chckbxStalker.isSelected());
		mobileTemplate.setHarvestable(chckbxHarvestable.isSelected());
		
		if (creatureTemps.getSize() > 0) {
			Object[] objArray = creatureTemps.toArray();
			String[] stringArray = Arrays.copyOf(objArray, objArray.length, String[].class);
			Vector<String> creatureTemplates = new Vector<String>(Arrays.asList(stringArray));
			mobileTemplate.setCreatureTemplates(creatureTemplates);
		} else {
			mobileTemplate.setCreatureTemplates(new Vector<String>());
		}

		if (weaponTemps.size() > 0) {
			Object[] objArray = weaponTemps.toArray();
			String[] stringArray = Arrays.copyOf(objArray, objArray.length, String[].class);
			Vector<Weapon> updatedWeaponTemps = new Vector<Weapon>();
			
			for (String temp : stringArray) {
				String[] values = temp.replace(" ", "").split(",");
				updatedWeaponTemps.add(new Weapon(values[0], Integer.valueOf(values[1]), Float.valueOf(values[2])));
			}
			
			mobileTemplate.setWeaponTemplates(updatedWeaponTemps);
		} else {
			mobileTemplate.setWeaponTemplates(new Vector<Weapon>());
		}

		/*
		if (mobileTemplate.getAttacks().size() != 0)
			listAttacks.setListData(mobileTemplate.getAttacks());
		else
			listAttacks.setListData(new String[] { "Template has no attacks." });*/
		
		mobileTemplate.setDirty(true);
		
		if (!btnBuildCurrent.isEnabled())
			btnBuildCurrent.setEnabled(true);
		
		modifiedTemplates.add(mobileTemplate);
		
		if (modifiedTemplates.size() >= 2 && !btnBuildAll.isEnabled())
			btnBuildAll.setEnabled(true);
		
		mobilesTree.updateUI(); // Show Asterisk
		System.out.println("Saved values for Template: " + mobileTemplate.getCreatureName());
		} catch (Exception e) { Helpers.showExceptionError(frmPswgToolsMbs, e.getLocalizedMessage());}
		
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
	public void updateWeaponTemplatesList(Mobile mobileTemplate) {
		weaponTemps.clear();
		
		if (mobileTemplate.getWeaponTemplates().size() != 0) {

			for (Weapon weapon : mobileTemplate.getWeaponTemplates())
				weaponTemps.addElement(weapon.toString());
			
			listWeaponTemps.setModel(weaponTemps);
		}else {
			listWeaponTemps.setModel(new AbstractListModel() {
				String[] values = new String[] {"No Weapon Templates found."};
				public int getSize() {
					return values.length;
				}
				public Object getElementAt(int index) {
					return values[index];
				}
			});
		}
	}
	
	private void tryBackup(File file, int count) throws IOException {
		File backup = new File(file.getAbsolutePath() + ".bak." + String.valueOf(count));
		if (!backup.exists())
			Files.copy(file.toPath(), backup.toPath());
		else
			tryBackup(backup, count++);
	}
	public void setActiveMobile(Mobile activeMobile) {
		this.activeMobile = activeMobile;
	}

	public void setConfig(Properties config) {
		this.config = config;
	}

	public void setCoreLocation(String coreLocation) {
		this.coreLocation = coreLocation;
	}
	
	public static MobileScriptBuilder getInstance() {
		return instance;
	}

	@SuppressWarnings("unchecked")
	public DefaultListModel<String> getWeaponTemps() {
		return weaponTemps;
	}

	@SuppressWarnings("unchecked")
	public DefaultListModel<String> getCreatureTemps() {
		return creatureTemps;
	}
	
	public void setWeaponTemps(DefaultListModel<String> weaponTemps) {
		this.weaponTemps = weaponTemps;
	}

	public void setCreatureTemps(DefaultListModel<String> creatureTemps) {
		this.creatureTemps = creatureTemps;
	}

	public JList<String> getListWeaponTemps() {
		return listWeaponTemps;
	}

	public void setListWeaponTemps(JList<String> listWeaponTemps) {
		this.listWeaponTemps = listWeaponTemps;
	}
	
	public JList<String> getListCreatureTemps() {
		return listCreatureTemps;
	}

	public DefaultListModel getAttacks() {
		return attacks;
	}

	public void setAttacks(DefaultListModel attacks) {
		this.attacks = attacks;
	}

	public JTree getMobilesTree() {
		return mobilesTree;
	}
	private static void addPopup(Component component, final JPopupMenu popup) {
		component.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}
			private void showMenu(MouseEvent e) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			}
		});
	}
}
