package sg.edu.iss.team6.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import sg.edu.iss.team6.model.Course;
import sg.edu.iss.team6.model.CourseClass;
import sg.edu.iss.team6.model.Enrollment;
import sg.edu.iss.team6.model.EnrollmentEnum;
import sg.edu.iss.team6.model.Lecturer;
import sg.edu.iss.team6.service.CourseClassService;
import sg.edu.iss.team6.service.CourseService;
import sg.edu.iss.team6.service.EnrollmentService;
import sg.edu.iss.team6.service.LecturerService;
import sg.edu.iss.team6.service.StudentService;

@Controller
public class LecturerController {

    @Autowired
    private LecturerService lectSvc;

    @Autowired
    private EnrollmentService enrlSvc;

    @Autowired
    private CourseService cseSvc;

    @Autowired
    private CourseClassService cseClsSvc;

    @Autowired
    private StudentService stuSvc;

    private Long retrieveLecturerId(HttpSession sessionObj) {
        String lectuerUsername = (String) sessionObj.getAttribute("username");
        List<Lecturer> lecturerList = lectSvc.findByUser_Username(lectuerUsername);
        long lecturerId = 1; // mock up a lecturer ID
        for(Lecturer lecturer : lecturerList) {
            if(lecturer != null) {
                lecturerId = lecturer.getLecturerId();
            }
        }
        return lecturerId;
    }
    
    @GetMapping("/lecturer")
    public String lecturerHomePage(HttpSession sessionObj, Model model) {
        return "lecturer";
    }


    @RequestMapping(value = "/lecturer/coursesTaught/{lecturerId}", method = RequestMethod.GET)
    public String coursesTaught(@PathVariable long lecturerId, Model model) {
        Lecturer lecturer = lectSvc.findById(lecturerId);

        List<Long> courseIdList = cseClsSvc.findDistinctCourseId(lecturerId);
        ArrayList<Course> courseList = new ArrayList<>();

        for (long courseId : courseIdList) {
            Course course = cseSvc.findById(courseId);
            courseList.add(course);
        }

        model.addAttribute("lecturer", lecturer);
        model.addAttribute("courseList", courseList);
        return "lecturer-courses-taught";
    }

    @RequestMapping(value = "/lecturer/courseEnrollment/{lecturerId}", method = RequestMethod.GET)
    public String courseEnrollmentList(@PathVariable long lecturerId, Model model) {
        Lecturer lecturer = lectSvc.findById(lecturerId);
        ArrayList<CourseClass> courseClassList = cseClsSvc.findByLecturerId(lecturerId);
        ArrayList<Course> courseList = new ArrayList<>();
        ArrayList<Enrollment> enrollmentList = new ArrayList<>();

        for (CourseClass courseClass : courseClassList) {
            Course course = cseSvc.findById(courseClass.getCourse().getCourseId());
            courseList.add(course);
            Enrollment enrollment = enrlSvc.findById(courseClass.getClassId());
            enrollmentList.add(enrollment);
        }

        model.addAttribute(lecturer);
        model.addAttribute(courseClassList);
        model.addAttribute(courseList);
        model.addAttribute(enrollmentList);
        return "lecturer-course-enrollment";
    }
// Lecturer Grade A Course

    @RequestMapping(value = "/lecturer/selectCourseAndClass/", method = RequestMethod.GET)
    public String selectCourseAndClass(HttpSession sessionObj, Model model) {
    	long lecturerId = retrieveLecturerId(sessionObj);
        ArrayList<CourseClass> courseClassList = cseClsSvc.findByLecturerId(lecturerId);
        ArrayList<Course> distinctCourseList = new ArrayList<>();
        for (CourseClass current : courseClassList) {
        	long courseId = current.getCourse().getCourseId();
        	Course course = cseSvc.findById(courseId);
        	if(!distinctCourseList.contains(course)) {
        		distinctCourseList.add(course);
        	}
        }
        model.addAttribute("courseClassList",courseClassList);
        model.addAttribute("courseList", distinctCourseList);
        return "lecturer-select-course-class";
    }

    @RequestMapping(value = "/lecturer/gradeStudentList/{classId}", method = RequestMethod.GET)
    public String viewStudentList(@PathVariable long classId, Model model) {
    	CourseClass courseClass = cseClsSvc.findById(classId);
    	Course course = cseSvc.findById(courseClass.getCourse().getCourseId());
    	ArrayList<Enrollment> enrollmentList = enrlSvc.findByClassId(classId);
    	ArrayList<Enrollment> confirmedEnrollmentList = new ArrayList<>();
    	for(Enrollment enrollment : enrollmentList) {
    		//check enrollment status is only confirmed
    		if(enrollment.getEnrollmentStatus().equals(EnrollmentEnum.CONFIRMED)) {
    			confirmedEnrollmentList.add(enrollment);
    		}
    	}
    	model.addAttribute("courseClass", courseClass);
    	model.addAttribute("course", course);
        model.addAttribute("enrollmentList", confirmedEnrollmentList);
    	return "lecturer-student-list";
    }

	@RequestMapping(value = "/lecturer/gradeStudentList/{enrollmentId}", method = RequestMethod.POST)
	public ModelAndView gradeCourse(@Valid @PathVariable long enrollmentId, @ModelAttribute("enrollment") Enrollment enrollment, BindingResult result) {
		Enrollment currentEnrollment = enrlSvc.findById(enrollmentId);
        ModelAndView modelAndView = new ModelAndView("redirect:/lecturer/gradeStudentList/" + currentEnrollment.getCourseClass().getClassId());
		if(result.hasErrors() || enrollment.getScore() == null) {
        	String message1 = "Invalid input! Please enter a valid score!";
        	modelAndView.addObject("message1", message1);
		}
		else if(enrollment.getScore() < 0 || enrollment.getScore() > 100) {
        	String message2 = "Score out of range! Please enter a range between 0 to 100.";
        	modelAndView.addObject("message2", message2);
        }
		else {
			currentEnrollment.setScore(enrollment.getScore());
			enrlSvc.update(currentEnrollment);
            String message3 = "Score has been successfully uploaded!";
            modelAndView.addObject("message3", message3);
        } 
        return modelAndView;
	}
}