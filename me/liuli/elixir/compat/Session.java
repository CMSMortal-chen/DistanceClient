/*
 * Decompiled with CFR 0.153-SNAPSHOT (${git.commit.id.abbrev}).
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package me.liuli.elixir.compat;

public final class Session {
    private final String username;
    private final String uuid;
    private final String token;
    private final String type;

    public Session(String username, String uuid, String token, String type) {
        this.username = username;
        this.uuid = uuid;
        this.token = token;
        this.type = type;
    }

    public String getUsername() {
        return this.username;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getToken() {
        return this.token;
    }

    public String getType() {
        return this.type;
    }

    public String component1() {
        return this.username;
    }

    public String component2() {
        return this.uuid;
    }

    public String component3() {
        return this.token;
    }

    public String component4() {
        return this.type;
    }

    public Session copy(String username, String uuid, String token, String type) {
        return new Session(username, uuid, token, type);
    }

    public static /* synthetic */ Session copy$default(Session session, String string, String string2, String string3, String string4, int n, Object object) {
        if ((n & 1) != 0) {
            string = session.username;
        }
        if ((n & 2) != 0) {
            string2 = session.uuid;
        }
        if ((n & 4) != 0) {
            string3 = session.token;
        }
        if ((n & 8) != 0) {
            string4 = session.type;
        }
        return session.copy(string, string2, string3, string4);
    }

    public String toString() {
        return "Session(username=" + this.username + ", uuid=" + this.uuid + ", token=" + this.token + ", type=" + this.type + ')';
    }

    public int hashCode() {
        int result = this.username.hashCode();
        result = result * 31 + this.uuid.hashCode();
        result = result * 31 + this.token.hashCode();
        result = result * 31 + this.type.hashCode();
        return result;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Session)) {
            return false;
        }
        Session session = (Session)other;
        if (!this.username.equals(session.username)) {
            return false;
        }
        if (!this.uuid.equals(session.uuid)) {
            return false;
        }
        if (this.token.equals(session.token)) {
            return false;
        }
        return this.type.equals(session.type);
    }
}

