/*
 * Decompiled with CFR 0_132.
 */
package my.distance.api.events.Render;

import my.distance.api.Event;
import net.optifine.shaders.Shaders;

public class EventRender3D extends Event {
	public static float ticks;
	private boolean isUsingShaders;

	public EventRender3D() {
		this.isUsingShaders = Shaders.getShaderPackName() != null;
	}

	public EventRender3D(float ticks) {
		this.ticks = ticks;
		this.isUsingShaders = Shaders.getShaderPackName() != null;
	}

	public float getPartialTicks() {
		return this.ticks;
	}
	public void setPartialTicks(float ticks) {
		this.ticks = ticks;
	}
	public boolean isUsingShaders() {
		return this.isUsingShaders;
	}
}
