package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AlbumDTO {
    private String type;
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String artist;
    private String etc;

    //public AlbumDTO(){}
    public void createAlbumDTO(Album album) {
        this.type = album.getType();
        this.id = album.getId();
        this.name = album.getName();
        this.price = album.getPrice();
        this.stockQuantity = album.getStockQuantity();
        this.artist = album.getArtist();
        this.etc = album.getEtc();
    }
}
