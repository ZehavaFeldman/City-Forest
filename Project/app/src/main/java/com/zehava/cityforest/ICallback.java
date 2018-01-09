package com.zehava.cityforest;

/**
 * Created by avigail on 05/12/17.
 */

public interface ICallback {
    enum DRAGGABLE_CALSS {
        VIEW_MOVED,
        VIEW_TOUCHED
    }
    enum USER_UPDATES_CLASS {
        UPDATE_REMOVED,
        UPDATE_CREATED
    }
    void onDraggableNotify(DRAGGABLE_CALSS draggableIcall);

    void onUserUpdateNotify(USER_UPDATES_CLASS userUpdatesClass, int id);
}
