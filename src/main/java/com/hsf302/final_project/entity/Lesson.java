package com.hsf302.final_project.entity;

import com.hsf302.final_project.constant.ELessonType;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "lessons")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@Slf4j
public class Lesson extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lesson_id")
    Long lessonId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    CourseSection section;

    @Column(name = "title", columnDefinition = "NVARCHAR(255)")
    String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "lesson_type")
    ELessonType lessonType;

    @Column(name = "display_order")
    Integer displayOrder;

    //TẠI SAO LẠI LÀ MANY TO ONE
    // VIDEO
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "video_file_id")
    AppFile video;

    // SUBTITLE
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "subtitle_file_id")
    AppFile subtitle;

    @Column(name = "duration_seconds")
    Integer durationSeconds;

    // ARTICLE
    @Column(name = "article_content", columnDefinition = "NVARCHAR(MAX)")
    String articleContent;

    @OneToMany(mappedBy = "lesson", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions;
}
