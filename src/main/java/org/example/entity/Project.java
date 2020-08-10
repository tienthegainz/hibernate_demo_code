package org.example.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity()
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer pid;

    @Column
    private String name;

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "Project_Developer",
            joinColumns = @JoinColumn(name = "pid"),
            inverseJoinColumns = @JoinColumn(name = "did")
    )
    private List<Developer> devs = new ArrayList<Developer>( );

    public Project(){}
    public Project(String name){ this.name = name; }

    public Integer getId() {
        return pid;
    }
    public void setId(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<Developer> getDevs() { return devs; }
    public void setDevs(List devs) { this.devs = devs; }

    @Override
    public String toString() {
        return "Project{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                '}';
    }
}
