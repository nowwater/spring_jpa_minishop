package jpabook_practice.jpashop_practice.controller;

import jpabook_practice.jpashop_practice.domain.item.*;
import jpabook_practice.jpashop_practice.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    //== 등록할 물건 카테고리 선택 ==//
    @GetMapping("/items/new")
    public String create(Model model){
        model.addAttribute("form", new Book());
        return "items/selectItem";
    }

    //== 물건 별 등록 폼 ==//
    @GetMapping("/items/new/{type}")
    public String createAlbumForm(@PathVariable("type") String itemType, Model model){
        if(itemType.equals("album")){
            AlbumDTO dto = new AlbumDTO();
            dto.setType(itemType);
            model.addAttribute("form", dto);
        } else if(itemType.equals("book")){
            BookDTO dto = new BookDTO();
            dto.setType(itemType);
            model.addAttribute("form", dto);
        } else{
            MovieDTO dto = new MovieDTO();
            dto.setType(itemType);
            model.addAttribute("form", dto);
        }
        return "items/createItemForm";
    }

    //== 새로운 물건 등록 ==//
    @PostMapping("/items/new/album")
    public String createAlbum(AlbumDTO albumDTO){
        Album album = new Album();
        album.createAlbum(albumDTO);
        itemService.saveItem(album);
        return "redirect:/";
    }

    @PostMapping("/items/new/book")
    public String createBook(BookDTO bookDTO){
        Book book = new Book();
        book.createBook(bookDTO);
        itemService.saveItem(book);
        return "redirect:/";
    }

    @PostMapping("/items/new/movie")
    public String createMovie(MovieDTO movieDTO){
        Movie movie = new Movie();
        movie.createMovie(movieDTO);
        itemService.saveItem(movie);
        return "redirect:/";
    }

    //== 물품 목록 확인 ==//
    @GetMapping("/items")
    public String list(Model model){
        List<Item> items = itemService.findItems();
        model.addAttribute("items", items);
        return "items/itemList";
    }

    //== 물품 정보 변경 폼 ==//
    @GetMapping("/items/edit/{itemId}")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model){
        Item item = itemService.findOne(itemId);
        String type = item.getType();
        if(type.equals("album")){
            Album album = (Album)item;
            AlbumDTO albumDTO = new AlbumDTO();
            albumDTO.createAlbumDTO(album);
            model.addAttribute("form", albumDTO);
        }
        else if(type.equals("book")){
            Book book = (Book)item;
            BookDTO bookDTO = new BookDTO();
            bookDTO.createBookDTO(book);
            model.addAttribute("form", bookDTO);
        }
        else if(type.equals("movie")){
            Movie movie = (Movie)item;
            MovieDTO movieDTO = new MovieDTO();
            movieDTO.createMovieDTO(movie);
            model.addAttribute("form", movieDTO);
        }
        return "items/updateItemForm";
    }

    //== 물품 정보 변경 ==//
    @PostMapping("items/edit/album")
    public String updateAlbum(AlbumDTO albumDTO){
        Album album = (Album)itemService.findOne(albumDTO.getId());
        album.createAlbum(albumDTO);
        itemService.saveItem(album);
        return "redirect:/items";
    }
    @PostMapping("items/edit/book")
    public String updateBook(BookDTO bookDTO){
        Book book = (Book)itemService.findOne(bookDTO.getId());
        book.createBook(bookDTO);
        itemService.saveItem(book);
        return "redirect:/items";
    }
    @PostMapping("items/edit/movie")
    public String updateMovie(MovieDTO movieDTO){
        Movie movie = (Movie)itemService.findOne(movieDTO.getId());
        movie.createMovie(movieDTO);
        itemService.saveItem(movie);
        return "redirect:/items";
    }
}
