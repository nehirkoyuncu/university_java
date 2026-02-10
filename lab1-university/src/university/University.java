package university;

import java.util.logging.Logger;

public class University {
    // ... (Sınıf değişkenleri ve iç sınıflar değişmedi) ...
    private String name;
    private String rectorFirst;
    private String rectorLast;

    private int nextStudentID = 10000;
    private java.util.Map<Integer, Student> students = new java.util.HashMap<>();

    private int nextCourseCode = 10;
    private java.util.Map<Integer, Course> courses = new java.util.HashMap<>();

    private java.util.Map<Integer, java.util.Set<Integer>> courseAttendees = new java.util.HashMap<>();
    private java.util.Map<Integer, java.util.Set<Integer>> studentStudyPlan = new java.util.HashMap<>();

    private java.util.Map<Integer, java.util.Map<Integer, java.util.List<Integer>>> studentExams = new java.util.HashMap<>();

    private class Student {
        private int id;
        private String first;
        private String last;

        public Student(int id, String first, String last) {
            this.id = id;
            this.first = first;
            this.last = last;
        }
        
        public String getFirst() { return first; }
        public String getLast() { return last; }
        public String toString() {
            return id + " " + first + " " + last;
        }
    }

    private class Course {
        private int code;
        private String title;
        private String teacher;

        public Course(int code, String title, String teacher) {
            this.code = code;
            this.title = title;
            this.teacher = teacher;
        }
        public String getTitle() { return title; }
        public String toString() {
            return code + "," + title + "," + teacher;
        }
    }

