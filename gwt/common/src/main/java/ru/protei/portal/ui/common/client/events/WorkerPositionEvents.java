package ru.protei.portal.ui.common.client.events;

import ru.protei.portal.core.model.ent.WorkerPosition;

public class WorkerPositionEvents {

    public static class Created {
        public Created( WorkerPosition workerPosition) {
            this.workerPosition = workerPosition;
        }

        public WorkerPosition workerPosition;
    }

    public static class Changed {
        public Changed( WorkerPosition workerPosition) {
            this.workerPosition = workerPosition;
        }

        public WorkerPosition workerPosition;
    }

    public static class Removed {
        public Removed( WorkerPosition workerPosition) {
            this.workerPosition = workerPosition;
        }

        public WorkerPosition workerPosition;
    }

    public static class Edit {
        public Edit( WorkerPosition workerPosition) {
            this.workerPosition = workerPosition;
        }

        public WorkerPosition workerPosition;
    }
}
