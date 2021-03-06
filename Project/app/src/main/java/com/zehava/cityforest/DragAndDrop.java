package com.zehava.cityforest;


import android.content.Context;
import android.graphics.Canvas;
import android.telecom.RemoteConnection;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.maps.Projection;
import com.mapbox.services.commons.models.Position;

/**
 * Created by avigail on 05/12/17.
 */

/*calss that extends ImageView adding on dragging options
we use this as a workaround for draging markers on map*/
public class DragAndDrop extends ImageView implements View.OnTouchListener {


    float dX;
    float dY;

    //communicate with activity via callbacks
    private ICallback iCallback;


    public DragAndDrop(Context context)
    {
        super(context);

    }

    public DragAndDrop(Context context, ICallback callback)
    {
        super(context);
        iCallback = callback;

        setOnTouchListener(this);

    }


    public DragAndDrop(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public DragAndDrop(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }


    public boolean onTouch(View view, MotionEvent event) {

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                //notifay activity on draging start
                if(iCallback != null)
                    iCallback.onDraggableNotify(ICallback.DRAGGABLE_CALSS.VIEW_TOUCHED);

                dX = view.getX() - event.getRawX();
                dY = view.getY() - event.getRawY();
                break;

            case MotionEvent.ACTION_MOVE:

                view.setY(event.getRawY() + dY);
                view.setX(event.getRawX() + dX);
                break;

            case MotionEvent.ACTION_UP:
                //notifay activity on dragging stop
                if(iCallback != null)
                    iCallback.onDraggableNotify(ICallback.DRAGGABLE_CALSS.VIEW_MOVED);
                break;

            default:
                return false;
        }
        return true;
    }

}
