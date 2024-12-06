package com.fit.commonservice.utils;

public class Constant {
    //Topic Auth
    public static final String USER_ONBOARDING_TOPIC = "userOnboarding";
    public static final String USER_ONBOARDED_TOPIC = "userOnboarded";
    public static final String VERIFY_ACCOUNT_TOPIC = "verifyAccount";

    //Topic Recommendation
    public static final String RECOMMEND_PREFERENCES_TOPIC = "recommendByPreferences";
    public static final String RECOMMEND_INTERACTED_TOPIC = "recommendByInteracted";


    //Topic Tour
    public static final String REQUEST_RECOMMENDATION_TOPIC = "requestRecommendation";

    //Topic Booking
    public static final String REQUEST_CHECK_AVAILABLE_SLOT_TOPIC = "checkAvailableSlot";
    public static final String RESPONSE_BOOKING_TOPIC = "responseBooking";
    public static final String VERIFY_BOOKING_TOUR_TOPIC = "verifyBookingTourUser";

    //Topic Notification
    public static final String NOTIFICATION_CREATED_USER_TOPIC = "notificationCreatedUser";
    public static final String NOTIFICATION_BOOKING_TOUR_TOPIC = "notificationBookingTourUser";


    public static final String  JSON_REQ_CREATE_USER= "validator/createUser.schema.json";
}
