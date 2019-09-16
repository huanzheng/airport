package com.invent.airport.constants;

import com.intel.aware.utils.L;
import com.invent.airport.ui.AirportApp;

public final class Constants {
    public static final String LB = "1d3212bb702ebcc0";
    public static final String HTTP = "http";
    public static final String UPDATE_SERVER_IP = "www.wendangquan.com";
    public static final String PORT = "8080";
    public static final String ROOT_PATH = "server-0.1";
    public static final int PERIOD = 60;
    public static final String SEPERATER = "/";
    public static final String AND = "&";
    public static final String QUESTION = "?";
    public static final String EQUAL = "=";

    public static final int QUERY_PAGE_SIZE = 10;
    //private static final String TAG = "Constants";
    public static final String URL_TAG = "URL";

    public static final int ASSET_TYPE_CONTROLLED = 0;
    public static final int ASSET_TYPE_NONE_CONTROLLED = 1;
    //UI related
    public static final int OP_NONE = -1;
    public static final int OP_CHECKIN = 1;
    public static final int OP_CHECKOUT = 2;
    public static final int OP_PRECHECKIN = 3;
    public static final int OP_PRECHECKOUT = 4;
    public static final int OP_CREATE_CHECKOUT_TASK = 5;
    public static final int OP_MOVE = 6;
    public static final int OP_CHONGHONG = 7;

    public static final String KEY_OP = "key_op";
    public static final String KEY_ASSET_INFO = "key_asset_info";
    public static final String KEY_PRECHECK_INOUT_INFO = "key_precheckinout_info";

    /*ROLE_WEIXIU: [groupId: 1, groupName: "维修人员"],
ROLE_BANZHANG: [groupId: 2, groupName: "班长"], preCheckIn preCheckOut checkOutTask
ROLE_CANGGUAN: [groupId: 3, groupName: "仓管"],
ROLE_ADMIN: [groupId: 4, groupName: "管理员"],
ROLE_USER: [groupId: 5, groupName: "观察员"],*/
    public static final int GROUP_WORKER = 1;
    public static final int GROUP_LEADER = 2;
    public static final int GROUP_REPO_MANGER = 3;
    public static final int GROUP_ADMIN = 4;
    public static final int GROUP_OBSERVER = 5;

    public static final int INVALID_QUANT = -100;
    public static final Float INVALID_PRICE = (float) -100.0;
    public static final int INVALID_PICI = 0;
    public static final String INVALID_BARCODE = "";

    public static String getFunctionURL(String function) {
        String ret;
        String http, ip, port, rootPath;

        http = AirportApp.getInstance().getHttp();
        ip = AirportApp.getInstance().getAPIIP();
        port = AirportApp.getInstance().getPort();
        rootPath = AirportApp.getInstance().getRootPath();

        if (FUNC_LOGIN.equals(function)) {
            ret = http + "://" + ip + ":" + port + "/" + rootPath + "/" + function;
        } else if (FUNC_GET_SERVERIP.equals(function)) {
            ip = AirportApp.getInstance().getUpdateServerIP();
            ret = http + "://" + ip + ":" + port + "/ip-0.1/IP/" + function;
        } else {
            ret = http + "://" + ip + ":" + port + "/" + rootPath + "/api/" + function;
        }
        L.i(URL_TAG, "URL of function " + function + " is " + ret);
        return ret;
    }

    public static String getImageURL(String image) {
        String imageFunc = getFunctionURL(FUNC_IMAGES);
        String ret = imageFunc + "/" + image;
        L.i(URL_TAG, "Image url is " + ret);
        return ret;
    }

    public static String getAvatarURL(String image) {
        String imageFunc = getFunctionURL(FUNC_AVATAR);
        String ret = imageFunc + "/" + image;
        L.i(URL_TAG, "Avatar url is " + ret);
        return ret;
    }

