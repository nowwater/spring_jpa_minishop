package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("A")
@Getter
@Setter
public class Album extends Item {
    private String artist;
    private String etc;

    public void createAlbum(AlbumDTO album){
        super.change(album.getName(), album.getPrice(), album.getStockQuantity(), "album");
        this.artist = album.getArtist();
        this.etc = album.getEtc();
    }
}
