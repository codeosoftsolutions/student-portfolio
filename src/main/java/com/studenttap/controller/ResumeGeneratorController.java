package com.studenttap.controller;

import com.studenttap.model.*;
import com.studenttap.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/public/resume-template")
@CrossOrigin(origins = "*")
public class ResumeGeneratorController {

    @Autowired private StudentRepository studentRepository;
    @Autowired private PersonalDetailsRepository personalDetailsRepository;
    @Autowired private EducationRepository educationRepository;
    @Autowired private CertificationRepository certificationRepository;
    @Autowired private WorkExperienceRepository workExperienceRepository;
    @Autowired private ResumeRepository resumeRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    @GetMapping(value = "/{username}/{template}",
        produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> generateResume(
            @PathVariable String username,
            @PathVariable String template) {
        try {
            Student student = studentRepository
                .findByUsername(username)
                .orElseThrow(() ->
                    new RuntimeException("Student not found"));

            Optional<PersonalDetails> personalOpt =
                personalDetailsRepository
                    .findByStudentId(student.getId());

            List<Education> educations =
                educationRepository
                    .findByStudentIdOrderByDisplayOrderAsc(
                        student.getId());

            List<Certification> certifications =
                certificationRepository
                    .findByStudentId(student.getId());

            List<WorkExperience> experiences =
                workExperienceRepository
                    .findByStudentIdOrderByDisplayOrderAsc(
                        student.getId());

            String html;
            switch (template.toUpperCase()) {
                case "TEMPLATE_1":
                    html = template1Dark(student,
                        personalOpt.orElse(null),
                        educations, certifications, experiences);
                    break;
                case "TEMPLATE_2":
                    html = template2Green(student,
                        personalOpt.orElse(null),
                        educations, certifications, experiences);
                    break;
                case "TEMPLATE_3":
                    html = template3Purple(student,
                        personalOpt.orElse(null),
                        educations, certifications, experiences);
                    break;
                case "TEMPLATE_4":
                    html = template4Bold(student,
                        personalOpt.orElse(null),
                        educations, certifications, experiences);
                    break;
                default:
                    html = template1Dark(student,
                        personalOpt.orElse(null),
                        educations, certifications, experiences);
            }
            return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(html);

        } catch (Exception e) {
            return ResponseEntity.status(404)
                .body("<h1 style='padding:40px;font-family:sans-serif;"
                    + "color:red;'>Resume not found: "
                    + e.getMessage() + "</h1>");
        }
    }

    // ==========================================================
    // TEMPLATE 1 — Classic Dark (FRESHER)
    // Dark sidebar left + white content right
    // ==========================================================
    private String template1Dark(Student s, PersonalDetails p,
            List<Education> edu, List<Certification> certs,
            List<WorkExperience> exp) {

        String skills = p != null && p.getSkills() != null ? p.getSkills() : "";
        String langs = p != null && p.getLanguagesKnown() != null ? p.getLanguagesKnown() : "";
        String obj = p != null && p.getCareerObjective() != null ? p.getCareerObjective()
            : (s.getBio() != null ? s.getBio() : "");

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
            + "<title>" + safe(s.getFullName()) + " — Resume</title>"
            + "<style>"
            + "*{box-sizing:border-box;margin:0;padding:0;}"
            + "body{font-family:'Segoe UI',Arial,sans-serif;"
            + "background:#e8e8e8;display:flex;"
            + "justify-content:center;padding:20px;}"
            + ".page{width:794px;min-height:1123px;background:white;"
            + "display:flex;box-shadow:0 4px 20px rgba(0,0,0,0.2);}"
            + ".sidebar{width:240px;background:#0f172a;color:white;"
            + "padding:30px 20px;flex-shrink:0;}"
            + ".avatar{width:90px;height:90px;border-radius:50%;"
            + "background:linear-gradient(135deg,#6366f1,#818cf8);"
            + "display:flex;align-items:center;justify-content:center;"
            + "font-size:34px;font-weight:700;color:white;"
            + "margin:0 auto 14px;border:3px solid #6366f1;}"
            + ".s-name{font-size:16px;font-weight:700;text-align:center;color:white;line-height:1.3;}"
            + ".s-title{font-size:10px;color:#818cf8;text-align:center;margin-top:4px;letter-spacing:1px;text-transform:uppercase;}"
            + ".divider{height:1px;background:rgba(255,255,255,0.1);margin:14px 0;}"
            + ".s-head{font-size:9px;letter-spacing:2px;text-transform:uppercase;color:#6366f1;margin-bottom:8px;font-weight:700;}"
            + ".s-item{margin-bottom:6px;font-size:10px;color:rgba(255,255,255,0.8);padding-left:4px;}"
            + ".sk{margin-bottom:7px;}"
            + ".sk-name{font-size:10px;color:rgba(255,255,255,0.8);margin-bottom:3px;}"
            + ".sk-bar{height:4px;background:rgba(255,255,255,0.1);border-radius:2px;}"
            + ".sk-fill{height:100%;background:linear-gradient(90deg,#6366f1,#818cf8);border-radius:2px;}"
            + ".lang-chip{display:inline-block;background:rgba(99,102,241,0.3);border:1px solid #6366f1;"
            + "color:rgba(255,255,255,0.8);font-size:9px;padding:2px 8px;border-radius:10px;margin:2px;}"
            + ".main{flex:1;padding:30px 26px;}"
            + ".m-name{font-size:30px;color:#0f172a;font-weight:700;letter-spacing:-0.5px;}"
            + ".m-title{font-size:12px;color:#6366f1;font-weight:600;letter-spacing:1.5px;text-transform:uppercase;margin-top:4px;}"
            + ".accent-line{height:3px;width:60px;background:#6366f1;margin:12px 0;border-radius:2px;}"
            + ".sec{margin-bottom:20px;}"
            + ".sec-h{font-size:13px;font-weight:700;color:#0f172a;margin-bottom:10px;"
            + "border-bottom:2px solid #e2e8f0;padding-bottom:4px;display:flex;align-items:center;gap:6px;}"
            + ".obj-box{font-size:11px;color:#475569;line-height:1.8;background:#f8fafc;"
            + "padding:12px;border-radius:6px;border-left:4px solid #6366f1;}"
            + ".edu-item{margin-bottom:12px;}"
            + ".edu-deg{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".edu-sch{font-size:11px;color:#6366f1;font-weight:600;margin-top:1px;}"
            + ".edu-det{font-size:10px;color:#64748b;margin-top:2px;}"
            + ".badge{display:inline-block;background:#eef2ff;color:#4338ca;"
            + "font-size:9px;font-weight:700;padding:2px 8px;border-radius:10px;margin-left:6px;}"
            + ".exp-wrap{padding-left:12px;border-left:2px solid #e2e8f0;position:relative;margin-bottom:14px;}"
            + ".exp-dot{position:absolute;left:-5px;top:5px;width:8px;height:8px;background:#6366f1;border-radius:50%;}"
            + ".exp-role{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".exp-co{font-size:11px;color:#6366f1;font-weight:600;}"
            + ".exp-per{font-size:10px;color:#94a3b8;margin-top:2px;}"
            + ".exp-desc{font-size:10px;color:#64748b;margin-top:5px;line-height:1.6;}"
            + ".cert-row{display:flex;align-items:center;gap:8px;padding:7px;"
            + "background:#f8fafc;border-radius:6px;margin-bottom:6px;}"
            + ".cert-dot{width:7px;height:7px;background:#6366f1;border-radius:50%;flex-shrink:0;}"
            + ".cert-n{font-size:11px;font-weight:700;color:#1e293b;}"
            + ".cert-o{font-size:10px;color:#64748b;}"
            + ".chip-wrap{display:flex;flex-wrap:wrap;gap:5px;}"
            + ".chip{font-size:10px;padding:3px 10px;border-radius:20px;"
            + "background:#eef2ff;color:#4338ca;border:1px solid #c7d2fe;font-weight:600;}"
            + ".print-btn{position:fixed;bottom:24px;right:24px;background:#6366f1;"
            + "color:white;border:none;padding:11px 20px;border-radius:10px;"
            + "font-size:13px;font-weight:600;cursor:pointer;"
            + "box-shadow:0 4px 12px rgba(99,102,241,0.4);}"
            + "@media print{.print-btn{display:none;}body{background:white;padding:0;}"
            + ".page{box-shadow:none;}}"
            + "</style></head><body>"
            + "<button class='print-btn' onclick='window.print()'>🖨️ Print / Save PDF</button>"
            + "<div class='page'>");

        // SIDEBAR
        String init = s.getFullName() != null ? s.getFullName().substring(0,1).toUpperCase() : "S";
        sb.append("<div class='sidebar'>")
          .append("<div class='avatar'>").append(init).append("</div>")
          .append("<div class='s-name'>").append(safe(s.getFullName())).append("</div>")
          .append("<div class='s-title'>").append(safe(s.getDesignation())).append("</div>")
          .append("<div class='divider'></div>")
          .append("<div class='s-head'>Contact</div>");
        if (s.getPhone() != null) sb.append("<div class='s-item'>📞 ").append(safe(s.getPhone())).append("</div>");
        if (s.getEmailPublic() != null) sb.append("<div class='s-item'>✉️ ").append(safe(s.getEmailPublic())).append("</div>");
        if (s.getAddress() != null) sb.append("<div class='s-item'>📍 ").append(safe(s.getAddress())).append("</div>");
        if (s.getLinkedinUrl() != null) sb.append("<div class='s-item'>💼 LinkedIn</div>");
        if (s.getGithubUrl() != null) sb.append("<div class='s-item'>🐙 GitHub</div>");

        if (!skills.isEmpty()) {
            sb.append("<div class='divider'></div><div class='s-head'>Skills</div>");
            int i = 0;
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (s2.isEmpty()) continue;
                int pct = 65 + (i++ % 4) * 9;
                sb.append("<div class='sk'><div class='sk-name'>").append(safe(s2)).append("</div>")
                  .append("<div class='sk-bar'><div class='sk-fill' style='width:").append(pct).append("%'></div></div></div>");
            }
        }
        if (!langs.isEmpty()) {
            sb.append("<div class='divider'></div><div class='s-head'>Languages</div>");
            for (String l : langs.split(",")) {
                String lang = l.trim();
                if (!lang.isEmpty()) sb.append("<span class='lang-chip'>").append(safe(lang)).append("</span>");
            }
        }
        if (p != null) {
            sb.append("<div class='divider'></div><div class='s-head'>Personal</div>");
            if (p.getDateOfBirth() != null) sb.append("<div class='s-item'>📅 ").append(safe(p.getDateOfBirth())).append("</div>");
            if (p.getGender() != null) sb.append("<div class='s-item'>👤 ").append(safe(p.getGender())).append("</div>");
            if (p.getNationality() != null) sb.append("<div class='s-item'>🌍 ").append(safe(p.getNationality())).append("</div>");
        }
        sb.append("</div>"); // end sidebar

        // MAIN
        sb.append("<div class='main'>")
          .append("<div class='m-name'>").append(safe(s.getFullName())).append("</div>")
          .append("<div class='m-title'>").append(safe(s.getDesignation())).append("</div>")
          .append("<div class='accent-line'></div>");

        if (!obj.isEmpty()) sb.append("<div class='sec'><div class='sec-h'>🎯 Career Objective</div><div class='obj-box'>").append(safe(obj)).append("</div></div>");
        if (!edu.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>🎓 Education</div>");
            for (Education e : edu) {
                sb.append("<div class='edu-item'><div class='edu-deg'>").append(safe(e.getEducationType())).append(" — ").append(safe(e.getFieldOfStudy())).append("</div>")
                  .append("<div class='edu-sch'>").append(safe(e.getInstitutionName())).append("</div>")
                  .append("<div class='edu-det'>").append(safe(e.getBoardUniversity())).append(" · ").append(safe(e.getYearOfPassing()));
                if (e.getPercentageCgpa() != null) sb.append("<span class='badge'>").append(safe(e.getPercentageCgpa())).append("</span>");
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        if (!exp.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>💼 Work Experience</div>");
            for (WorkExperience e : exp) {
                sb.append("<div class='exp-wrap'><div class='exp-dot'></div>")
                  .append("<div class='exp-role'>").append(safe(e.getJobTitle())).append("</div>")
                  .append("<div class='exp-co'>").append(safe(e.getCompanyName())).append(" · ").append(safe(e.getJobLocation())).append("</div>")
                  .append("<div class='exp-per'>").append(safe(e.getStartDate())).append(" – ").append(Boolean.TRUE.equals(e.getIsCurrent()) ? "Present" : safe(e.getEndDate())).append("</div>");
                if (e.getDescription() != null) sb.append("<div class='exp-desc'>").append(safe(e.getDescription())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        }
        if (!certs.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>🏅 Certifications</div>");
            for (Certification c : certs) {
                sb.append("<div class='cert-row'><div class='cert-dot'></div><div>")
                  .append("<div class='cert-n'>").append(safe(c.getCourseName())).append("</div>")
                  .append("<div class='cert-o'>").append(safe(c.getIssuingOrg()));
                if (c.getIssueDate() != null) sb.append(" · ").append(safe(c.getIssueDate()));
                sb.append("</div></div></div>");
            }
            sb.append("</div>");
        }
        if (!skills.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>🛠 Technical Skills</div><div class='chip-wrap'>");
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (!s2.isEmpty()) sb.append("<span class='chip'>").append(safe(s2)).append("</span>");
            }
            sb.append("</div></div>");
        }

        sb.append("</div></div></body></html>");
        return sb.toString();
    }

    // ==========================================================
    // TEMPLATE 2 — Modern Green (FRESHER)
    // Green header banner + two-column body
    // ==========================================================
    private String template2Green(Student s, PersonalDetails p,
            List<Education> edu, List<Certification> certs,
            List<WorkExperience> exp) {

        String skills = p != null && p.getSkills() != null ? p.getSkills() : "";
        String langs = p != null && p.getLanguagesKnown() != null ? p.getLanguagesKnown() : "";
        String obj = p != null && p.getCareerObjective() != null ? p.getCareerObjective()
            : (s.getBio() != null ? s.getBio() : "");

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
            + "<title>" + safe(s.getFullName()) + " — Resume</title>"
            + "<style>"
            + "*{box-sizing:border-box;margin:0;padding:0;}"
            + "body{font-family:'Segoe UI',Arial,sans-serif;background:#e8e8e8;"
            + "display:flex;justify-content:center;padding:20px;}"
            + ".page{width:794px;min-height:1123px;background:white;"
            + "box-shadow:0 4px 20px rgba(0,0,0,0.2);}"
            + ".header{background:linear-gradient(135deg,#064e3b,#059669);"
            + "padding:30px 32px;color:white;display:flex;align-items:center;gap:22px;}"
            + ".avatar{width:90px;height:90px;border-radius:50%;"
            + "background:rgba(255,255,255,0.2);display:flex;align-items:center;"
            + "justify-content:center;font-size:34px;font-weight:700;color:white;"
            + "flex-shrink:0;border:3px solid rgba(255,255,255,0.5);}"
            + ".h-name{font-size:28px;font-weight:700;letter-spacing:-0.5px;}"
            + ".h-title{font-size:12px;color:rgba(255,255,255,0.8);margin-top:4px;"
            + "letter-spacing:1px;text-transform:uppercase;}"
            + ".h-contacts{margin-top:12px;display:flex;flex-wrap:wrap;gap:14px;}"
            + ".h-c{font-size:11px;color:rgba(255,255,255,0.9);}"
            + ".body{display:flex;}"
            + ".left{width:230px;padding:22px 18px;background:#f0fdf4;flex-shrink:0;"
            + "border-right:1px solid #d1fae5;}"
            + ".right{flex:1;padding:22px 24px;}"
            + ".sec-h{font-size:11px;font-weight:700;color:#064e3b;margin-bottom:9px;"
            + "text-transform:uppercase;letter-spacing:1.5px;"
            + "border-bottom:2px solid #059669;padding-bottom:4px;}"
            + ".sec{margin-bottom:18px;}"
            + ".sk-chip{display:inline-block;background:#dcfce7;color:#166534;"
            + "border:1px solid #86efac;font-size:10px;padding:3px 9px;"
            + "border-radius:12px;margin:2px;font-weight:600;}"
            + ".lang-item{font-size:11px;color:#374151;margin-bottom:5px;"
            + "padding-left:12px;border-left:2px solid #059669;}"
            + ".info-item{font-size:10px;color:#4b5563;margin-bottom:5px;}"
            + ".link-item{font-size:10px;color:#059669;margin-bottom:5px;"
            + "font-weight:600;}"
            + ".edu-item{margin-bottom:13px;padding-left:12px;"
            + "border-left:3px solid #059669;}"
            + ".edu-deg{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".edu-sch{font-size:11px;color:#059669;font-weight:600;margin-top:1px;}"
            + ".edu-det{font-size:10px;color:#6b7280;margin-top:2px;}"
            + ".grade{background:#f0fdf4;color:#16a34a;font-size:10px;font-weight:700;"
            + "padding:2px 8px;border-radius:6px;display:inline-block;margin-left:6px;}"
            + ".exp-item{margin-bottom:14px;}"
            + ".exp-header{display:flex;justify-content:space-between;align-items:flex-start;}"
            + ".exp-role{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".exp-per{font-size:10px;color:white;background:#059669;"
            + "padding:2px 8px;border-radius:10px;font-weight:600;}"
            + ".exp-co{font-size:11px;color:#059669;font-weight:600;margin-top:2px;}"
            + ".exp-desc{font-size:10px;color:#6b7280;margin-top:6px;line-height:1.7;}"
            + ".cert-item{padding:8px 10px;background:white;border-radius:6px;"
            + "border:1px solid #d1fae5;margin-bottom:6px;"
            + "border-left:3px solid #059669;}"
            + ".cert-n{font-size:11px;font-weight:700;color:#1e293b;}"
            + ".cert-o{font-size:10px;color:#6b7280;}"
            + ".obj{font-size:11px;color:#374151;line-height:1.8;padding:10px 14px;"
            + "background:#f0fdf4;border-radius:6px;border-left:4px solid #059669;}"
            + ".print-btn{position:fixed;bottom:24px;right:24px;background:#059669;"
            + "color:white;border:none;padding:11px 20px;border-radius:10px;"
            + "font-size:13px;font-weight:600;cursor:pointer;}"
            + "@media print{.print-btn{display:none;}body{background:white;padding:0;}"
            + ".page{box-shadow:none;}}"
            + "</style></head><body>"
            + "<button class='print-btn' onclick='window.print()'>🖨️ Print / Save PDF</button>"
            + "<div class='page'>");

        // HEADER
        String init = s.getFullName() != null ? s.getFullName().substring(0,1).toUpperCase() : "S";
        sb.append("<div class='header'>")
          .append("<div class='avatar'>").append(init).append("</div>")
          .append("<div>")
          .append("<div class='h-name'>").append(safe(s.getFullName())).append("</div>")
          .append("<div class='h-title'>").append(safe(s.getDesignation())).append("</div>")
          .append("<div class='h-contacts'>");
        if (s.getPhone() != null) sb.append("<span class='h-c'>📞 ").append(safe(s.getPhone())).append("</span>");
        if (s.getEmailPublic() != null) sb.append("<span class='h-c'>✉️ ").append(safe(s.getEmailPublic())).append("</span>");
        if (s.getAddress() != null) sb.append("<span class='h-c'>📍 ").append(safe(s.getAddress())).append("</span>");
        sb.append("</div></div></div><div class='body'>");

        // LEFT
        sb.append("<div class='left'>");
        if (!skills.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Skills</div>");
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (!s2.isEmpty()) sb.append("<span class='sk-chip'>").append(safe(s2)).append("</span>");
            }
            sb.append("</div>");
        }
        if (!langs.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Languages</div>");
            for (String l : langs.split(",")) {
                String lang = l.trim();
                if (!lang.isEmpty()) sb.append("<div class='lang-item'>").append(safe(lang)).append("</div>");
            }
            sb.append("</div>");
        }
        if (p != null) {
            sb.append("<div class='sec'><div class='sec-h'>Personal</div>");
            if (p.getDateOfBirth() != null) sb.append("<div class='info-item'>📅 ").append(safe(p.getDateOfBirth())).append("</div>");
            if (p.getGender() != null) sb.append("<div class='info-item'>👤 ").append(safe(p.getGender())).append("</div>");
            if (p.getNationality() != null) sb.append("<div class='info-item'>🌍 ").append(safe(p.getNationality())).append("</div>");
            if (p.getTotalExperience() != null) sb.append("<div class='info-item'>⏱ ").append(safe(p.getTotalExperience())).append("</div>");
            sb.append("</div>");
        }
        if (s.getLinkedinUrl() != null || s.getGithubUrl() != null) {
            sb.append("<div class='sec'><div class='sec-h'>Links</div>");
            if (s.getLinkedinUrl() != null) sb.append("<div class='link-item'>💼 LinkedIn Profile</div>");
            if (s.getGithubUrl() != null) sb.append("<div class='link-item'>🐙 GitHub Profile</div>");
            if (s.getWebsiteUrl() != null) sb.append("<div class='link-item'>🌐 Portfolio Website</div>");
            sb.append("</div>");
        }
        if (!certs.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Certifications</div>");
            for (Certification c : certs) {
                sb.append("<div class='cert-item'><div class='cert-n'>").append(safe(c.getCourseName())).append("</div>")
                  .append("<div class='cert-o'>").append(safe(c.getIssuingOrg()));
                if (c.getIssueDate() != null) sb.append(" · ").append(safe(c.getIssueDate()));
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        sb.append("</div>"); // end left

        // RIGHT
        sb.append("<div class='right'>");
        if (!obj.isEmpty()) sb.append("<div class='sec'><div class='sec-h'>Career Objective</div><div class='obj'>").append(safe(obj)).append("</div></div>");
        if (!edu.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Education</div>");
            for (Education e : edu) {
                sb.append("<div class='edu-item'><div class='edu-deg'>").append(safe(e.getEducationType())).append(" — ").append(safe(e.getFieldOfStudy())).append("</div>")
                  .append("<div class='edu-sch'>").append(safe(e.getInstitutionName())).append("</div>")
                  .append("<div class='edu-det'>").append(safe(e.getBoardUniversity())).append(" · ").append(safe(e.getYearOfPassing()));
                if (e.getPercentageCgpa() != null) sb.append("<span class='grade'>").append(safe(e.getPercentageCgpa())).append("</span>");
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        if (!exp.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Work Experience</div>");
            for (WorkExperience e : exp) {
                sb.append("<div class='exp-item'><div class='exp-header'>")
                  .append("<div class='exp-role'>").append(safe(e.getJobTitle())).append("</div>")
                  .append("<span class='exp-per'>").append(safe(e.getStartDate())).append(" – ").append(Boolean.TRUE.equals(e.getIsCurrent()) ? "Present" : safe(e.getEndDate())).append("</span>")
                  .append("</div><div class='exp-co'>").append(safe(e.getCompanyName())).append(" · ").append(safe(e.getJobLocation())).append("</div>");
                if (e.getDescription() != null) sb.append("<div class='exp-desc'>").append(safe(e.getDescription())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        }
        sb.append("</div></div></div></body></html>");
        return sb.toString();
    }

    // ==========================================================
    // TEMPLATE 3 — Elegant Purple (EXPERIENCED)
    // Full-width header with gradient + timeline experience
    // ==========================================================
    private String template3Purple(Student s, PersonalDetails p,
            List<Education> edu, List<Certification> certs,
            List<WorkExperience> exp) {

        String skills = p != null && p.getSkills() != null ? p.getSkills() : "";
        String obj = p != null && p.getCareerObjective() != null ? p.getCareerObjective()
            : (s.getBio() != null ? s.getBio() : "");

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
            + "<title>" + safe(s.getFullName()) + " — Resume</title>"
            + "<style>"
            + "*{box-sizing:border-box;margin:0;padding:0;}"
            + "body{font-family:'Segoe UI',Arial,sans-serif;background:#e8e8e8;"
            + "display:flex;justify-content:center;padding:20px;}"
            + ".page{width:794px;min-height:1123px;background:white;"
            + "box-shadow:0 4px 20px rgba(0,0,0,0.2);}"
            // HEADER - full width gradient
            + ".header{background:linear-gradient(135deg,#4c1d95,#7c3aed,#8b5cf6);"
            + "padding:32px;color:white;text-align:center;position:relative;overflow:hidden;}"
            + ".header::before{content:'';position:absolute;top:-30px;right:-30px;"
            + "width:150px;height:150px;background:rgba(255,255,255,0.05);border-radius:50%;}"
            + ".header::after{content:'';position:absolute;bottom:-40px;left:-40px;"
            + "width:180px;height:180px;background:rgba(255,255,255,0.04);border-radius:50%;}"
            + ".avatar{width:90px;height:90px;border-radius:50%;"
            + "background:rgba(255,255,255,0.15);display:flex;align-items:center;"
            + "justify-content:center;font-size:34px;font-weight:700;color:white;"
            + "margin:0 auto 14px;border:3px solid rgba(255,255,255,0.4);position:relative;z-index:2;}"
            + ".h-name{font-size:30px;font-weight:700;position:relative;z-index:2;}"
            + ".h-title{font-size:13px;color:rgba(255,255,255,0.8);margin-top:5px;"
            + "letter-spacing:2px;text-transform:uppercase;position:relative;z-index:2;}"
            + ".h-bar{width:60px;height:3px;background:rgba(255,255,255,0.5);"
            + "margin:12px auto;border-radius:2px;position:relative;z-index:2;}"
            + ".h-contacts{display:flex;justify-content:center;flex-wrap:wrap;gap:18px;"
            + "position:relative;z-index:2;}"
            + ".h-c{font-size:11px;color:rgba(255,255,255,0.9);}"
            // CONTACT STRIP
            + ".contact-strip{background:#7c3aed;padding:10px 32px;"
            + "display:flex;gap:20px;flex-wrap:wrap;}"
            + ".cs-item{font-size:10px;color:rgba(255,255,255,0.85);}"
            // BODY
            + ".body{display:flex;}"
            + ".left{width:250px;padding:22px 18px;background:#faf5ff;flex-shrink:0;}"
            + ".right{flex:1;padding:22px 24px;}"
            + ".sec-h{font-size:12px;font-weight:700;color:#4c1d95;margin-bottom:10px;"
            + "text-transform:uppercase;letter-spacing:1px;display:flex;align-items:center;gap:6px;}"
            + ".sec-h::after{content:'';flex:1;height:2px;background:#ede9fe;}"
            + ".sec{margin-bottom:18px;}"
            + ".sk-item{margin-bottom:8px;}"
            + ".sk-name{font-size:11px;color:#374151;margin-bottom:3px;font-weight:500;}"
            + ".sk-bar{height:5px;background:#ede9fe;border-radius:3px;}"
            + ".sk-fill{height:100%;background:linear-gradient(90deg,#7c3aed,#a78bfa);border-radius:3px;}"
            + ".lang-tag{display:inline-block;background:#ede9fe;color:#4c1d95;"
            + "font-size:10px;padding:3px 10px;border-radius:12px;margin:2px;font-weight:600;}"
            + ".cert-item{padding:8px 10px;background:white;border-radius:8px;"
            + "border:1px solid #ede9fe;margin-bottom:7px;border-left:3px solid #7c3aed;}"
            + ".cert-n{font-size:11px;font-weight:700;color:#1e293b;}"
            + ".cert-o{font-size:10px;color:#6b7280;}"
            + ".info-item{font-size:11px;color:#4b5563;margin-bottom:6px;}"
            // Timeline experience
            + ".timeline{position:relative;padding-left:20px;}"
            + ".timeline::before{content:'';position:absolute;left:6px;top:0;bottom:0;"
            + "width:2px;background:linear-gradient(to bottom,#7c3aed,#ede9fe);}"
            + ".t-item{position:relative;margin-bottom:18px;}"
            + ".t-dot{position:absolute;left:-18px;top:4px;width:12px;height:12px;"
            + "background:#7c3aed;border-radius:50%;border:2px solid white;"
            + "box-shadow:0 0 0 2px #7c3aed;}"
            + ".t-role{font-size:13px;font-weight:700;color:#1e293b;}"
            + ".t-co{font-size:11px;color:#7c3aed;font-weight:600;margin-top:2px;}"
            + ".t-per{font-size:10px;background:#ede9fe;color:#4c1d95;"
            + "padding:2px 8px;border-radius:10px;font-weight:600;"
            + "display:inline-block;margin-top:3px;}"
            + ".t-desc{font-size:10px;color:#6b7280;margin-top:6px;line-height:1.7;}"
            + ".edu-item{margin-bottom:13px;background:#faf5ff;"
            + "border-radius:8px;padding:10px 12px;}"
            + ".edu-deg{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".edu-sch{font-size:11px;color:#7c3aed;font-weight:600;margin-top:1px;}"
            + ".edu-det{font-size:10px;color:#6b7280;margin-top:3px;}"
            + ".grade{background:#ede9fe;color:#4c1d95;font-size:10px;font-weight:700;"
            + "padding:2px 8px;border-radius:6px;display:inline-block;margin-left:6px;}"
            + ".obj{font-size:11px;color:#374151;line-height:1.8;padding:12px;"
            + "background:#faf5ff;border-radius:8px;border-left:4px solid #7c3aed;}"
            + ".chip{display:inline-block;background:#ede9fe;color:#4c1d95;"
            + "border:1px solid #c4b5fd;font-size:10px;padding:3px 10px;"
            + "border-radius:12px;margin:2px;font-weight:600;}"
            + ".print-btn{position:fixed;bottom:24px;right:24px;background:#7c3aed;"
            + "color:white;border:none;padding:11px 20px;border-radius:10px;"
            + "font-size:13px;font-weight:600;cursor:pointer;}"
            + "@media print{.print-btn{display:none;}body{background:white;padding:0;}"
            + ".page{box-shadow:none;}}"
            + "</style></head><body>"
            + "<button class='print-btn' onclick='window.print()'>🖨️ Print / Save PDF</button>"
            + "<div class='page'>");

        // HEADER
        String init = s.getFullName() != null ? s.getFullName().substring(0,1).toUpperCase() : "S";
        sb.append("<div class='header'>")
          .append("<div class='avatar'>").append(init).append("</div>")
          .append("<div class='h-name'>").append(safe(s.getFullName())).append("</div>")
          .append("<div class='h-title'>").append(safe(s.getDesignation())).append("</div>")
          .append("<div class='h-bar'></div>")
          .append("<div class='h-contacts'>");
        if (s.getPhone() != null) sb.append("<span class='h-c'>📞 ").append(safe(s.getPhone())).append("</span>");
        if (s.getEmailPublic() != null) sb.append("<span class='h-c'>✉️ ").append(safe(s.getEmailPublic())).append("</span>");
        if (s.getAddress() != null) sb.append("<span class='h-c'>📍 ").append(safe(s.getAddress())).append("</span>");
        if (s.getLinkedinUrl() != null) sb.append("<span class='h-c'>💼 LinkedIn</span>");
        sb.append("</div></div>");

        sb.append("<div class='body'>");

        // LEFT
        sb.append("<div class='left'>");
        if (!skills.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Skills</div>");
            int i = 0;
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (s2.isEmpty()) continue;
                int pct = 65 + (i++ % 4) * 9;
                sb.append("<div class='sk-item'><div class='sk-name'>").append(safe(s2)).append("</div>")
                  .append("<div class='sk-bar'><div class='sk-fill' style='width:").append(pct).append("%'></div></div></div>");
            }
            sb.append("</div>");
        }
        if (p != null && p.getLanguagesKnown() != null) {
            sb.append("<div class='sec'><div class='sec-h'>Languages</div>");
            for (String l : p.getLanguagesKnown().split(",")) {
                String lang = l.trim();
                if (!lang.isEmpty()) sb.append("<span class='lang-tag'>").append(safe(lang)).append("</span>");
            }
            sb.append("</div>");
        }
        if (p != null) {
            sb.append("<div class='sec'><div class='sec-h'>Profile</div>");
            if (p.getDateOfBirth() != null) sb.append("<div class='info-item'>📅 ").append(safe(p.getDateOfBirth())).append("</div>");
            if (p.getGender() != null) sb.append("<div class='info-item'>👤 ").append(safe(p.getGender())).append("</div>");
            if (p.getNationality() != null) sb.append("<div class='info-item'>🌍 ").append(safe(p.getNationality())).append("</div>");
            if (p.getTotalExperience() != null) sb.append("<div class='info-item'>⏱ ").append(safe(p.getTotalExperience())).append("</div>");
            sb.append("</div>");
        }
        if (!certs.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Certifications</div>");
            for (Certification c : certs) {
                sb.append("<div class='cert-item'><div class='cert-n'>").append(safe(c.getCourseName())).append("</div>")
                  .append("<div class='cert-o'>").append(safe(c.getIssuingOrg()));
                if (c.getIssueDate() != null) sb.append(" · ").append(safe(c.getIssueDate()));
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        sb.append("</div>"); // end left

        // RIGHT
        sb.append("<div class='right'>");
        if (!obj.isEmpty()) sb.append("<div class='sec'><div class='sec-h'>Summary</div><div class='obj'>").append(safe(obj)).append("</div></div>");
        if (!exp.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Experience</div><div class='timeline'>");
            for (WorkExperience e : exp) {
                sb.append("<div class='t-item'><div class='t-dot'></div>")
                  .append("<div class='t-role'>").append(safe(e.getJobTitle())).append("</div>")
                  .append("<div class='t-co'>").append(safe(e.getCompanyName())).append(" · ").append(safe(e.getJobLocation())).append("</div>")
                  .append("<span class='t-per'>").append(safe(e.getStartDate())).append(" – ").append(Boolean.TRUE.equals(e.getIsCurrent()) ? "Present" : safe(e.getEndDate())).append("</span>");
                if (e.getDescription() != null) sb.append("<div class='t-desc'>").append(safe(e.getDescription())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div></div>");
        }
        if (!edu.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Education</div>");
            for (Education e : edu) {
                sb.append("<div class='edu-item'><div class='edu-deg'>").append(safe(e.getEducationType())).append(" — ").append(safe(e.getFieldOfStudy())).append("</div>")
                  .append("<div class='edu-sch'>").append(safe(e.getInstitutionName())).append("</div>")
                  .append("<div class='edu-det'>").append(safe(e.getBoardUniversity())).append(" · ").append(safe(e.getYearOfPassing()));
                if (e.getPercentageCgpa() != null) sb.append("<span class='grade'>").append(safe(e.getPercentageCgpa())).append("</span>");
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        if (!skills.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Technical Stack</div>");
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (!s2.isEmpty()) sb.append("<span class='chip'>").append(safe(s2)).append("</span>");
            }
            sb.append("</div>");
        }
        sb.append("</div></div></div></body></html>");
        return sb.toString();
    }

    // ==========================================================
    // TEMPLATE 4 — Bold Red (EXPERIENCED)
    // Red diagonal header + minimal clean layout
    // ==========================================================
    private String template4Bold(Student s, PersonalDetails p,
            List<Education> edu, List<Certification> certs,
            List<WorkExperience> exp) {

        String skills = p != null && p.getSkills() != null ? p.getSkills() : "";
        String obj = p != null && p.getCareerObjective() != null ? p.getCareerObjective()
            : (s.getBio() != null ? s.getBio() : "");

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'/>"
            + "<title>" + safe(s.getFullName()) + " — Resume</title>"
            + "<style>"
            + "*{box-sizing:border-box;margin:0;padding:0;}"
            + "body{font-family:'Segoe UI',Arial,sans-serif;background:#e8e8e8;"
            + "display:flex;justify-content:center;padding:20px;}"
            + ".page{width:794px;min-height:1123px;background:white;"
            + "box-shadow:0 4px 20px rgba(0,0,0,0.2);}"
            + ".header{background:#991b1b;padding:0;position:relative;overflow:hidden;}"
            + ".header-inner{display:flex;align-items:center;gap:24px;"
            + "padding:28px 32px;position:relative;z-index:2;}"
            + ".header::before{content:'';position:absolute;right:-60px;top:0;bottom:0;"
            + "width:200px;background:#7f1d1d;transform:skewX(-10deg);}"
            + ".header::after{content:'';position:absolute;right:-20px;top:0;bottom:0;"
            + "width:100px;background:#b91c1c;transform:skewX(-10deg);}"
            + ".avatar{width:85px;height:85px;border-radius:8px;"
            + "background:rgba(255,255,255,0.15);display:flex;align-items:center;"
            + "justify-content:center;font-size:32px;font-weight:700;color:white;"
            + "flex-shrink:0;border:3px solid rgba(255,255,255,0.4);}"
            + ".h-name{font-size:28px;font-weight:700;color:white;letter-spacing:-0.5px;}"
            + ".h-title{font-size:12px;color:rgba(255,255,255,0.8);margin-top:4px;"
            + "letter-spacing:2px;text-transform:uppercase;}"
            + ".h-exp{font-size:13px;background:rgba(255,255,255,0.2);color:white;"
            + "padding:4px 12px;border-radius:20px;margin-top:8px;"
            + "display:inline-block;font-weight:600;}"
            + ".contact-bar{background:#1c1917;padding:10px 32px;"
            + "display:flex;gap:20px;flex-wrap:wrap;}"
            + ".cb-item{font-size:10px;color:rgba(255,255,255,0.8);}"
            + ".body{padding:24px 32px;}"
            + ".two-col{display:grid;grid-template-columns:1fr 1fr;gap:24px;}"
            + ".full{grid-column:1/-1;}"
            + ".sec-h{font-size:13px;font-weight:700;color:#991b1b;margin-bottom:10px;"
            + "text-transform:uppercase;letter-spacing:1px;"
            + "border-bottom:3px solid #fca5a5;padding-bottom:4px;}"
            + ".sec{margin-bottom:20px;}"
            + ".obj{font-size:11px;color:#374151;line-height:1.8;padding:12px;"
            + "background:#fff1f2;border-radius:6px;border-left:4px solid #dc2626;}"
            + ".edu-item{margin-bottom:13px;padding:10px;background:#fff1f2;"
            + "border-radius:6px;border-left:3px solid #dc2626;}"
            + ".edu-deg{font-size:12px;font-weight:700;color:#1e293b;}"
            + ".edu-sch{font-size:11px;color:#dc2626;font-weight:600;margin-top:1px;}"
            + ".edu-det{font-size:10px;color:#6b7280;margin-top:2px;}"
            + ".grade{background:#fef2f2;color:#dc2626;font-size:10px;font-weight:700;"
            + "padding:2px 8px;border-radius:6px;display:inline-block;margin-left:6px;}"
            + ".exp-item{margin-bottom:16px;padding:12px;background:#f9fafb;"
            + "border-radius:8px;border-top:3px solid #dc2626;}"
            + ".exp-role{font-size:13px;font-weight:700;color:#1e293b;}"
            + ".exp-co{font-size:11px;color:#dc2626;font-weight:600;margin-top:2px;}"
            + ".exp-per{font-size:10px;color:#6b7280;margin-top:2px;}"
            + ".exp-desc{font-size:10px;color:#6b7280;margin-top:6px;line-height:1.7;}"
            + ".sk-row{display:flex;flex-wrap:wrap;gap:6px;}"
            + ".sk-chip{font-size:10px;padding:4px 12px;border-radius:4px;"
            + "background:#fef2f2;color:#991b1b;border:1px solid #fca5a5;font-weight:700;}"
            + ".cert-item{padding:8px 12px;background:#fff1f2;border-radius:6px;"
            + "margin-bottom:7px;border-left:3px solid #dc2626;}"
            + ".cert-n{font-size:11px;font-weight:700;color:#1e293b;}"
            + ".cert-o{font-size:10px;color:#6b7280;}"
            + ".info-row{display:flex;gap:16px;flex-wrap:wrap;margin-bottom:4px;}"
            + ".info-item{font-size:11px;color:#4b5563;}"
            + ".print-btn{position:fixed;bottom:24px;right:24px;background:#dc2626;"
            + "color:white;border:none;padding:11px 20px;border-radius:10px;"
            + "font-size:13px;font-weight:600;cursor:pointer;}"
            + "@media print{.print-btn{display:none;}body{background:white;padding:0;}"
            + ".page{box-shadow:none;}}"
            + "</style></head><body>"
            + "<button class='print-btn' onclick='window.print()'>🖨️ Print / Save PDF</button>"
            + "<div class='page'>");

        // HEADER
        String init = s.getFullName() != null ? s.getFullName().substring(0,1).toUpperCase() : "S";
        sb.append("<div class='header'><div class='header-inner'>")
          .append("<div class='avatar'>").append(init).append("</div>")
          .append("<div>")
          .append("<div class='h-name'>").append(safe(s.getFullName())).append("</div>")
          .append("<div class='h-title'>").append(safe(s.getDesignation())).append("</div>");
        if (p != null && p.getTotalExperience() != null)
            sb.append("<span class='h-exp'>⏱ ").append(safe(p.getTotalExperience())).append(" Experience</span>");
        sb.append("</div></div></div>");

        // Contact bar
        sb.append("<div class='contact-bar'>");
        if (s.getPhone() != null) sb.append("<span class='cb-item'>📞 ").append(safe(s.getPhone())).append("</span>");
        if (s.getEmailPublic() != null) sb.append("<span class='cb-item'>✉️ ").append(safe(s.getEmailPublic())).append("</span>");
        if (s.getAddress() != null) sb.append("<span class='cb-item'>📍 ").append(safe(s.getAddress())).append("</span>");
        if (s.getLinkedinUrl() != null) sb.append("<span class='cb-item'>💼 LinkedIn</span>");
        if (s.getGithubUrl() != null) sb.append("<span class='cb-item'>🐙 GitHub</span>");
        sb.append("</div>");

        sb.append("<div class='body'><div class='two-col'>");

        // Summary - full width
        if (!obj.isEmpty()) sb.append("<div class='sec full'><div class='sec-h'>Professional Summary</div><div class='obj'>").append(safe(obj)).append("</div></div>");

        // Experience - full width
        if (!exp.isEmpty()) {
            sb.append("<div class='sec full'><div class='sec-h'>Work Experience</div>");
            for (WorkExperience e : exp) {
                sb.append("<div class='exp-item'>")
                  .append("<div class='exp-role'>").append(safe(e.getJobTitle())).append("</div>")
                  .append("<div class='exp-co'>").append(safe(e.getCompanyName())).append(" · ").append(safe(e.getJobLocation())).append("</div>")
                  .append("<div class='exp-per'>").append(safe(e.getStartDate())).append(" – ").append(Boolean.TRUE.equals(e.getIsCurrent()) ? "Present" : safe(e.getEndDate())).append("</div>");
                if (e.getDescription() != null) sb.append("<div class='exp-desc'>").append(safe(e.getDescription())).append("</div>");
                sb.append("</div>");
            }
            sb.append("</div>");
        }

        // Education - left column
        if (!edu.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Education</div>");
            for (Education e : edu) {
                sb.append("<div class='edu-item'><div class='edu-deg'>").append(safe(e.getEducationType())).append("</div>")
                  .append("<div class='edu-sch'>").append(safe(e.getInstitutionName())).append("</div>")
                  .append("<div class='edu-det'>").append(safe(e.getYearOfPassing()));
                if (e.getPercentageCgpa() != null) sb.append("<span class='grade'>").append(safe(e.getPercentageCgpa())).append("</span>");
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }

        // Skills + certs - right column
        sb.append("<div>");
        if (!skills.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Technical Skills</div><div class='sk-row'>");
            for (String sk : skills.split(",")) {
                String s2 = sk.trim();
                if (!s2.isEmpty()) sb.append("<span class='sk-chip'>").append(safe(s2)).append("</span>");
            }
            sb.append("</div></div>");
        }
        if (!certs.isEmpty()) {
            sb.append("<div class='sec'><div class='sec-h'>Certifications</div>");
            for (Certification c : certs) {
                sb.append("<div class='cert-item'><div class='cert-n'>").append(safe(c.getCourseName())).append("</div>")
                  .append("<div class='cert-o'>").append(safe(c.getIssuingOrg()));
                if (c.getIssueDate() != null) sb.append(" · ").append(safe(c.getIssueDate()));
                sb.append("</div></div>");
            }
            sb.append("</div>");
        }
        if (p != null) {
            sb.append("<div class='sec'><div class='sec-h'>Personal</div>");
            if (p.getDateOfBirth() != null) sb.append("<div class='info-item'>📅 ").append(safe(p.getDateOfBirth())).append("</div><br>");
            if (p.getGender() != null) sb.append("<div class='info-item'>👤 ").append(safe(p.getGender())).append("</div><br>");
            if (p.getNationality() != null) sb.append("<div class='info-item'>🌍 ").append(safe(p.getNationality())).append("</div>");
            sb.append("</div>");
        }
        sb.append("</div>");

        sb.append("</div></div></div></body></html>");
        return sb.toString();
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}