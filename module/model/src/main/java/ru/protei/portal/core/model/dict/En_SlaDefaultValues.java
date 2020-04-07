package ru.protei.portal.core.model.dict;

public enum En_SlaDefaultValues {
    CRITICAL {
        @Override
        public Long getReactionTime() {
            return hoursToMinutes(1);
        }

        @Override
        public Long getTemporarySolutionTime() {
            return hoursToMinutes(4);
        }

        @Override
        public Long getFullSolutionTime() {
            return daysToMinutes(3);
        }
    },

    IMPORTANT {
        @Override
        public Long getReactionTime() {
            return hoursToMinutes(2);
        }

        @Override
        public Long getTemporarySolutionTime() {
            return daysToMinutes(1);
        }

        @Override
        public Long getFullSolutionTime() {
            return daysToMinutes(3);
        }
    },

    BASIC {
        @Override
        public Long getReactionTime() {
            return daysToMinutes(1);
        }

        @Override
        public Long getTemporarySolutionTime() {
            return daysToMinutes(3);
        }

        @Override
        public Long getFullSolutionTime() {
            return daysToMinutes(30);
        }
    },

    COSMETIC {
        @Override
        public Long getReactionTime() {
            return daysToMinutes(1);
        }

        @Override
        public Long getTemporarySolutionTime() {
            return weeksToMinutes(2);
        }

        @Override
        public Long getFullSolutionTime() {
            return daysToMinutes(90);
        }
    };

    public abstract Long getReactionTime();
    public abstract Long getTemporarySolutionTime();
    public abstract Long getFullSolutionTime();

    Long hoursToMinutes(int hours) {
        return hours * 60L;
    }

    Long daysToMinutes(int days) {
        return hoursToMinutes(days * 24);
    }

    Long weeksToMinutes(int weeks) {
        return daysToMinutes(weeks * 7);
    }
}
