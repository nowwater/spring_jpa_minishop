package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovieDTO {
    private String type;
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String director;
    private String actor;

    public void createMovieDTO(Movie movie) {
        this.type = movie.getType();
        this.id = movie.getId();
        this.name = movie.getName();
        this.price = movie.getPrice();
        this.stockQuantity = movie.getStockQuantity();
        this.director = movie.getDirector();
        this.actor = movie.getActor();
    }
}
