package my.distance.api.value;

public abstract class Value<V> {
	private String displayName;
	private String name;
	public V value;
	public Mode modes;
	public Enum<?>[] targetModes;

	public Value(String displayName, String name) {
		this.displayName = displayName;
		this.name = name;
	}


	public String getDisplayName() {
		return this.displayName;
	}

	public String getName() {
		return this.name;
	}

	public V getValue() {
		return this.value;
	}

	public V get() {
		return this.value;
	}

	public boolean isDisplayable() {
		if (targetModes != null){
			for (Enum<?> targetMode : targetModes) {
				if (targetMode.equals(modes.getValue())) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
