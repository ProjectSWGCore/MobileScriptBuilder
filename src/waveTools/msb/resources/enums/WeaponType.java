package waveTools.msb.resources.enums;

public enum WeaponType {
	RIFLE(0), CARBINE(1), PISTOL(2), ONEHANDEDMELEE(4), TWOHANDEDMELEE(5), UNARMED(6), POLEARMMELEE(7), THROWN(8), 
	ONEHANDEDSABER(9), TWOHANDEDSABER(10), POLEARMSABER(11), HEAVYWEAPON(12), FLAMETHROWER(13);
	
	private int weaponType;
	
	private WeaponType(int weaponType) {
		this.weaponType = weaponType;
	}
	
	@Override
	public String toString() {
		switch(this) {
		case RIFLE:
			return "Rifle";
		case CARBINE:
			return "Carbine";
		case PISTOL:
			return "Pistol";
		case ONEHANDEDMELEE:
			return "1H - Melee";
		case TWOHANDEDMELEE:
			return "2H - Melee";
		case UNARMED:
			return "Unarmed";
		case POLEARMMELEE:
			return "Polearm - Melee";
		case THROWN:
			return "Thrown";
		case ONEHANDEDSABER:
			return "1H - Saber";
		case TWOHANDEDSABER:
			return "2H - Saber";
		case POLEARMSABER:
			return "Polearm - Saber";
		case HEAVYWEAPON:
			return "Heavy Weapon";
		case FLAMETHROWER:
			return "Flame Thrower";
		}
		return null;
	}
}
