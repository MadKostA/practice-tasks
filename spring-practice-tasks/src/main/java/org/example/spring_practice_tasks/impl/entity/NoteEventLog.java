package org.example.spring_practice_tasks.impl.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "note_event_log")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class NoteEventLog {



}
