package id.adrianz.ruangkelas.controller;

import id.adrianz.ruangkelas.model.Schedule;
import id.adrianz.ruangkelas.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/jadwal")
public class ScheduleApiController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping("/data")
    public List<Map<String, Object>> getJadwal(
            @RequestParam Long classId) {

        List<Schedule> schedules =
                scheduleService.getSchedulesByClassCode(classId);

        List<Map<String, Object>> events =
                new ArrayList<>();

        for (Schedule s : schedules) {

            Map<String, Object> event =
                    new HashMap<>();

            event.put("id", s.getId());        

            event.put("title", s.getNamaMatkul());

            event.put("start", s.getTanggalKuliah().toString());

            event.put("jamMulai", s.getJamMulai());
            event.put("jamSelesai", s.getJamSelesai());
            event.put("ruangan", s.getRuangan());
            event.put("dosen", s.getNamaDosen());

            events.add(event);
        }

        return events;
    }
}