package com.lbriceno.weatherapp.tools;

import android.content.Context;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by luis_ on 10/1/2016.
 */
public class BSUtils {

    public static Date getTodayTime(Integer daysToAdd, Integer hour, Integer minute) {
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);

        if (daysToAdd != null)
            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + daysToAdd);

        if (hour != null)
            calendar.set(Calendar.HOUR_OF_DAY, hour);

        if (minute != null)
            calendar.set(Calendar.MINUTE, minute);

        return new Date(calendar.getTimeInMillis() / 1000);
    }


    public static Date getDateByUTCTimestamp(Long dt) {
        return new Date(dt * 1000);
    }

    public static <T> Collection<T> filter(Collection<T> target, IPredicate<T> predicate) {
        Collection<T> result = new ArrayList<T>();
        for (T element : target) {
            if (predicate.apply(element)) {
                result.add(element);
            }
        }
        return result;
    }

    public static String getDayGreeting(Long dt) {
        String greeting = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(dt * 1000));
        int hours = calendar.get(Calendar.HOUR_OF_DAY);

        if (hours >= 1 && hours <= 12) {
            greeting = "Morning";
        } else if (hours >= 12 && hours <= 16) {
            greeting = "Afternoon";
        } else if (hours >= 16 && hours <= 21) {
            greeting = "Evening";
        } else if (hours >= 21 && hours <= 24) {
            greeting = "Night";
        }
        return greeting;
    }

    public interface IPredicate<T> {
        boolean apply(T type);
    }


    public static void showSnackBar(Context context,View view, String message, Integer bgColor, Integer textColor) {
        SpannableStringBuilder snackBarText = new SpannableStringBuilder();
        snackBarText.append(message);
        Snackbar snackbar = Snackbar.make(view, snackBarText, Snackbar.LENGTH_LONG);

        if (bgColor != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                snackbar.getView().setBackgroundColor(context.getColor(bgColor));
                ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(context.getColor(textColor));
            } else {
                snackbar.getView().setBackgroundColor(context.getResources().getColor(bgColor));
                ((TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text)).setTextColor(context.getResources().getColor(textColor));
            }
        }

        snackbar.show();
    }
}
