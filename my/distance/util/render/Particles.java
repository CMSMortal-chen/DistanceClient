package my.distance.util.render;

import my.distance.util.misc.Location;

public class Particles {
	public int ticks;
	public Location location;
	public String text;

	public Particles(final Location location, final String text) {
		this.location = location;
		this.text = text;
		this.ticks = 0;
	}
}
