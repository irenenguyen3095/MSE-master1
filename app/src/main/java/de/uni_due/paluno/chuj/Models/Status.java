package de.uni_due.paluno.chuj.Models;


public class Status {
    private static boolean msgStatus;
    private static boolean menuStatus;
    private static boolean notificationStatus;
    private static boolean statusForMap;

    public static void setMsgStatus(boolean MsgStatus)
    {
        msgStatus=MsgStatus;
    }
    public static boolean getMsgStatus()
    {
        return msgStatus;
    }
    public static void setMenuStatus(boolean MenuStatus)
    {
        menuStatus=MenuStatus;
    }
    public static boolean getMenuStatus()
    {
        return menuStatus;
    }

    public static boolean getNotificationStatus() {
        return notificationStatus;
    }

    public static void setNotificationStatus(boolean notificationStatus) {
        Status.notificationStatus = notificationStatus;
    }

    public static boolean getStatusForMap() {
        return statusForMap;
    }

    public static void setStatusForMap(boolean statusForMap) {
        Status.statusForMap = statusForMap;
    }
}
