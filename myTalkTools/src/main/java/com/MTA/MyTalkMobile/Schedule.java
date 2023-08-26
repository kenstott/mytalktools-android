/*
 * Copyright MTA Consulting (c) 2014
 */
package com.MTA.MyTalkMobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Keep;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

// TODO: Auto-generated Javadoc

/**
 * The Class Schedule.
 */
@Keep
class Schedule extends Dialog {

    private final Dialog dialog;
    private final Context context;
    private final int maxYear = 2045;
    private final int maxMonth = 11;
    private final int maxDay = 30;
    private final DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault());
    private final DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault());
    private String mCommand;
    private TextView startDateText;
    private TextView startTimeText;
    private Date startDate;
    private TextView endDateText;
    private Date endDate;
    private Spinner repeatOptions;
    private TableRow tableRow2;
    private ToggleButton toggleButton;


    public Schedule(final Context context) {
        super(context);
        this.dialog = this;
        this.context = context;
    }

    private int getNthDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        //int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        int xDayInMonth = 0;
        for (int i = 1; i <= day; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            if (calendar.get(Calendar.DAY_OF_WEEK) == weekday) {
                xDayInMonth++;
            }
        }
        return xDayInMonth;
    }

    @SuppressLint("StringFormatInvalid")
    private ArrayList<String> getRepeatOptions() {
        ArrayList<String> result = new ArrayList<>();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE", Locale.getDefault());
        String dayOfWeek = dateFormatter.format(startDate);
        dateFormatter = new SimpleDateFormat("MMMM d", Locale.getDefault());
        String monthDay = dateFormatter.format(startDate);
        String[] counts = new String[]{"first", "second", "third", "fourth", "fifth"};
        String nthDay = counts[getNthDay()];
        result.add(context.getString(R.string.just_once));
        result.add(String.format(context.getString(R.string.every_weekday), dayOfWeek));
        result.add(String.format(context.getString(R.string.every_nth_weekday_of_the_month), nthDay, dayOfWeek));
        result.add(String.format(context.getString(R.string.day_n_of_the_month), startDate.getDate()));
        result.add(String.format(context.getString(R.string.every_nth_of_given_month), monthDay));
        result.add(context.getString(R.string.every_day));
        result.add(context.getString(R.string.every_mon_frie));
        result.add(context.getString(R.string.every_sat_sun));
        return result;
    }

    public void updateWidgetsFromCommand(String command) {
        boolean isCommand = false;
        int startYear = 0;
        int startMonth = 0;
        int startDay = 0;
        int stopYear = 0;
        int stopMonth = 0;
        int stopDay = 0;
        int startHour = 0;
        int startMinute = 0;
        int repeat = 0;
        Calendar calendar = Calendar.getInstance();
        String[] items = command.split("/");
        for (int i = 0; i < items.length; i++) {
            String item = items[i];
            if (item.equalsIgnoreCase("mtschedule:")) {
                isCommand = true;
            }
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
            switch (item.toLowerCase()) {
                case "min":
                    startMinute = Integer.parseInt(items[i + 1]);
                    break;
                case "v0":
                    repeat = 0;
                    break;
                case "v1":
                    repeat = 1;
                    break;
                case "v2":
                    repeat = 2;
                    break;
                case "v3":
                    repeat = 3;
                    break;
                case "v4":
                    repeat = 4;
                    break;
                case "v5":
                    repeat = 5;
                    break;
                case "v6":
                    repeat = 6;
                    break;
                case "v7":
                    repeat = 7;
                    break;
                case "v8":
                    repeat = 8;
                    break;
            }
        }
        if (isCommand) {
            if (repeat == 0) {
                tableRow2.setVisibility(View.GONE);
                toggleButton.setVisibility(View.GONE);
            } else {
                tableRow2.setVisibility(View.GONE);
                toggleButton.setVisibility(View.VISIBLE);
            }
            calendar.set(Calendar.YEAR, startYear);
            calendar.set(Calendar.MONTH, startMonth - 1);
            calendar.set(Calendar.DATE, startDay);
            calendar.set(Calendar.HOUR_OF_DAY, startHour);
            calendar.set(Calendar.MINUTE, startMinute);
            calendar.setTimeZone(TimeZone.getDefault());
            startDate = calendar.getTime();
            startDateText.setText(dateFormatter.format(startDate));
            startTimeText.setText(timeFormatter.format(startDate));
            repeatOptions.setAdapter(new ArrayAdapter<>(context, R.layout.simple_text_view, getRepeatOptions()));
            repeatOptions.setSelection(repeat);
            calendar.set(Calendar.YEAR, stopYear);
            calendar.set(Calendar.MONTH, stopMonth - 1);
            calendar.set(Calendar.DATE, stopDay);
            calendar.set(Calendar.HOUR_OF_DAY, 6);
            calendar.set(Calendar.MINUTE, 0);
            endDate = calendar.getTime();
            endDateText.setText(dateFormatter.format(endDate));
            if (endDate.getYear() + 1900 == maxYear) {
                toggleButton.setChecked(false);
                tableRow2.setVisibility(View.GONE);
            } else {
                toggleButton.setChecked(true);
                tableRow2.setVisibility(View.VISIBLE);
            }
        }
    }


    public String getCommand() {
        return mCommand;
    }

    private String getScheduleCommand() {
        Calendar calendar = Calendar.getInstance();
        String command = String.format(Locale.getDefault(), "mtschedule:/v%d", repeatOptions.getSelectedItemPosition());
        calendar.setTime(startDate);
        String start = String.format(Locale.getDefault(), "/start/%d/%d/%d/hour/%d/min/%d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        String date = String.format(Locale.getDefault(), "/start/%d/%d/%d/hour/%d/min/%d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE));
        String dayOfWeek;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                dayOfWeek = "/sun";
                break;
            case Calendar.MONDAY:
                dayOfWeek = "/mon";
                break;
            case Calendar.TUESDAY:
                dayOfWeek = "/tue";
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = "/wed";
                break;
            case Calendar.THURSDAY:
                dayOfWeek = "/thu";
                break;
            case Calendar.FRIDAY:
                dayOfWeek = "/fri";
                break;
            case Calendar.SATURDAY:
                dayOfWeek = "/sat";
                break;

            default:
                dayOfWeek = "";
                break;
        }
        calendar.setTime(endDate);
        String stop = String.format(Locale.getDefault(), "/stop/%d/%d/%d",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DATE));
        int nthDay = getNthDay();
        switch (repeatOptions.getSelectedItemPosition()) {
            case 0: //!
                command = String.format("%s%s", command, date);
                break;
            case 1:
                command = String.format("%s%s/and%s%s", command, start, dayOfWeek, stop);
                break;
            case 2:
                command = String.format(Locale.getDefault(), "%s%s/and%s/weekof/%d%s", command, start, stop, nthDay, dayOfWeek);
                break;
            case 3:
                command = String.format(Locale.getDefault(), "%s/day/%d/and%s%s", command, calendar.get(Calendar.DATE), start, stop);
                break;
            case 4:
                command = String.format(Locale.getDefault(), "%s%s/and/monthday/%d/%d/and/%s", command, start, calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE), stop);
                break;
            case 5:
                command = String.format("%s%s/and%s", command, start, stop);
                break;
            case 6:
                command = String.format("%s%s/and%s/-/sat/sun", command, start, stop);
                break;
            case 7:
                command = String.format("%s%s/and%s/-/mon/tue/wed/thu/fri", command, start, stop);
                break;
            case 8:
                command = "";
                break;

            default:
                break;
        }
        return command;
    }

    protected final void onCreate(final Bundle paramBundle) {
        super.onCreate(paramBundle);
        setContentView(R.layout.schedule);
        setTitle("Edit Schedule");

        Button ok = findViewById(R.id.ok);
        Button cancel = findViewById(R.id.cancel);
        toggleButton = findViewById(R.id.toggleButton);
        startDateText = findViewById(R.id.startDate);
        endDateText = findViewById(R.id.endDate);
        tableRow2 = findViewById(R.id.tableRow2);
        startTimeText = findViewById(R.id.startTime);
        repeatOptions = findViewById(R.id.repeatOption);

        endDate = new Date(maxYear - 1900, maxMonth, maxDay);
        endDateText.setText(dateFormatter.format(endDate));
        startDate = new Date();
        startDateText.setText(dateFormatter.format(startDate));
        startTimeText.setText(timeFormatter.format(startDate));

        cancel.setOnClickListener(v -> {
            mCommand = "";
            dialog.cancel();
        });
        ok.setOnClickListener(v -> {
            mCommand = getScheduleCommand();
            dialog.dismiss();
        });
        startDateText.setOnClickListener(v -> {
            int month = startDate.getMonth();
            int year = startDate.getYear() + 1900;
            final int day = startDate.getDate();
            DatePickerDialog d = new DatePickerDialog(context, (view, year1, monthOfYear, dayOfMonth) -> {
                startDate.setYear(year1 - 1900);
                startDate.setMonth(monthOfYear);
                startDate.setDate(dayOfMonth);
                startDateText.setText(dateFormatter.format(startDate));
            }, year, month, day);
            d.show();
        });
        toggleButton.setOnClickListener(v -> {
            tableRow2.setVisibility(toggleButton.isChecked() ? View.VISIBLE : View.GONE);
            if (!toggleButton.isChecked()) {
                endDate = new Date(maxYear - 1900, maxMonth, maxDay);
                endDateText.setText(dateFormatter.format(endDate));
            } else {
                endDate.setTime(startDate.getTime());
                endDate.setYear(endDate.getYear() + 1);
                endDateText.setText(dateFormatter.format(endDate));
            }
        });
        endDateText.setOnClickListener(v -> {
            int month = endDate.getMonth();
            int year = endDate.getYear() + 1900;
            final int day = endDate.getDate();
            DatePickerDialog d = new DatePickerDialog(context, (view, year12, monthOfYear, dayOfMonth) -> {
                endDate.setYear(year12 - 1900);
                endDate.setMonth(monthOfYear);
                endDate.setDate(dayOfMonth);
                endDateText.setText(dateFormatter.format(endDate));
            }, year, month, day);
            d.show();
        });
        startTimeText.setOnClickListener(v -> {
            TimePickerDialog d = new TimePickerDialog(context, (view, hourOfDay, minute) -> {
                startDate.setHours(hourOfDay);
                startDate.setMinutes(minute);
                startTimeText.setText(timeFormatter.format(startDate));
            }, startDate.getHours(), startDate.getMinutes(), false);
            d.show();
        });
        repeatOptions.setAdapter(new ArrayAdapter<>(context, R.layout.simple_text_view, getRepeatOptions()));
        repeatOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toggleButton.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
                if (position == 0) {
                    tableRow2.setVisibility(View.GONE);
                    toggleButton.setChecked(false);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toggleButton.setVisibility(View.GONE);
                tableRow2.setVisibility(View.GONE);
            }
        });
    }
}
