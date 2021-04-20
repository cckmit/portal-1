package ru.protei.portal.ui.sitefolder.client.events;

import ru.protei.portal.core.model.ent.ServerGroup;

import java.util.function.Consumer;

public class ServerGroupEvents {
    public static class Edit {
        public Edit(ServerGroup serverGroup, Consumer<ServerGroup> onSave) {
            this(serverGroup, onSave, null);
        }

        public Edit(ServerGroup serverGroup, Consumer<ServerGroup> onSave, Consumer<Long> onRemove) {
            this.serverGroup = serverGroup;
            this.onSave = onSave;
            this.onRemove = onRemove;
        }

        public ServerGroup serverGroup;
        public Consumer<ServerGroup> onSave;
        public Consumer<Long> onRemove;
    }

    public static class Changed {
        public Changed(ServerGroup serverGroup) {
            this.serverGroup = serverGroup;
        }

        public ServerGroup serverGroup;
    }

    public static class Removed {
        public Removed(Long serverGroupId) {
            this.serverGroupId = serverGroupId;
        }

        public Long serverGroupId;
    }
}
