package my.distance.api;

import my.distance.api.events.World.EventPostUpdate;
import my.distance.api.events.World.EventPreUpdate;
import my.distance.api.events.World.EventTick;
import my.distance.module.Module;
import my.distance.util.misc.Helper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class EventBus {
	private final Map<Class<? extends Event>, List<Handler>> registry = new HashMap<>();
	private final Comparator<Handler> comparator = Comparator.comparingInt(h -> h.priority);
	private final MethodHandles.Lookup lookup = MethodHandles.lookup();
	private static final EventBus instance = new EventBus();

	public static EventBus getInstance() {
		return instance;
	}

	public void register(Object... objs) {
		for (Object obj : objs) {
			Method[] arrmethod = obj.getClass().getDeclaredMethods();
			for (Method m : arrmethod) {
				if (m.getParameterCount() == 1 && m.isAnnotationPresent(EventHandler.class)) {
					Class<?> eventClass = m.getParameterTypes()[0];
					if (!this.registry.containsKey(eventClass)) {
						this.registry.put((Class<? extends Event>) eventClass, new CopyOnWriteArrayList<>());
					}
					this.registry.get(eventClass)
							.add(new Handler(m, obj, m.getDeclaredAnnotation(EventHandler.class).priority()));
					this.registry.get(eventClass).sort(this.comparator);
				}
			}
		}
	}

	public void unregister(Object... objs) {
		for (Object obj : objs) {
			for (List<Handler> list : this.registry.values()) {
				for (Handler data : list) {
					if (data.parent != obj)
						continue;
					list.remove(data);
				}
			}
		}
	}

	public <E extends Event> E register(E event) {
		boolean whiteListedEvents = event instanceof EventTick || event instanceof EventPreUpdate
				|| event instanceof EventPostUpdate;
		List<Handler> list = this.registry.get(event.getClass());
		if (list != null && !list.isEmpty()) {
			for (Handler data : list) {
				try {
					if (list instanceof Module) {
						if (((Module) list).isEnabled()) {
							if (whiteListedEvents) {
								Helper.mc.mcProfiler.startSection(((Module) list).getName());
							}
							if (whiteListedEvents) {
								Helper.mc.mcProfiler.endSection();
							}
						}
					} else {
						if (whiteListedEvents) {
							Helper.mc.mcProfiler.startSection("non module");
						}
						if (whiteListedEvents) {
							Helper.mc.mcProfiler.endSection();
						}
					}
					data.handler.invokeExact(data.parent, event);
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
		}
		return event;
	}

	private class Handler {
		private MethodHandle handler;
		private Object parent;
		private byte priority;

		public Handler(Method method, Object parent, byte priority) {
			if (!method.isAccessible()) {
				method.setAccessible(true);
			}
			MethodHandle m = null;
			try {
				m = EventBus.this.lookup.unreflect(method);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			if (m != null) {
				this.handler = m
						.asType(m.type().changeParameterType(0, Object.class).changeParameterType(1, Event.class));
			}
			this.parent = parent;
			this.priority = priority;
		}
	}
}
