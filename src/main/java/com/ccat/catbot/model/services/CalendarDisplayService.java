package com.ccat.catbot.model.services;

import java.time.LocalDate;

public class CalendarDisplayService {
    public static String buildCalendarDisplay(int selectedYear, int selectedMonth) {

        LocalDate date = LocalDate.of(selectedYear, selectedMonth, 1);
        int begin = date.getDayOfWeek().getValue();
        int days = date.lengthOfMonth();

        StringBuilder builder = new StringBuilder();

        String header = "  S  M  T  W  T  F  S";
        builder.append(header);

        int day = 1;
        for(int i=1; i<= begin + days - 1; i++) {
            if(i % 7 == 1) {
                builder.append("\n");
            }
            if(i < begin) {
                builder.append("   ");
            } else {
                if(day/10 == 0) {
                    builder.append("  ").append(day);
                } else {
                    builder.append(" ").append(day);
                }
                day ++;
            }
        }
        return "`" + builder.toString() + "`";
    }
}
