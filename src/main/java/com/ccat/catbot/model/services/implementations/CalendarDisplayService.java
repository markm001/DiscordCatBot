package com.ccat.catbot.model.services.implementations;

import java.time.LocalDate;

public class CalendarDisplayService {
    public static String buildCalendarDisplay(int selectedYear, int selectedMonth) {

        LocalDate date = LocalDate.of(selectedYear, selectedMonth, 1);
        int begin = date.getDayOfWeek().getValue() + 1;
        int days = date.lengthOfMonth();

        StringBuilder builder = new StringBuilder();

        String header = " So Mo Tu We Th Fr Sa";
        builder.append(header);

        int day = 1;

        for(int i=1; i<(begin + days); i++) {
            if(i % 7 == 1) builder.append("\n");
            if(i < begin) {
                builder.append("   ");
            } else {
                builder.append((day/10 == 0) ? "  " : " ").append(day);
                day ++;
            }
        }
        return "`\n" + builder.toString() + "\n`";
    }
}
