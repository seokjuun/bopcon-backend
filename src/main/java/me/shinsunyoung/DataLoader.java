package me.shinsunyoung;

import me.shinsunyoung.service.SetlistService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final SetlistService setlistService;

    public DataLoader(SetlistService setlistService) {
        this.setlistService = setlistService;
    }

    @Override
    public void run(String... args) {
        setlistService.fetchAndSaveSetlists();
    }
}
