package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("M")
@Getter @Setter
public class Movie extends Item{
    private String director;
    private String actor;

    public void createMovie(MovieDTO movie){
        super.change(movie.getId(), movie.getName(), movie.getPrice(), movie.getStockQuantity(), "movie");
        this.director = movie.getDirector();
        this.actor = movie.getActor();
    }
}
