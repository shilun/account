package com.account.service.utils;

import com.common.util.DateUtil;

import java.util.Calendar;
import java.util.Date;

public class TimeUtils {

    /**
     * 起始时间
     * @param day 天数 1
     * @return
     */
    public static Date getStartTime(Integer day) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY,0);
        todayStart.set(Calendar.MINUTE,0);
        todayStart.set(Calendar.SECOND,0);
        todayStart.set(Calendar.MILLISECOND,0);
        Long dayMillos = 24*3600*1000l;
        return new Date(todayStart.getTime().getTime()-day*dayMillos);
    }

    /**
     * 结束时间
     * @param day 天数
     * @return
     */
    public static Date getEndTime(Integer day) {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY,23);
        todayEnd.set(Calendar.MINUTE,59);
        todayEnd.set(Calendar.SECOND,59);
        todayEnd.set(Calendar.MILLISECOND,999);
        Long dayMillos = 24*3600*1000l;
        return new Date(todayEnd.getTime().getTime()-day*dayMillos);
    }

    /**
     * 传入时间的当天零点
     * @param date
     * @return
     */
    public static Date getMinTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,1);
        return calendar.getTime();
    }

    /**
     * 传入时间的当天23:59:59 999
     * @param date
     * @return
     */
    public static Date getMaxTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        return new Date(calendar.getTime().getTime()+24*3600*1000l);
    }

    /**
     * 获取传入状态的 最小时间
     * @param dayStatus 1 全部  2 当天  3 本周  4 本月
     * @return
     */
    public static Date getDayStatusTime(Integer dayStatus){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        if(dayStatus==1){
            return date;
        }
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        if(dayStatus==2){//当天

        }
        if(dayStatus==3){//本周
            calendar.set(Calendar.DAY_OF_WEEK,2);
        }
        if(dayStatus==4){//本月
            calendar.set(Calendar.DAY_OF_MONTH,1);
        }
        return calendar.getTime();
    }

    /**
     * 获取传入状态的 最大时间
     * @param dayStatus 1 全部  2 当天  3 本周  4 本月
     * @return
     */
    public static Date getDayStatusMaxTime(Integer dayStatus){
        Calendar calendar = Calendar.getInstance();
        Date date = new Date();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        if(dayStatus==2){//当天
            calendar.add(Calendar.DAY_OF_YEAR,1);
        }
        if(dayStatus==3){//本周
            calendar.set(Calendar.DAY_OF_WEEK,2);
            calendar.add(Calendar.DAY_OF_YEAR,7);
        }
        if(dayStatus==4){//本月
            calendar.set(Calendar.DAY_OF_MONTH,1);
            calendar.add(Calendar.MONTH,1);
        }
        return calendar.getTime();
    }


    public static void main(String[] args) {
        System.out.println(getDayStatusMaxTime(2));

    }
}
