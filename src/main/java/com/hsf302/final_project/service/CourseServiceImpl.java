package com.hsf302.final_project.service;

import com.hsf302.final_project.constant.ECourseStatus;
import com.hsf302.final_project.constant.EFilePurpose;
import com.hsf302.final_project.constant.EFileType;
import com.hsf302.final_project.constant.ELessonType;
import com.hsf302.final_project.dto.request.*;
import com.hsf302.final_project.dto.response.*;
import com.hsf302.final_project.entity.*;
import com.hsf302.final_project.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final CourseVersionRepository courseVersionRepository;
    private final AppFileRepository appFileRepository;
    private final CourseSectionRepository sectionRepository;
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final QuizAnswerRepository quizAnswerRepository;
    private final VideoProcessingService videoProcessingService;
    private final S3Service s3Service;

    public Long createCourseOverview(CourseOverviewForm form) {
        Course course;
        CourseVersion version;
        // update
        if (form.getCourseId() != null && form.getCourseVersionId() != null) {
            course = courseRepository.findById(form.getCourseId())
                    .orElseThrow(() ->
                            new RuntimeException("Course not found"));
            version = courseVersionRepository.findById(
                            form.getCourseVersionId())
                    .orElseThrow(() ->
                            new RuntimeException("Course version not found"));
        }
        // create
        else {
            course = new Course();
            course.setAverageRating(BigDecimal.ZERO);
            course.setTotalReviews(0);
            course.setTotalStudents(0);
            courseRepository.save(course);
            version = new CourseVersion();
            version.setCourse(course);
            version.setVersionNumber(1);
            version.setStatus(ECourseStatus.DRAFT);
        }
        version.setTitle(form.getTitle());
        version.setSubtitle(form.getSubtitle());
        version.setDescription(form.getDescription());
        version.setPrice(
                form.getPrice() != null
                        ? form.getPrice()
                        : BigDecimal.ZERO
        );
        // thumbnail
        MultipartFile thumbnailFile = form.getThumbnailFile();
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            // upload lên S3
            String fileUrl = s3Service.uploadFileWithoutException(thumbnailFile);
            // tạo AppFile
            AppFile appFile = new AppFile();
            appFile.setFileName(thumbnailFile.getOriginalFilename());
            appFile.setFileUrl(fileUrl);
            appFile.setFileType(EFileType.IMAGE);
            appFile.setPurpose(EFilePurpose.COURSE_THUMBNAIL);
            // lấy extension
            String originalName = thumbnailFile.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
                appFile.setExtension(extension);
            }
            // save AppFile
            appFileRepository.save(appFile);
            // set vào version
            version.setThumbnail(appFile);
        }
        courseVersionRepository.save(version);
        // chỉ set version cho course khi create hoặc chưa có current draft version
        if (course.getCurrentDraftVersion() == null) {
            course.setCurrentDraftVersion(version);
        }
        courseRepository.save(course);
        return course.getCourseId();
    }

    @Override
    public CourseOverviewForm getCourseOverview(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));
        // ưu tiên lấy draft
        CourseVersion version =
                courseVersionRepository
                        .findByCourse_CourseIdAndStatus(
                                courseId,
                                ECourseStatus.DRAFT
                        )
                        .orElse(
                                course.getCurrentPublishedVersion()
                        );
        CourseOverviewForm form = new CourseOverviewForm();
        form.setCourseId(course.getCourseId());
        if (version != null) {
            form.setCourseVersionId(version.getCourseVersionId());
            form.setTitle(version.getTitle());
            form.setSubtitle(version.getSubtitle());
            form.setDescription(version.getDescription());
            form.setPrice(version.getPrice());
            String tags = version.getTags()
                    .stream()
                    .map(Tag::getTagName)
                    .collect(Collectors.joining(", "));
            form.setTags(tags);
            form.setThumbnailUrl(version.getThumbnail().getFileUrl());
        }
        return form;
    }

    public Long saveSection(SectionRequest request) {
        CourseSection section;
        if (request.getId() != null) {
            section = sectionRepository.findById(request.getId())
                    .orElseThrow(() ->
                            new RuntimeException("Section not found"));
        } else {
            section = new CourseSection();
        }
        CourseVersion course = courseVersionRepository.findById(request.getCourseVersionId())
                .orElseThrow(() ->
                        new RuntimeException("Course not found"));
        section.setCourseVersion(course);
        section.setTitle(request.getTitle());
        section.setDisplayOrder(request.getDisplayOrder());
        sectionRepository.save(section);
        return section.getSectionId();
    }

    @Override
    public List<SectionResponse> getSectionsByCourseVersionId(Long courseVersionId) {
        List<CourseSection> sections = sectionRepository
                .findByCourseVersion_CourseVersionIdOrderByDisplayOrder(courseVersionId);
        return sections.stream()
                .map(section -> {
                    SectionResponse response = new SectionResponse();
                    response.setId(section.getSectionId());
                    response.setTitle(section.getTitle());
                    response.setDisplayOrder(section.getDisplayOrder());
                    // LESSONS
                    List<Lesson> lessons = lessonRepository
                            .findBySection_SectionIdOrderByDisplayOrder(section.getSectionId());
                    response.setLessons(lessons.stream().map(this::mapLessonResponse).toList());
                    return response;
                })
                .toList();
    }

    @Override
    public void deleteSection(Long sectionId) {
        CourseSection section = sectionRepository
            .findById(sectionId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy chương"));
        sectionRepository.delete(section);
    }

    @Override
    public Long saveLesson(LessonRequest request) {
        Lesson lesson;
        // update
        if (request.getId() != null) {
            lesson = lessonRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Lesson not found"));
        }
        // create
        else {
            lesson = new Lesson();
        }
        CourseSection section = sectionRepository
                        .findById(request.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Section not found"));
        lesson.setSection(section);
        lesson.setTitle(request.getTitle());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setLessonType(ELessonType.valueOf(request.getType()));
        // ARTICLE
        if (lesson.getLessonType() == ELessonType.ARTICLE) {
            lesson.setArticleContent(request.getContent());
        }
        /* save lesson trước để có lessonId */
        lessonRepository.save(lesson);
        // QUIZ
        if (lesson.getLessonType() == ELessonType.QUIZ) {
            // xoá question cũ nếu update
            quizQuestionRepository.deleteByLesson_LessonId(lesson.getLessonId());
            if (request.getQuestions() != null) {
                for (QuestionRequest q : request.getQuestions()) {
                    QuizQuestion question = new QuizQuestion();
                    question.setLesson(lesson);
                    question.setQuestionText(q.getQuestionText());
                    question.setDisplayOrder(q.getDisplayOrder());
                    quizQuestionRepository.save(question);
                    // answers
                    if (q.getAnswers() != null) {
                        int answerOrder = 1;
                        for (AnswerRequest a : q.getAnswers()) {
                            QuizAnswer answer = new QuizAnswer();
                            answer.setQuestion(question);
                            answer.setAnswerText(a.getText());
                            answer.setIsCorrect(a.getIsCorrect());
                            answer.setDisplayOrder(answerOrder++);
                            quizAnswerRepository.save(answer);
                        }
                    }
                }
            }
        }
        return lesson.getLessonId();
    }

    @Override
    @Transactional
    public Long saveVideoLesson(VideoLessonRequest request) {
        Lesson lesson;
        if (request.getId() != null) {
            lesson = lessonRepository
                    .findById(request.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Lesson not found"
                            )
                    );
        }
        else {
            lesson = new Lesson();
        }
        // =========================
        // SECTION
        // =========================
        CourseSection section = sectionRepository
                        .findById(request.getSectionId())
                        .orElseThrow(() -> new RuntimeException("Section not found"));
        lesson.setSection(section);
        lesson.setTitle(request.getTitle());
        lesson.setDisplayOrder(request.getDisplayOrder());
        lesson.setLessonType(ELessonType.VIDEO);
        // =========================
        // VIDEO
        // =========================
        MultipartFile videoFile = request.getVideoFile();
        if (videoFile != null && !videoFile.isEmpty()) {
            // FFmpeg + HLS + Upload S3
            ProcessedVideoResult result = videoProcessingService.processVideo(videoFile);
            AppFile videoAppFile = new AppFile();
            videoAppFile.setFileName(videoFile.getOriginalFilename());
            // m3u8 URL
            videoAppFile.setFileUrl(result.getPlaylistUrl());
            videoAppFile.setFileType(EFileType.VIDEO);
            videoAppFile.setPurpose(EFilePurpose.LESSON_VIDEO);
            // HLS
            videoAppFile.setExtension("m3u8");
            appFileRepository.save(videoAppFile);
            lesson.setVideo(videoAppFile);
        }
        // =========================
        // SUBTITLE
        // =========================
        MultipartFile subtitleFile = request.getSubtitleFile();
        if (subtitleFile != null && !subtitleFile.isEmpty()) {
            // upload subtitle lên S3
            String subtitleUrl = s3Service.uploadFileWithoutException(subtitleFile);
            AppFile subtitleAppFile = new AppFile();
            subtitleAppFile.setFileName(subtitleFile.getOriginalFilename());
            subtitleAppFile.setFileUrl(subtitleUrl);
            subtitleAppFile.setFileType(EFileType.SUBTITLE);
            subtitleAppFile.setPurpose(EFilePurpose.LESSON_SUBTITLE);
            // extension
            String originalName = subtitleFile.getOriginalFilename();
            if (originalName != null && originalName.contains(".")) {
                String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
                subtitleAppFile.setExtension(extension);
            }
            appFileRepository.save(subtitleAppFile);
            lesson.setSubtitle(subtitleAppFile);
        }
        // =========================
        // SAVE LESSON
        // =========================
        lessonRepository.save(lesson);
        return lesson.getLessonId();
    }

    @Override
    public void deleteLesson(Long lessonId) {
        Lesson lesson = lessonRepository
                .findById(lessonId).orElseThrow(() -> new RuntimeException("Không tìm thấy bài học"));
        // nếu là quiz thì xoá question + answer
        if (lesson.getLessonType() == ELessonType.QUIZ) {
            List<QuizQuestion> questions = quizQuestionRepository.findByLessonLessonIdOrderByDisplayOrder(lessonId);
            for (QuizQuestion question : questions) {
                quizAnswerRepository.deleteByQuestion_QuestionId(question.getQuestionId());
            }
            quizQuestionRepository.deleteByLesson_LessonId(lessonId);
        }
        lessonRepository.delete(lesson);
    }

    private LessonResponse mapLessonResponse(Lesson lesson) {
        LessonResponse response = new LessonResponse();
        response.setId(lesson.getLessonId());
        response.setTitle(lesson.getTitle());
        response.setDisplayOrder(lesson.getDisplayOrder());
        response.setType(lesson.getLessonType().name());
        // ARTICLE
        response.setArticleContent(lesson.getArticleContent());
        // VIDEO
        if (lesson.getVideo() != null) {
            response.setVideoUrl(lesson.getVideo().getFileUrl());
        }
        // SUBTITLE
        if (lesson.getSubtitle() != null) {
            response.setSubtitleUrl(lesson.getSubtitle().getFileUrl());
        }
        // QUIZ
        if (lesson.getLessonType() == ELessonType.QUIZ) {
            List<QuizQuestion> questions = quizQuestionRepository
                            .findByLessonLessonIdOrderByDisplayOrder(lesson.getLessonId());
            List<QuestionResponse> questionResponses =
                    questions.stream()
                            .map(question -> {
                                QuestionResponse questionResponse = new QuestionResponse();
                                questionResponse.setId(question.getQuestionId());
                                questionResponse.setQuestionText(question.getQuestionText());
                                questionResponse.setDisplayOrder(question.getDisplayOrder());
                                // ANSWERS
                                List<QuizAnswer> answers =
                                        quizAnswerRepository
                                                .findByQuestionQuestionIdOrderByDisplayOrder(question.getQuestionId());
                                List<AnswerResponse> answerResponses = answers.stream()
                                                .map(answer -> {
                                                    AnswerResponse answerResponse = new AnswerResponse();
                                                    answerResponse.setId(answer.getAnswerId());
                                                    answerResponse.setText(answer.getAnswerText());
                                                    answerResponse.setIsCorrect(answer.getIsCorrect());
                                                    answerResponse.setDisplayOrder(answer.getDisplayOrder());
                                                    return answerResponse;
                                                })
                                                .toList();
                                questionResponse.setAnswers(answerResponses);
                                return questionResponse;
                            })
                            .toList();
            response.setQuestions(questionResponses);
        }
        return response;
    }

    @Override
    @Transactional
    public void deleteCourse(Long courseId) {
        Course course = courseRepository
                .findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        courseRepository.delete(course);
    }

    @Override
    public void publishCourse(Long courseId) {
        Course course = courseRepository
                        .findById(courseId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Course not found"
                                )
                        );
        CourseVersion version = course.getCurrentDraftVersion();
        if (version == null) {
            throw new RuntimeException("Không có phiên bản draft để publish");
        }
        // update status
        version.setStatus(ECourseStatus.APPROVED);
        // set current published version
        course.setCurrentPublishedVersion(version);
        // optional:
        // remove draft reference
        course.setCurrentDraftVersion(null);
        courseVersionRepository.save(version);
        courseRepository.save(course);
    }
}
