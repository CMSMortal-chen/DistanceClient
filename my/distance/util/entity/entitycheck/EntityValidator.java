/*
 * Decompiled with CFR 0.150.
 */
package my.distance.util.entity.entitycheck;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.Entity;

public final class EntityValidator {
    private final Set<ICheck> checks = new HashSet<>();

    public boolean validate(Entity entity) {
        for (ICheck check : this.checks) {
            if (check.validate(entity)) continue;
            return false;
        }
        return true;
    }

    public void add(ICheck check) {
        this.checks.add(check);
    }
}

