package my.distance.api.value;

public class Mode extends Value<Enum<?>> {
	private final Enum<?>[] modes;

	public Mode(String displayName, String name, Enum<?>[] modes, Enum<?> value) {
		super(displayName, name);
		this.modes = modes;
		this.setValue(value);
	}
	public Mode(String displayName, Enum<?>[] modes, Enum<?> value) {
		super(displayName, displayName);
		this.modes = modes;
		this.setValue(value);
	}
	public Enum<?>[] getModes() {
		return this.modes;
	}

	public String getModeAsString() {
		return this.getValue().name();
	}

	public void setMode(String mode) {
		for (Enum<?> val: this.modes){
			if (val.name().equalsIgnoreCase(mode)) {
				this.setValue(val);
				break;
			}
		}
	}

	public boolean isValid(String name) {
		for (Enum<?> val : this.modes) {
			if (val.name().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
}
