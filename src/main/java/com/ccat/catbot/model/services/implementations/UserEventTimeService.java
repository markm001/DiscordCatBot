package com.ccat.catbot.model.services.implementations;

import com.ccat.catbot.model.entities.UserEventTime;
import com.ccat.catbot.model.repositories.UserEventTimeDao;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class UserEventTimeService {
    private final UserEventTimeDao eventTimeDao;

    public UserEventTimeService(UserEventTimeDao eventTimeDao) {
        this.eventTimeDao = eventTimeDao;
    }

    public UserEventTime saveEventTime(UserEventTime request) {
        return eventTimeDao.save(new UserEventTime(
                UUID.randomUUID().getMostSignificantBits()&Long.MAX_VALUE,
                request.getUserId(),
                request.getEventId(),
                request.getAvailableTime()
        ));
    }

    public void searchSuitableTime(UserEventTime request) {
        List<UserEventTime> userEventTimesResponse = eventTimeDao.findUserTimesForEventId(request.getEventId());

        List<ZonedDateTime> userTimeData = userEventTimesResponse.stream()
                .map(UserEventTime::getAvailableTime)
                .collect(Collectors.toList());

        HashMap<Month, HashMap<Integer, List<ZonedDateTime>>> monthMap = new HashMap<>();
        userTimeData.forEach(zdt -> {
            Month month = zdt.getMonth();
            int dayOfMonth = zdt.getDayOfMonth();

            monthMap.computeIfAbsent(month, v -> new HashMap<>())
                    .computeIfAbsent(dayOfMonth, v -> new ArrayList<>()).add(zdt);
        });

        //Most available Users, Date, Hours:
        AtomicReference<Set<Long>> availableUsers = new AtomicReference<>(Set.of());
        AtomicReference<LocalDate> availableDate = new AtomicReference<>(LocalDate.now());
        AtomicReference<Set<Integer>> availableHours = new AtomicReference<>(Set.of());
        HashMap<String, Integer> timeEvaluation = new HashMap<>();

        monthMap.keySet().forEach(m -> {
            monthMap.get(m).keySet().forEach(d -> {
                List<ZonedDateTime> times = monthMap.get(m).get(d);

                Set<Long> userIdsForDay = userEventTimesResponse.stream()
                        .filter(userEventTime -> times.contains(userEventTime.getAvailableTime()))
                        .map(UserEventTime::getUserId)
                        .collect(Collectors.toSet());

                if(userIdsForDay.size() > availableUsers.get().size()) {
                    availableUsers.set(userIdsForDay);
                    availableDate.set(LocalDate.of(times.get(0).getYear(), m, d));

                    List<Integer> possibleHours = times.stream()
                            .map(ZonedDateTime::getHour)
                            .collect(Collectors.toList());
                    availableHours.set(Set.copyOf(possibleHours));

                    double avg = times.stream()
                            .mapToDouble(ZonedDateTime::getHour)
                            .average().orElse(0.0);

                    int latestTime = times.stream()
                            .mapToInt(ZonedDateTime::getHour)
                            .max().orElse(0);

                    int earliestTime = times.stream()
                            .mapToInt(ZonedDateTime::getHour)
                            .min().orElse(0);

                    int averageTime = (int)(Math.abs(avg-earliestTime) < Math.abs(latestTime-avg) ? Math.floor(avg) : Math.ceil(avg));

                    timeEvaluation.put("AVERAGE",averageTime);
                    timeEvaluation.put("EARLY",earliestTime);
                    timeEvaluation.put("LATE",latestTime);
                }
            });
        });

        HashMap<Long,List<LocalTime>> userSelectedTimes = new HashMap<>();
        availableUsers.get().forEach(uid -> {
            LocalDate selectedDate = availableDate.get();
            List<LocalTime> timeSelection = userEventTimesResponse.stream()
                    .filter(eventData -> eventData.getUserId().equals(uid))
                    .filter(eventData -> {
                        ZonedDateTime date = eventData.getAvailableTime();
                        return date.getYear() == selectedDate.getYear()
                                && date.getMonth().equals(selectedDate.getMonth())
                                && date.getDayOfMonth() == selectedDate.getDayOfMonth();
                    })
                    .map(eventData -> eventData.getAvailableTime().toLocalTime())
                    .collect(Collectors.toList());
            userSelectedTimes.computeIfAbsent(uid, v -> new ArrayList<>()).addAll(timeSelection);
        });



        System.out.println("Result ~ \nBest Date:" + availableDate +
                "\nAll Times:" + availableHours +
                "\nParticipants: " + availableUsers);
        System.out.println(timeEvaluation);
        userSelectedTimes.forEach((u,v) -> System.out.println("User: " + u + " : " + v));
    }
}