    // R1
    public University(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setRector(String first, String last) {
        this.rectorFirst = first;
        this.rectorLast = last;
    }

    public String getRector() {
        if (rectorFirst == null || rectorLast == null) return null;
        return rectorFirst + " " + rectorLast;
    }

    // R2
    public int enroll(String first, String last) {
        int id = nextStudentID++;
        Student newStudent = new Student(id, first, last);
        students.put(id, newStudent);
        logger.info("New student enrolled: " + id + ", " + first + " " + last);
        return id;
    }

    public String student(int id) {
        Student student = students.get(id);
        if (student == null) return null;
        return student.toString();
    }

    // R3
    public int activate(String title, String teacher) {
        int code = nextCourseCode++;
        Course newCourse = new Course(code, title, teacher);
        courses.put(code, newCourse);
        courseAttendees.put(code, new java.util.HashSet<>());
        logger.info("New course activated: " + code + ", " + title + " " + teacher);
        return code;
    }

    public String course(int code) {
        Course course = courses.get(code);
        if (course == null) return null;
        return course.toString();
    }

    // R4
    public void register(int studentID, int courseCode) {
        if (students.containsKey(studentID) && courses.containsKey(courseCode)) {
            courseAttendees.get(courseCode).add(studentID);
            studentStudyPlan.computeIfAbsent(studentID, k -> new java.util.HashSet<>()).add(courseCode);
            logger.info("Student " + studentID + " signed up for course " + courseCode);
        }
    }

    public String listAttendees(int courseCode) {
        java.util.Set<Integer> attendees = courseAttendees.get(courseCode);
        if (attendees == null || attendees.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int studentID : attendees) {
            String studentInfo = student(studentID);
            if (studentInfo != null) {
                sb.append(studentInfo).append('\n');
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    public String studyPlan(int studentID) {
        java.util.Set<Integer> courseCodes = studentStudyPlan.get(studentID);
        if (courseCodes == null || courseCodes.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int courseCode : courseCodes) {
            String courseInfo = course(courseCode);
            if (courseInfo != null) {
                sb.append(courseInfo).append('\n');
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    // R5
    public void exam(int studentId, int courseID, int grade) {
        if (students.containsKey(studentId) && courses.containsKey(courseID) && grade >= 0 && grade <= 30) {
            studentExams.computeIfAbsent(studentId, k -> new java.util.HashMap<>())
                        .computeIfAbsent(courseID, k -> new java.util.ArrayList<>())
                        .add(grade);
            logger.info("Student " + studentId + " took an exam in course " + courseID + " with grade " + grade);
        }
    }

    public String studentAvg(int studentId) {
        java.util.Map<Integer, java.util.List<Integer>> exams = studentExams.get(studentId);
        
        if (!students.containsKey(studentId) || exams == null || exams.isEmpty()) {
            return "Student " + studentId + " hasn't taken any exams";
        }

        double totalGrade = 0;
        int examCount = 0;

        for (java.util.List<Integer> grades : exams.values()) {
            examCount += grades.size();
            for (int grade : grades) {
                totalGrade += grade;
            }
        }

        if (examCount == 0) {
            return "Student " + studentId + " hasn't taken any exams";
        }

        double average = totalGrade / examCount;
        // DÜZELTME: Başarısız olan testin istediği %.1f formatına getirildi.
        String avgFormatted = java.lang.String.format("%.1f", average); 

        return "Student " + studentId + " : " + avgFormatted;
    }

    public String courseAvg(int courseId) {
        Course course = courses.get(courseId);
        if (course == null) return null;
        String courseTitle = course.getTitle();

        double totalGrade = 0;
        int examCount = 0;

        for (java.util.Map.Entry<Integer, java.util.Map<Integer, java.util.List<Integer>>> entry : studentExams.entrySet()){
            java.util.List<Integer> grades = entry.getValue().get(courseId);
            if (grades != null) {
                examCount += grades.size();
                for (int grade : grades) {
                    totalGrade += grade;
                }
            }
        }

        if (examCount == 0) {
            return "No student has taken the exam in " + courseTitle;
        }

        double average = totalGrade / examCount;
        // DÜZELTME: Başarısız olan testin istediği %.1f formatına getirildi.
        String avgFormatted = java.lang.String.format("%.1f", average);

        return "The average for the course " + courseTitle + " is: " + avgFormatted;
    }

    // R6
    private double calculateStudentScore(int studentId) {
        java.util.Map<Integer, java.util.List<Integer>> exams = studentExams.get(studentId);
        java.util.Set<Integer> enrolledCourses = studentStudyPlan.get(studentId);

        if (exams == null || exams.isEmpty()) {
            return 0.0;
        }

        double totalGrade = 0;
        int examCount = 0;

        for (java.util.List<Integer> grades : exams.values()) {
            examCount += grades.size();
            for (int grade : grades) {
                totalGrade += grade;
            }
        }

        if (examCount == 0) {
            return 0.0;
        }

        double avgGrade = totalGrade / examCount;

        double bonus = 0.0;
        if (enrolledCourses != null && !enrolledCourses.isEmpty() && enrolledCourses.size() > 0) {
            bonus = ((double) examCount / enrolledCourses.size()) * 10.0;
        }

        return avgGrade + bonus;
    }

    public String topThreeStudents() {
        java.util.List<java.util.Map.Entry<Integer, Double>> studentScores = new java.util.ArrayList<>();

        for (int studentId : students.keySet()) {
            if (studentExams.containsKey(studentId)) {
                double score = calculateStudentScore(studentId);
                studentScores.add(new java.util.AbstractMap.SimpleEntry<>(studentId, score));
            }
        }
        
        if (studentScores.isEmpty()) {
            return "";
        }

        studentScores.sort(java.util.Map.Entry.comparingByValue(java.util.Comparator.reverseOrder()));

        int count = java.lang.Math.min(3, studentScores.size());

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < count; i++) {
            java.util.Map.Entry<Integer, Double> entry = studentScores.get(i);
            int studentId = entry.getKey();
            double score = entry.getValue();
            Student student = students.get(studentId);

            // R6 formatı %.2f olarak kalmalı (TestR6_Awards'a uymak için)
            String scoreFormatted = java.lang.String.format("%.2f", score);

            sb.append(student.getFirst()).append(" ").append(student.getLast()).append(" : ").append(scoreFormatted).append('\n');
        }

        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    // R7
    public static final Logger logger = Logger.getLogger("University");
}