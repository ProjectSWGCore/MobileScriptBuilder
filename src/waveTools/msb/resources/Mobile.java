package waveTools.msb.resources;

import java.util.Vector;

public class Mobile {
	
	@SuppressWarnings("unused")
	private static final long serialVersionUID = 1L;
	
	private String scriptLocation;
	private String templateName;
	private String creatureName;
	
	private String defaultAttack;
	private String socialGroup;
	private int level;
	private int minLevel;
	private int maxLevel;
	private int difficulty;
	private int attackRange;
	private int weaponType;
	private int minSpawnDistance;
	private int maxSpawnDistance;
	private int assistRange;
	private int respawnTime;
	private boolean harvestable;
	private boolean stalker;
	private String faction;
	private int factionStatus;
	private float attackSpeed;
	private boolean deathblowEnabled;
	private String meatType, milkType, boneType, hideType;
	private int meatAmount, milkAmount, boneAmount, hideAmount;
	private Vector<String> creatureTemplates = new Vector<String>();
	private Vector<Weapon> weaponTemplates = new Vector<Weapon>();
	private Vector<String> attacks = new Vector<String>();
	private boolean dirty;
	
	public Mobile(String template, String scriptLocation) { 
		this.templateName = template;
		this.scriptLocation = scriptLocation;
	}


	public Mobile() { }


	public String getScriptLocation() {
		return scriptLocation;
	}


	public void setScriptLocation(String scriptLocation) {
		this.scriptLocation = scriptLocation;
	}


	public String getCreatureName() {
		return creatureName;
	}


	public void setCreatureName(String creatureName) {
		this.creatureName = creatureName;
	}


	public Vector<String> getCreatureTemplates() {
		return creatureTemplates;
	}


	public void setCreatureTemplates(Vector<String> creatureTemplates) {
		this.creatureTemplates = creatureTemplates;
	}


	public Vector<Weapon> getWeaponTemplates() {
		return weaponTemplates;
	}


	public Vector<String> getAttacks() {
		return attacks;
	}


	public void setWeaponTemplates(Vector<Weapon> weaponTemplates) {
		this.weaponTemplates = weaponTemplates;
	}
	
	public int getLevel() {
		return level;
	}


	public void setLevel(int level) {
		this.level = level;
	}


	public int getMinLevel() {
		return minLevel;
	}


	public void setMinLevel(int minLevel) {
		this.minLevel = minLevel;
	}


	public int getMaxLevel() {
		return maxLevel;
	}


	public void setMaxLevel(int maxLevel) {
		this.maxLevel = maxLevel;
	}


	public float getAttackSpeed() {
		return attackSpeed;
	}


	public void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}


	public int getAttackRange() {
		return attackRange;
	}


	public void setAttackRange(int attackRange) {
		this.attackRange = attackRange;
	}


	public int getWeaponType() {
		return weaponType;
	}


	public void setWeaponType(int weaponType) {
		this.weaponType = weaponType;
	}


	public int getDifficulty() {
		return difficulty;
	}


	public void setDifficulty(int difficulty) {
		this.difficulty = difficulty;
	}


	public String getDefaultAttack() {
		return defaultAttack;
	}


	public void setDefaultAttack(String defaultAttack) {
		this.defaultAttack = defaultAttack;
	}


	public boolean isDirty() {
		return dirty;
	}


	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}
	
	public boolean isDeathblowEnabled() {
		return deathblowEnabled;
	}


	public void setDeathblowEnabled(boolean deathblowEnabled) {
		this.deathblowEnabled = deathblowEnabled;
	}


	public String getSocialGroup() {
		return socialGroup;
	}


	public void setSocialGroup(String socialGroup) {
		this.socialGroup = socialGroup;
	}


	public int getMinSpawnDistance() {
		return minSpawnDistance;
	}


	public void setMinSpawnDistance(int minSpawnDistance) {
		this.minSpawnDistance = minSpawnDistance;
	}


	public int getMaxSpawnDistance() {
		return maxSpawnDistance;
	}


	public void setMaxSpawnDistance(int maxSpawnDistance) {
		this.maxSpawnDistance = maxSpawnDistance;
	}


	public int getAssistRange() {
		return assistRange;
	}


	public void setAssistRange(int assistRange) {
		this.assistRange = assistRange;
	}


	public int getRespawnTime() {
		return respawnTime;
	}


	public void setRespawnTime(int respawnTime) {
		this.respawnTime = respawnTime;
	}


	public boolean isHarvestable() {
		return harvestable;
	}


	public void setHarvestable(boolean harvestable) {
		this.harvestable = harvestable;
	}


	public String getFaction() {
		return faction;
	}


	public void setFaction(String faction) {
		this.faction = faction;
	}


	public int getFactionStatus() {
		return factionStatus;
	}


	public void setFactionStatus(int factionStatus) {
		this.factionStatus = factionStatus;
	}


	public String getMeatType() {
		return meatType;
	}


	public void setMeatType(String meatType) {
		this.meatType = meatType;
	}


	public String getMilkType() {
		return milkType;
	}


	public void setMilkType(String milkType) {
		this.milkType = milkType;
	}


	public String getBoneType() {
		return boneType;
	}


	public void setBoneType(String boneType) {
		this.boneType = boneType;
	}


	public String getHideType() {
		return hideType;
	}


	public void setHideType(String hideType) {
		this.hideType = hideType;
	}


	public int getMeatAmount() {
		return meatAmount;
	}


	public void setMeatAmount(int meatAmount) {
		this.meatAmount = meatAmount;
	}


	public int getMilkAmount() {
		return milkAmount;
	}


	public void setMilkAmount(int milkAmount) {
		this.milkAmount = milkAmount;
	}


	public int getBoneAmount() {
		return boneAmount;
	}


	public void setBoneAmount(int boneAmount) {
		this.boneAmount = boneAmount;
	}


	public int getHideAmount() {
		return hideAmount;
	}


	public void setHideAmount(int hideAmount) {
		this.hideAmount = hideAmount;
	}


	public void setAttacks(Vector<String> attacks) {
		this.attacks = attacks;
	}


	public void addCreatureTemplate(String template) {
		creatureTemplates.add(template);
	}
	
	public void addAttack(String attack) {
		attacks.add(attack);
	}
	public boolean isStalker() {
		return stalker;
	}


	public void setStalker(boolean stalker) {
		this.stalker = stalker;
	}


	@Override
	public String toString() {
		if (dirty)
			return  "*" + templateName;
		else return templateName;
	}


	public String getTemplateName() {
		return templateName;
	}


	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
}
