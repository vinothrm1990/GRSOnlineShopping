package com.app.grsonlineshopping.helper;

import android.content.SharedPreferences;

public class Constants {

    public static SharedPreferences pref;
    public static SharedPreferences.Editor editor;

    public static String BASE_URL = "https://grsshopping.com/jsons/";
    public static String IMAGE_URL = "https://grsshopping.com/admin/uploads/";
    public static String PROFILE_IMAGE_URL = "https://grsshopping.com/jsons/user_pic/";

    public static String LOGIN = "login_user.php";
    public static String REGISTER = "register_user.php";
    public static String GET_OTP = "get_otp.php";
    public static String VERIFY_OTP = "verify_otp.php";
    public static String CHECK = "check_register.php";
    public static String FORGOT = "forgot_password.php";
    public static String GET_BANNER = "get_banner.php";
    public static String GET_DISCOVER = "get_discover1.php";
    public static String GET_BRAND = "get_subcategory.php";
    public static String GET_PRODUCT = "get_products.php";
    public static String RATING = "post_rating.php";
    public static String GET_RATING = "get_review.php";
    public static String ADD_REMOVE_CART = "addremove_cart.php";
    public static String GET_CART_FLAG = "get_cart_flag.php";
    public static String GET_CART= "get_cart.php";
    public static String ADD_QUANTITY= "add_quantity.php?";
    public static String ADD_BAG_QUANTITY= "add_bag_quantity.php?";
    public static String ADD_REMOVE_BAG= "addremove_bag.php?";
    public static String GET_BAG = "get_bag.php?";
    public static String GET_WISHLIST = "get_wishlist.php?";
    public static String GET_SUBPRODUCT = "get_subproducts.php?";
    public static String GET_MOBILE = "get_mobile.php?";
    public static String POST_PIC = "post_pic.php?";
    public static String GET_PROFILE = "get_profile.php?";
    public static String POST_PROFILE = "post_profile.php?";
    public static String MOVE_CART = "movetocart.php?";
    public static String GET_ORDER = "order_history.php?";
    public static String ADD_REMOVE_WISHLIST = "addremove_wishlist.php?";
    public static String GET_WISH_FLAG = "get_wish_flag.php?";
    public static String ORDER_PRODUCT = "order_product.php?";
    public static String ORDER_CONFIRMED = "order_confirmed.php?";
    public static String CHECK_ORDER = "check_order.php?";

    public static String cart="0";
    public static String wish="0";

}
