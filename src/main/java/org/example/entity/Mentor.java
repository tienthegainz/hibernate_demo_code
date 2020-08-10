package org.example.entity;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Mentor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private Integer mid;

    private String position;

    @OneToMany(
            cascade={CascadeType.PERSIST, CascadeType.MERGE},
//            fetch = FetchType.EAGER
            fetch = FetchType.LAZY
    )
//    @Fetch(FetchMode.SUBSELECT)
    private List<Developer> devs = new ArrayList<Developer>( );

    public Mentor(){
    }

    public Mentor(String position){
        this.setPosition(position);
    }

    public Integer getId() {
        return mid;
    }
    public void setId(Integer id) {
        this.mid = id;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) {this.position = position;}

    public List<Developer> getDevs() { return devs; }
    public void setDevs(List devs) { this.devs = devs; }

    @Override
    public String toString() {
        return "Mentor{" +
                "mid=" + mid +
                ", position='" + position + '\'' +
                '}';
    }
}
