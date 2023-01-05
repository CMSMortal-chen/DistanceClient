package my.distance.api.value;

public class Option extends Value<Boolean> {
	public float anim = 0.1f;
	public Option(String displayName, String name, boolean enabled) {
		super(displayName, name);
		this.setValue(enabled);
	}
	public Option(String displayName, boolean enabled) {
		super(displayName, displayName);
		this.setValue(enabled);
	}
}
