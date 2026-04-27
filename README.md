# AI-driven Academic Feedback and Assessment Management System

Java Spring Boot project with a modern Thymeleaf interface, OpenAI-powered academic assistance, persistent database storage, and CRUD workflows for the academic scope.

## Features

- Role-based demo login: Admin, Faculty, Student
- Course delivery and assessment management
- Course material upload by link
- Assignment creation, submission, grading, grade calculation, feedback, recheck, and resubmission
- Student participation and engagement tracking
- AI quiz/question suggestion
- Curriculum design, CLO/PLO mapping, benchmarking notes, trend notes, stakeholder feedback, version history, and AI gap analysis
- Teaching FAQ CRUD with AI answer generation
- Capstone proposal, supervisor assignment, milestone tracking, originality score, rubric evaluation, AI proposal screening, and AI report summary
- Dashboard analytics
- H2 file database by default
- Optional MySQL profile

## Tech Stack

- Java 17
- Spring Boot 3.3.5
- Spring MVC
- Spring Data JPA / Hibernate
- Thymeleaf
- H2 file database
- MySQL connector
- OpenAI Responses API through Java HttpClient

## Java 26 Note

This project targets Java 17 in Maven, but it can run with your installed Java 26 JDK.

If `java -version` does not work in PowerShell, set these Windows environment variables:

```powershell
setx JAVA_HOME "C:\Program Files\Java\jdk-26"
setx PATH "%PATH%;C:\Program Files\Java\jdk-26\bin"
```

After running those commands, close PowerShell/IntelliJ and open them again.

Inside IntelliJ, you can also select Java directly:

1. `File > Project Structure > Project`
2. Set SDK to `C:\Program Files\Java\jdk-26`
3. Apply

## IntelliJ Run Steps

1. Open IntelliJ IDEA.
2. Click `File > Open`.
3. Select this project folder: `C:\Users\120kh\OneDrive\Documents\New project`.
4. Wait for Maven to download all dependencies.
5. Open `src/main/java/com/example/academicfeedback/AcademicFeedbackApplication.java`.
6. Click the green run button beside the `main` method.
7. Open your browser: `http://localhost:8080`.

## Maven From IntelliJ

If normal `mvn` is not installed, you can use IntelliJ's bundled Maven:

```powershell
$env:JAVA_HOME='C:\Program Files\Java\jdk-26'
& 'C:\Program Files\JetBrains\IntelliJ IDEA 2026.1\plugins\maven\lib\maven3\bin\mvn.cmd' clean package
```

## Demo Login

| Role | Email | Password |
| --- | --- | --- |
| Admin | `admin@demo.com` | `1234` |
| Faculty | `faculty@demo.com` | `1234` |
| Student | `student@demo.com` | `1234` |

## OpenAI Setup

The app works without an API key by showing local fallback drafts. For real OpenAI output, set `OPENAI_API_KEY`.

Do not save your API key inside `application.properties`, Git files, screenshots, or chat messages. If a key is exposed, rotate it from the OpenAI dashboard and use the new key locally.

### IntelliJ Environment Variable

1. Open `Run > Edit Configurations`.
2. Select `AcademicFeedbackApplication`.
3. In `Environment variables`, add `OPENAI_API_KEY=your_api_key_here`.
4. Optional model override: `OPENAI_MODEL=gpt-5.4`.
5. Apply and run again.

After login, open `AI Tools` from the top navigation or dashboard. You can generate:

- Student feedback
- Quiz questions
- FAQ answers
- Curriculum gap analysis
- Capstone screening
- Report summaries

## Database

Default database:

- H2 file database
- Data location: `data/academic_feedback`
- JDBC URL: `jdbc:h2:file:./data/academic_feedback;AUTO_SERVER=TRUE`

H2 console:

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/academic_feedback;AUTO_SERVER=TRUE`
- User Name: `sa`
- Password: empty

## How To Access Database

Run the project first, then open:

`http://localhost:8080/h2-console`

Use exactly:

```text
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:file:./data/academic_feedback;AUTO_SERVER=TRUE
User Name: sa
Password:
```

Leave password blank and click `Connect`.

## Optional MySQL Run

1. Create or allow the app to create database `academic_feedback`.
2. Update `src/main/resources/application-mysql.properties` if your MySQL username/password is different.
3. In IntelliJ `Run > Edit Configurations`, add VM option `-Dspring.profiles.active=mysql`.

## Main Project Flow

1. Login as Admin.
2. Add users from `Users`.
3. Create courses from `Courses`.
4. Add materials, assignments, and engagement records inside a course.
5. Login as Student and submit assignment work.
6. Login as Faculty and grade submissions.
7. Leave feedback blank during grading to generate AI feedback.
8. Student can request recheck or submit revised work if faculty requests resubmission.
9. Use `Curriculum` for CLO/PLO mapping and AI gap analysis.
10. Use `FAQs` for repeated student questions and AI draft answers.
11. Use `Capstone` for proposal screening, milestone tracking, rubric evaluation, and final report summaries.