    public static final String FUNC_GET_SERVERIP = "getServerIP";
    public static final String FUNC_LOGIN = "j_spring_security_check";
    public static final String FUNC_QUERY_WITH_BARCODE = "assets";
    public static final String FUNC_QUERY = "assetsBy";
    public static final String FUNC_QUERY_WITH_BARCODE_PURE_ASSERT_INFO = "getAssetDetails";
    public static final String FUNC_CHECK_IN_UPDATE = "checkInWithBarcode";
    public static final String FUNC_CHECK_IN_NEW = "checkInWithoutBarcode";
    public static final String FUNC_CHECK_OUT = "checkout";
    public static final String FUNC_MOVE = "move";
    public static final String FUNC_PRE_CHECK_IN_NEW = "preCheckIn";
    public static final String FUNC_ADD_PIC_FOR_PRE_CHECK_IN = "addPicForPreCheckIn";
    public static final String FUNC_ADD_PIC_FOR_CHECK_IN = "addPicForCheckIn";
    public static final String FUNC_GET_PIC_URL_FOR_BARCODE = "getPicUrlByBarcode";
    public static final String FUNC_GET_OUTSTANDING_PRE_CHECKINS = "getOutstandingPreCheckIns";
    public static final String FUNC_GET_OUTSTANDING_PRE_CHECKOUTS = "getOutstandingPreCheckOuts";
    public static final String FUNC_PRE_CHECKIN_HANDLED = "markPreCheckInHandled";
    public static final String FUNC_PRE_CHECKOUT = "preCheckout";
    public static final String FUNC_ADD_PIC_FOR_PRE_CHECK_OUT = "addPicForPreCheckout";
    public static final String FUNC_PRE_CHECKOUT_HANDLED = "preCheckOutHandled";
    public static final String FUNC_ASSETS_BY_LOCATION = "assetsByLocationBarcode";
    public static final String FUNC_NEW_CHECKOUT_TASK = "newCheckoutTask";
    public static final String FUNC_TAKE_CHECKOUT_TASK = "takeCheckoutTask";
    public static final String FUNC_GET_CHECKOUT_TASK_LIST = "getCheckoutTaskList";
    public static final String FUNC_GET_CHECKOUT_TASK_INFO = "getCheckoutTaskDetail";
    public static final String FUNC_GET_LOCATION_BY_BARCODE = "getLocationByBarcode";
    public static final String FUNC_GET_USER_DETAILS = "getUserDetails";
    public static final String FUNC_IMAGES = "images";
    public static final String FUNC_ADD_PIC_FOR_CHECKOUT = "addPicForCheckout";
    public static final String FUNC_AVATAR = "avatars";
    public static final String FUNC_CHECKIN_CHONGHONG = "ruKuChongHong";
    public static final String FUNC_CHECKOUT_CHONGHONG = "chuKuChongHong";
    public static final String FUNC_LOCATION_QUERY = "locationQuery";
    public static final String FUNC_CHECK_PICI = "checkOlderPici";
    public static final String FUNC_GET_PRECHECKIN_BY_NAME_MODEL = "getPreCheckInByNameModel";
    public static final String FUNC_GET_PRECHECKIN_BY_LOCATIONBARCODE = "getPreCheckInByLocationBarcode";

    public static final String KEY_ERRORCODE = "errorCode";
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_BARCODE = "barcode";
    public static final String KEY_PAGE = "page";

    public static final int ERR_WRONG_USER = 1;
    public static final int ERR_WRONG_PASSWORD = 2;
    public static final int ERR_NO_ASSET_FOR_BARCODE = 3;

    public static final int ERR_INVALID_PAGE = 4;
    public static final int ERR_AT_LEAST_MODE_OR_NAME = 5;
    public static final int ERR_ASSET_NOT_FOUND = 6;
    public static final int ERR_INVALID_BARCODE = 7;
    public static final int ERR_INVALID_NUMBER = 8;
    public static final int ERR_INVALID_LOCATION = 9;
    public static final int ERR_LOCAITON_NO_MATCH = 10;
    public static final int ERR_NORMAL_ERROR = 11;
    public static final int ERR_INVALID_CHECKOUT_TASK_ID = 12;
    public static final int ERR_INVALID_NAME = 13;
    public static final int ERR_INVALID_SERIAL = 14;
    public static final int ERR_INVALID_PRICE = 15;
    public static final int ERR_INVALID_LOCATION_BARCODE = 16;
    static public final int ERR_INVALID_PCS_PER_PACKAGE = 17;
    public static final int ERR_INVALID_OLD_LOCATION = 18;
    public static final int ERR_INVALID_NEW_LOCATION = 19;
    public static final int ERR_INVALID_RECORD = 20;
    public static final int ERR_INVALID_FILE_FORMAT = 21;
    public static final int ERR_NO_PHOTO = 22;
    public static final int ERR_NO_RECORD = 23;
    public static final int ERR_NO_PREIN_RECORD = 24;
    public static final int ERR_NO_PREOUT_RECORD = 25;
    public static final int ERR_NO_TASK = 26;


    public static final String[] ERROR_MSGS = {
            "",
            "没有对应的用户",           //1
            "密码错误",                 //2
            "",                         //3
            "PAGE无效",                 //4
            "型号或名称至少指定一项",   //5
            "",             //6
            "条形码无效",   //7
            "数目无效",     //8
            "库位无效",     //9
            "库位不匹配",   //10
            "一般错误",             //11
            "型号无效",     //12
            "名称无效",     //13
            "批次无效",     //14
            "价格无效",     //15
            "库位条码无效", //16
            "",                //17
            "旧库位条码无效",  //18
            "新库位条码无效",  //19
            "记录无效",        //20
            "文件格式无效",    //21
            "无照片",          //22
            "无记录",          //23
            "无对应预入库记录", //24
            "无对应预出库记录", //25
            "任务不存在",       //26
    };
}
