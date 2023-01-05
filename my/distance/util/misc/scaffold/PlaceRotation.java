package my.distance.util.misc.scaffold;

import my.distance.util.math.Rotation;
import my.distance.util.misc.scaffold.blocks.PlaceInfo;

public final class PlaceRotation {

	private final PlaceInfo placeInfo;

	private final Rotation rotation;

	public PlaceInfo getPlaceInfo() {
		return this.placeInfo;
	}

	public Rotation getRotation() {
		return this.rotation;
	}

	public PlaceRotation(PlaceInfo placeInfo, Rotation rotation) {
		this.placeInfo = placeInfo;
		this.rotation = rotation;
	}
	
}
