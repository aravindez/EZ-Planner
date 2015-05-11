public class tellDay
{
    public static int tellDay(int dd, int mm, int yyyy)
    {
        int[] cnum = {6,4,2,0};
        int c = cnum[(yyyy / 100) % 4];
        int day = (dd + monthNumber(mm,yyyy) + (yyyy % 100) + ((yyyy % 100)/4) + c) % 7;
        return day;
    }

    public static int monthNumber(int mm, int yyyy)
    {
        int[] lymn = {6,2};
        int[] mn = {0,3,3,6,1,4,6,2,5,0,3,5};
        if (isLeap(yyyy) && mm <= 2)
        { return lymn[mm-1]; }
        return mn[mm-1];
    }

    public static boolean isLeap(int yyyy)
    {
        if ((yyyy % 4) == 0)
        { return true; }
        else if ((yyyy % 100) == 0)
        { return false; }
        return false;
    }
}
