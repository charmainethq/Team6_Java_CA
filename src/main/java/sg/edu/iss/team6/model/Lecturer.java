package sg.edu.iss.team6.model;

import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Lecturer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int lecturerId;

    @OneToOne
    @JoinColumn(name = "username")
    private User user;

    private String firstName;
    private String lastName;
    private String email;
    private String contactNo;
    private String address;

    @OneToMany(mappedBy="lecturer")
    private List<CourseClass> CourseClasses;

}
