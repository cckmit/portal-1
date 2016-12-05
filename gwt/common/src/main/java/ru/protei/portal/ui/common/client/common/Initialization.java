package ru.protei.portal.ui.common.client.common;

/**
 * Created by bondarenko on 05.12.16.
 */
public interface Initialization {
    void completeTask(PreparatoryTask task);

    enum PreparatoryTask{
        ISSUE_STATES_LOADING;

        private static int count = values().length;
        private boolean isCompleted;
        public void complete(){
            if(!isCompleted) {
                count--;
                isCompleted = true;
            }
        }
        public static boolean isCompleted(){
            return count == 0;
        }
    }
}
