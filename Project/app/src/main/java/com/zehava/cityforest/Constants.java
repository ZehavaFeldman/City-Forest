package com.zehava.cityforest;

import com.mapbox.services.commons.models.Position;

public final class Constants {

    public static final Position DEFAULT_JERUSALEM_COORDINATE
            = Position.fromCoordinates(35.207089, 31.771559);

    public static final String CHOSEN_COORDINATE = "Chosen Coordinate";
    public static final String NEW_COORDINATE_ID = "Coordinate ID";
    public static final String IS_COORDINATE_MAP_CREATED = "Is coordinate map created";
    public static final String COORDINATE_KEY = "Coordinate key";
    public static final String CHOSEN_TRACK = "Chosen Track";
    public static final String TRACK_EDIT = "Track Edit";
    public static final String SELECTED_TRACK = "Selected Track";
    public static final String CREATED_COORDINATE_FOR_ZOOM = "Created coordinate for zoom";
    public static final String EDITED_COORDINATE_FOR_ZOOM = "Edited coordinate for zoom";
    public static final String CREATED_UPDATE_FOR_ZOOM = "Created update for zoom";
    public static final String TRACK_STARTING_POINT = "Track Starting point";
    public static final String TRACK_ENDING_POINT = "Track Ending point";
    public static final String TRACK_STARTING_POINT_NAME = "Track Starting name";
    public static final String TRACK_ENDING_POINT_NAME = "Track Ending name";
    public static final String TRACK_WAYPOINTS = "Trak waypoints";

    public static final String Q_STARTING_POINT = "Starting point";
    public static final String Q_ENDING_POINT = "Ending point";


    public static final String LANGUAGE_TO_LOAD = "languageToLoad";
    public static final String LANGUAGE = "language";
    public static final String HEBREW = "iw";
    public static final String ENGLISH = "en";
    public static final String SET_FROM_PREFS = "set from prefs";


    public static final int NEW_COORDINATE = 100;
    public static final int COORDINATE_CREATED = 101;
    public static final int EDIT_COORDINATE = 102;
    public static final int COORDINATE_EDITED = 103;
    public static final int MAX_NUM_OF_TRACK_COORDINATES = 25;
    public static final int NEW_TRACK = 200;
    public static final int TRACK_CREATED = 201;
    public static final int TRACK_EDITED = 202;
    public static final int NEW_USER_UPDATE = 300;
    public static final int USER_UPDATE_CREATED = 301;
    public static final int USER_UPDATE_EDITED = 302;
    public static final int PICK_IMAGE_REQUEST = 400;
    public static final int PICK_POINTS_REQUEST =500;
    public static final int PICK_POINTS_CANCALED =500;
    public static final int PICK_POINTS_DONE =501;


    public static boolean ADD_COORDINATE_MODE = false;
    public static boolean DELETE_COORDINATE_MODE = false;
    public static boolean EDIT_COORDINATE_MODE = false;
    public static boolean ADD_TRACK_MODE = false;
    public static boolean FINISH_EDIT_TRACK_MODE = false;
    public static boolean SHOW_DETAILS_POPUP = false;
    public static boolean SHOW_TRACK_POPUP = false;
    public static boolean SHOW_UPDATE_POPUP = false;
    public static boolean MOVE_MARKER = false;

    public static int ROUTE_LINE_WIDTH = 4;
    public static int ZOOM_LEVEL_CURRENT_LOCATION = 13;
    public static int ZOOM_LEVEL_MARKER_CLICK = 16;
    public static int ZOOM_LEVEL_LOCATION = 18;
    public static int RC_SIGN_IN = 1;


    public static final String CURRENT_USER_UID = "current user uid";
    public static final String CURRENT_USER_NAME = "current user name";
    public static final String UPDATE_POSITION = "update position";
    public static final String IMAGE_NAME = "image name";
    public static final String IMAGE_EXTRA = "image extra";
    public static final String FROM_ADD_TRACK = "from add truck";

    public static final String LAST_LOCATION_SAVED = "last location saved";

    public static final String POST_KEY = "posts";
    public static final String NUM_LIKES_KEY = "numLikes";
    public static final String POST_LIKED_KEY = "post_liked";
    public static final String POST_IMAGES = "post_images";
    public static final String MY_POSTS = "my_posts";
    public static final String EXTRA_POST = "post";
    public static final String COMMENTS_KEY = "comments";
    public static final String USER_RECORD = "user_record";
    public static final String USERS_KEY = "users";
    public static final String NUM_COMMENTS_KEY = "numComments";
    public static final String EXTRA_MODEL_KEY = "model_name";
    public static final String POINTS_KEY = "points_of_interest";
    public static final String COORDINATES_KEY = "coordinates";
    public static final String TRACK_KEY = "tracks";
    public static final String UPDATES_KEY = "user_updates";
    public static final String IMAGE_KEY = "storage_images";


}
