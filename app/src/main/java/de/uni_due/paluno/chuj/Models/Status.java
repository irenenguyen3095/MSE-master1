package de.uni_due.paluno.chuj.Models;


public class Status {
    private static boolean msgStatus;
    private static boolean menuStatus;

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
}
