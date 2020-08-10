package org.example.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NamedQueries(
        {
                @NamedQuery(
                        name = "getDevByCourse",
                        query = "select name, age, course from Developer e where e.course = :course"
                )
        }
)
@Entity()
public class Developer {
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer did;

    private String name;
    private Integer age;
    private String course;

    @ManyToMany(
            mappedBy = "devs", cascade={CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    private List<Project> projects = new ArrayList<Project>( );

    public Developer(){
    }

    public Developer(String name, Integer age, String course){
        this.name = name;
        this.age = age;
        this.course = course;
    }

    public Integer getId() {
        return did;
    }

    public void setId(Integer id) {
        this.did = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }
    public void setAge(Integer age) {
        this.age = age;
    }

    public String getCourse() {
        return course;
    }
    public void setCourse(String course) {
        this.course = course;
    }


    public List<Project> getProjects() { return projects; }
    public void setProjects(List projects) { this.projects = projects; }

    @Override
    public String toString() {
        return "Dev{" +
                "id=" + did +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", course='" + course + '\'' +
                '}';
    }
}
