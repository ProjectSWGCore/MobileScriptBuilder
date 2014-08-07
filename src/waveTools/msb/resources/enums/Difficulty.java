package waveTools.msb.resources.enums;

public enum Difficulty {
	NORMAL(0), ELITE(1), BOSS(2);
	
	@SuppressWarnings("unused")
	private int difficulty;
	
	private Difficulty(int difficulty) {
		this.difficulty = difficulty;
	}
	
	@Override
	public String toString() {
		switch(this) {
		case NORMAL:
			return "Normal";
		case ELITE:
			return "Elite";
		case BOSS:
			return "Boss";
		}
		return null;
	}
}
