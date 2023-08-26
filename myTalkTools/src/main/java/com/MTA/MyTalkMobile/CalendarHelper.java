package com.MTA.MyTalkMobile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

import androidx.annotation.Keep;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

@Keep
public class CalendarHelper {
    private final Activity mContext;
    private final String mAccountName;
    private final String mCalendarName;
    private final String mOwnerAccount;
    private final long mCalendarId;

    public CalendarHelper(Activity context, String MyAccountName, String calendarName, String ownerAccount) {
        mAccountName = MyAccountName;
        mContext = context;
        mCalendarName = calendarName;
        mOwnerAccount = ownerAccount;
        mCalendarId = getCalendarId();
    }

    public ArrayList<String> GetCalendars() {
        ArrayList<String> result = new ArrayList<>();
        String[] projection =
                new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.NAME,
                        CalendarContract.Calendars.ACCOUNT_NAME,
                        CalendarContract.Calendars.ACCOUNT_TYPE};
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor calCursor =
                    mContext.getContentResolver().
                            query(CalendarContract.Calendars.CONTENT_URI,
                                    projection,
                                    CalendarContract.Calendars.VISIBLE + " = 1",
                                    null,
                                    CalendarContract.Calendars._ID + " ASC");
            if (calCursor != null) {
                if (calCursor.moveToFirst()) {
                    do {
                        calCursor.getLong(0);
                        String displayName = calCursor.getString(1);
                        result.add(displayName);
                    } while (calCursor.moveToNext());
                }
                calCursor.close();
            }
        }
        return result;
    }

    public long CreateCalendarEvent(Date startDate, Date endDate, String title, String description, String recurrence,
                                    String appPackage, String Uri, String organizer,
                                    String location, int accessLevel, int attendStatus, int allDay,
                                    int canInviteOthers, int guestsCanModify, int availability) {
        long calId = mCalendarId;
        if (calId == -1) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startDate.getTime());
        values.put(CalendarContract.Events.DTEND, endDate.getTime());
        values.put(CalendarContract.Events.RRULE, recurrence);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.EVENT_LOCATION, location);
        values.put(CalendarContract.Events.CALENDAR_ID, calId);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getDisplayName());
        values.put(CalendarContract.Events.DESCRIPTION,
                description);
        values.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, appPackage);
        values.put(CalendarContract.Events.CUSTOM_APP_URI, Uri);
        values.put(CalendarContract.Events.ACCESS_LEVEL, accessLevel);
        values.put(CalendarContract.Events.SELF_ATTENDEE_STATUS, attendStatus);
        values.put(CalendarContract.Events.ALL_DAY, allDay);
        values.put(CalendarContract.Events.ORGANIZER, organizer);
        values.put(CalendarContract.Events.GUESTS_CAN_INVITE_OTHERS, canInviteOthers);
        values.put(CalendarContract.Events.GUESTS_CAN_MODIFY, guestsCanModify);
        values.put(CalendarContract.Events.AVAILABILITY, availability);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Uri uri =
                    mContext.getContentResolver().
                            insert(CalendarContract.Events.CONTENT_URI, values);
            return Long.parseLong(uri != null ? uri.getLastPathSegment() : "0");
        }
        return -1;
    }

    private long AddLocalCalendar() {
        ContentValues values = new ContentValues();
        values.put(
                CalendarContract.Calendars.ACCOUNT_NAME,
                mAccountName);
        values.put(
                CalendarContract.Calendars.VISIBLE,
                0);
        values.put(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(
                CalendarContract.Calendars.NAME,
                mCalendarName);
        values.put(
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
                mCalendarName);
        values.put(
                CalendarContract.Calendars.CALENDAR_COLOR,
                0xffff0000);
        values.put(
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,
                CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(
                CalendarContract.Calendars.OWNER_ACCOUNT,
                mOwnerAccount);
        String tz = TimeZone.getDefault().getDisplayName();
        values.put(
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,
                tz);
        values.put(
                CalendarContract.Calendars.SYNC_EVENTS,
                1);
        Uri.Builder builder =
                CalendarContract.Calendars.CONTENT_URI.buildUpon();
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_NAME,
                mAccountName);
        builder.appendQueryParameter(
                CalendarContract.Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL);
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER,
                "true");
        Uri uri =
                mContext.getContentResolver().insert(builder.build(), values);
        return Long.parseLong(uri != null ? uri.getLastPathSegment() : "0");
    }


    public ArrayList<Event> getAllEvents() {
        ArrayList<Event> result = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor =
                    mContext.getContentResolver().
                            query(
                                    CalendarContract.Events.CONTENT_URI,
                                    null,
                                    CalendarContract.Events.CALENDAR_ID + " = ? ",
                                    new String[]{Long.toString(mCalendarId)},
                                    null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    result.add(new Event(cursor));
                    // read event data
                }
                cursor.close();
            }
        }
        return result;
    }

    public void addAttendee(Event event, String name, String email, boolean required) {
        ContentValues values = new ContentValues();
        values.clear();
        values.put(CalendarContract.Attendees.EVENT_ID, event.id);
        values.put(CalendarContract.Attendees.ATTENDEE_TYPE, required ? 1 : 0);
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, name);
        values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, email);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            mContext.getContentResolver().insert(CalendarContract.Attendees.CONTENT_URI, values);
        }
    }

    public void addReminder(Event event, int before) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, event.id);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        values.put(CalendarContract.Reminders.MINUTES, before);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            mContext.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, values);
        }
    }

    public void deleteEvent(Event event) {
        String[] selArgs =
                new String[]{Long.toString(event.id)};
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            mContext.getContentResolver().
                    delete(
                            CalendarContract.Events.CONTENT_URI,
                            CalendarContract.Events._ID + " =? ",
                            selArgs);
        }
    }

    public void updateEvent(Event event) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.TITLE, event.title);
        values.put(CalendarContract.Events.EVENT_LOCATION, event.location);
        values.put(CalendarContract.Events.DESCRIPTION, event.description);
        values.put(CalendarContract.Events.CUSTOM_APP_PACKAGE, event.customAppPackage);
        values.put(CalendarContract.Events.CUSTOM_APP_URI, event.customAppUri.toString());
        values.put(CalendarContract.Events.RRULE, event.recurrenceRule);
        values.put(CalendarContract.Events.DTSTART, event.startDate.getTime());
        values.put(CalendarContract.Events.DTEND, event.endDate.getTime());
        String[] selArgs =
                new String[]{Long.toString(event.id)};
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            mContext.getContentResolver().
                    update(
                            CalendarContract.Events.CONTENT_URI,
                            values,
                            CalendarContract.Events._ID + " =? ",
                            selArgs);
        }
    }

    private long getCalendarId() {
        String[] projection = new String[]{CalendarContract.Calendars._ID};
        String selection =
                CalendarContract.Calendars.ACCOUNT_NAME +
                        " = ? AND " +
                        CalendarContract.Calendars.ACCOUNT_TYPE +
                        " = ? ";
        // use the same values as above:
        String[] selArgs =
                new String[]{
                        mAccountName,
                        CalendarContract.ACCOUNT_TYPE_LOCAL};
        try {
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
                Cursor cursor =
                        mContext.getContentResolver().
                                query(
                                        CalendarContract.Calendars.CONTENT_URI,
                                        projection,
                                        selection,
                                        selArgs,
                                        null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        long id = cursor.getLong(0);
                        cursor.close();
                        return id;
                    }
                }
            }
        } catch (Exception ex) {
            return -1;
        }
        return AddLocalCalendar();
    }

    public Event GetEvent(long eventId) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            Cursor cursor =
                    mContext.getContentResolver().
                            query(
                                    CalendarContract.Events.CONTENT_URI,
                                    null,
                                    CalendarContract.Events._ID + " = ? ",
                                    new String[]{Long.toString(eventId)},
                                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    Event event = new Event(cursor);
                    cursor.close();
                    return event;
                }
            }
        }
        return null;
    }

    public void DeleteAllEvents() {
        String[] selArgs =
                new String[]{Long.toString(mCalendarId)};
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            mContext.getContentResolver().
                    delete(
                            CalendarContract.Events.CONTENT_URI,
                            CalendarContract.Events.CALENDAR_ID + " =? ",
                            selArgs);
        }
    }

    public void CreateEventsFromDates(List<Date> dates, String title, String description, String uri) {
        //ArrayList<Event> result = new ArrayList<>();
        for (Date d : dates) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(d);
            calendar.add(Calendar.HOUR_OF_DAY, 1);
            long eventId = CreateCalendarEvent(d, calendar.getTime(), title, description, "",
                    "com.MTA.MyTalkMobile", uri, "MyTalkTools", "",
                    CalendarContract.Events.ACCESS_PUBLIC,
                    CalendarContract.Events.STATUS_CONFIRMED, 0, 0, 0,
                    CalendarContract.Events.AVAILABILITY_FREE);
            Event event = GetEvent(eventId);
            addReminder(event, 5);
            //result.add(event);
        }
    }

    public void UpdateAllEvents() {
        ArrayList<BoardContent> contents = BoardContent.getScheduledContent(mContext);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Date now = calendar.getTime();
        DeleteAllEvents();
        for (BoardContent content : contents) {
            List<Date> d = getDatesFromCommand(content.getExternalUrl(), now, 90);
            CreateEventsFromDates(d, content.getText(), "", String.format(Locale.getDefault(), "mytalktools:/content/%d", content.getChildBoardId()));
        }
    }

    public ArrayList<Date> getDatesFromCommand(String command, Date Begin, int duration) {
        int startYear = 0;
        int startMonth = 0;
        int startDay = 0;
        int stopYear = 0;
        int stopMonth = 0;
        int stopDay = 0;
        int startHour = 0;
        int startMinute = 0;
        int repeat = 0;
        int[] monthAndDay = {0, 0};
        ArrayList<Integer> addDays = new ArrayList<>();
        ArrayList<Integer> removeDays = new ArrayList<>();
        ArrayList<Integer> currList = addDays;
        int day = 0;
        int weekOf = 0;
        int weekOfDay = 0;
        String[] items = command.split("/");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.equalsIgnoreCase("date") || item.equalsIgnoreCase("start")) {
                startYear = Integer.parseInt(items[i + 1]);
                startMonth = Integer.parseInt(items[i + 2]);
                startDay = Integer.parseInt(items[i + 3]);
            }
            if (item.equalsIgnoreCase("stop")) {
                stopYear = Integer.parseInt(items[i + 1]);
                stopMonth = Integer.parseInt(items[i + 2]);
                stopDay = Integer.parseInt(items[i + 3]);
            }
            if (item.equalsIgnoreCase("hour")) {
                startHour = Integer.parseInt(items[i + 1]);
            }
            if (item.equalsIgnoreCase("min")) {
                startMinute = Integer.parseInt(items[i + 1]);
            }
            if (item.equalsIgnoreCase("weekof")) {
                weekOf = Integer.parseInt(items[i + 1]);
                switch (items[i + 2]) {
                    case "mon":
                        weekOfDay = Calendar.MONDAY;
                        break;
                    case "tue":
                        weekOfDay = Calendar.TUESDAY;
                        break;
                    case "wed":
                        weekOfDay = Calendar.WEDNESDAY;
                        break;
                    case "thu":
                        weekOfDay = Calendar.THURSDAY;
                        break;
                    case "fri":
                        weekOfDay = Calendar.FRIDAY;
                        break;
                    case "sat":
                        weekOfDay = Calendar.SATURDAY;
                        break;
                    case "sun":
                        weekOfDay = Calendar.SUNDAY;
                        break;
                }
                i += 2;
            } else if (item.equalsIgnoreCase("v0")) {
                repeat = 0;
            } else if (item.equalsIgnoreCase("v1")) {
                repeat = 1;
            } else if (item.equalsIgnoreCase("v2")) {
                repeat = 2;
            } else if (item.equalsIgnoreCase("v3")) {
                repeat = 3;
            } else if (item.equalsIgnoreCase("v4")) {
                repeat = 4;
            } else if (item.equalsIgnoreCase("v5")) {
                repeat = 5;
            } else if (item.equalsIgnoreCase("v6")) {
                repeat = 6;
            } else if (item.equalsIgnoreCase("v7")) {
                repeat = 7;
            } else if (item.equalsIgnoreCase("v8")) {
                repeat = 8;
            } else if (item.equals("-")) {
                currList = removeDays;
            } else if (item.equals("+")) {
                currList = addDays;
            } else if (item.equalsIgnoreCase("mon")) {
                currList.add(Calendar.MONDAY);
            } else if (item.equalsIgnoreCase("tue")) {
                currList.add(Calendar.TUESDAY);
            } else if (item.equalsIgnoreCase("wed")) {
                currList.add(Calendar.WEDNESDAY);
            } else if (item.equalsIgnoreCase("thu")) {
                currList.add(Calendar.THURSDAY);
            } else if (item.equalsIgnoreCase("fri")) {
                currList.add(Calendar.FRIDAY);
            } else if (item.equalsIgnoreCase("sat")) {
                currList.add(Calendar.SATURDAY);
            } else if (item.equalsIgnoreCase("sun")) {
                currList.add(Calendar.SUNDAY);
            } else if (item.equalsIgnoreCase("monthday")) {
                monthAndDay[0] = Integer.parseInt(items[i + 1]);
                monthAndDay[1] = Integer.parseInt(items[i + 2]);
            } else if (item.equalsIgnoreCase("day")) {
                day = Integer.parseInt(items[i + 1]);
            }
        }
        Calendar start = Calendar.getInstance();
        start.set(startYear, startMonth - 1, startDay);
        Calendar stop = Calendar.getInstance();
        if (startYear > 0 && stopYear == 0) stopYear = startYear + 30;
        stop.set(stopYear, stopMonth - 1, stopDay);
        Calendar begin = Calendar.getInstance();
        begin.setTime(Begin);
        Calendar end = Calendar.getInstance();
        end.setTime(Begin);
        end.add(Calendar.DATE, duration);
        ArrayList<Date> dates = new ArrayList<>();

        begin.set(Calendar.HOUR_OF_DAY, 0);
        begin.set(Calendar.MINUTE, 0);
        begin.set(Calendar.SECOND, 0);
        begin.set(Calendar.MILLISECOND, 0);
        end.set(Calendar.HOUR_OF_DAY, 0);
        end.set(Calendar.MINUTE, 0);
        end.set(Calendar.SECOND, 0);
        end.set(Calendar.MILLISECOND, 0);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);
        stop.set(Calendar.HOUR_OF_DAY, 0);
        stop.set(Calendar.MINUTE, 0);
        stop.set(Calendar.SECOND, 0);
        stop.set(Calendar.MILLISECOND, 0);

        for (int x = 0; x < duration; x++) {

            // if date is after the start date,
            // and before the stop date,
            // add date to considered dates
            if (begin.after(start) && begin.before(stop)) {

                // the nth day of a given month
                if (monthAndDay[0] != 0) {
                    if (begin.get(Calendar.MONTH) + 1 == monthAndDay[0] && begin.get(Calendar.DAY_OF_MONTH) == monthAndDay[1]) {
                        dates.add(begin.getTime());
                    }
                }

                // the nth day of every month
                else if (day != 0) {
                    if (begin.get(Calendar.DAY_OF_MONTH) == day) {
                        dates.add(begin.getTime());
                    }
                }

                // weekends || weekdays
                else if (removeDays.size() > 0) {
                    if (!removeDays.contains(begin.get(Calendar.DAY_OF_WEEK))) {
                        dates.add(begin.getTime());
                    }
                } else if (addDays.size() > 0) {
                    if (addDays.contains(begin.get(Calendar.DAY_OF_WEEK))) {
                        dates.add(begin.getTime());
                    }
                }

                // the nth weekday of every month
                else if (weekOf != 0) {
                    if (begin.get(Calendar.DAY_OF_WEEK_IN_MONTH) == weekOf && begin.get(Calendar.DAY_OF_WEEK) == weekOfDay) {
                        dates.add(begin.getTime());
                    }
                } else {
                    dates.add(begin.getTime());
                }
            }
            begin.add(Calendar.DATE, 1);
        }
        for (Date d : dates) {
            d.setHours(startHour);
            d.setMinutes(startMinute);
        }
        return dates;
    }

    public static class Event {
        final long id;
        final long calendar_id;
        final Date startDate;
        final Date endDate;
        final String title;
        final String description;
        final String recurrenceRule;
        final String customAppPackage;
        final Uri customAppUri;
        final String organizer;
        final String location;
        final int accessLevel;
        final int selfAttendeeStatus;
        final int allDay;
        final int guestsCanInviteOthers;
        final int guestsCanModify;
        final int availability;

        @SuppressLint("Range")
        public Event(Cursor cursor) {
            startDate = new Date(cursor.getLong(cursor.getColumnIndex("dtstart")));
            endDate = new Date(cursor.getLong(cursor.getColumnIndex("dtend")));
            recurrenceRule = cursor.getString(cursor.getColumnIndex("rrule"));
            selfAttendeeStatus = cursor.getInt(cursor.getColumnIndex("selfAttendeeStatus"));
            organizer = cursor.getString(cursor.getColumnIndex("organizer"));
            availability = cursor.getInt(cursor.getColumnIndex("availability"));
            accessLevel = cursor.getInt(cursor.getColumnIndex("accessLevel"));
            allDay = cursor.getInt(cursor.getColumnIndex("allDay"));
            id = cursor.getLong(cursor.getColumnIndex("_id"));
            customAppUri = Uri.parse(cursor.getString(cursor.getColumnIndex("customAppUri")));
            description = cursor.getString(cursor.getColumnIndex("description"));
            title = cursor.getString(cursor.getColumnIndex("title"));
            guestsCanInviteOthers = cursor.getInt(cursor.getColumnIndex("guestsCanInviteOthers"));
            location = cursor.getString(cursor.getColumnIndex("eventLocation"));
            guestsCanModify = cursor.getInt(cursor.getColumnIndex("guestsCanModify"));
            customAppPackage = cursor.getString(cursor.getColumnIndex("customAppPackage"));
            calendar_id = cursor.getLong(cursor.getColumnIndex("calendar_id"));
        }
    }
}
