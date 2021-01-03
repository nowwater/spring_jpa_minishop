package jpabook_practice.jpashop_practice.service;

import jpabook_practice.jpashop_practice.domain.item.*;
import jpabook_practice.jpashop_practice.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void saveItem(Item item){
        itemRepository.save(item);
    }

    public void updateBook(BookDTO bookDTO) {
        Book book = (Book)itemRepository.findOne(bookDTO.getId());
        book.createBook(bookDTO);
        itemRepository.save(book);
    }

    public void updateAlbum(AlbumDTO albumDTO) {
        Album album = (Album)itemRepository.findOne(albumDTO.getId());
        album.createAlbum(albumDTO);
        itemRepository.save(album);
    }

    public void updateMovie(MovieDTO movieDTO) {
        Movie movie = (Movie)itemRepository.findOne(movieDTO.getId());
        movie.createMovie(movieDTO);
        itemRepository.save(movie);
    }

    public List<Item> findItems(){
        return itemRepository.findAll();
    }

    public Item findOne(Long id){
        return itemRepository.findOne(id);
    }
}
