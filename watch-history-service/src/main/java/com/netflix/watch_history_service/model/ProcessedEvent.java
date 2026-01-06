package com.netflix.watch_history_service.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class ProcessedEvent {

    @Id
    private String eventId;
}

