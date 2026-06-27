package id.adrianz.ruangkelas.controller.api;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/jadwal")
@RequiredArgsConstructor
public class ScheduleApiController {

    private final ScheduleService scheduleService;

    @GetMapping("/data")
    public List<Map<String, Object>> getJadwal(@RequestParam String classCode) {

        List<Schedule> schedules = scheduleService.getSchedulesByClassCode(classCode);
        List<Map<String, Object>> events = new ArrayList<>();

        for (Schedule s : schedules) {
            LocalDateTime dateTime = s.getStartTime();
            if (dateTime == null) {
                continue;
            }

            Map<String, Object> event = new HashMap<>();

            event.put("id", s.getId());        
            event.put("title", s.getPlace());
            event.put("start", dateTime.toString());
            event.put("end", s.getEndTime() != null ? s.getEndTime().toString() : null);
            event.put("place", s.getPlace());
            event.put("time", dateTime.toLocalTime().toString());

            events.add(event);
        }

        return events;
    }
}