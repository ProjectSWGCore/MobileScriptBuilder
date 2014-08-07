package waveTools.msb.resources;

public class Weapon {
	private String template;
	private int weaponType;
	private float attackSpeed;
	
	public Weapon(String template, int weaponType, float attackSpeed) {
		this.template = template;
		this.weaponType = weaponType;
		this.attackSpeed = attackSpeed;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public int getWeaponType() {
		return weaponType;
	}

	public void setWeaponType(int weaponType) {
		this.weaponType = weaponType;
	}

	public float getAttackSpeed() {
		return attackSpeed;
	}

	public void setAttackSpeed(float attackSpeed) {
		this.attackSpeed = attackSpeed;
	}
	
	@Override
	public String toString() {
		return template + ", " + String.valueOf(weaponType) + ", " + String.valueOf(attackSpeed);
	}
}
