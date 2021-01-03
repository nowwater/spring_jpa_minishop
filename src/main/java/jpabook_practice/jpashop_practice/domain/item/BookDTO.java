package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class BookDTO {
    private String type;
    private Long id;
    private String name;
    private int price;
    private int stockQuantity;
    private String author;
    private String isbn;

    public void createBookDTO(Book book) {
        this.type = book.getType();
        this.id = book.getId();
        this.name = book.getName();
        this.price = book.getPrice();
        this.stockQuantity = book.getStockQuantity();
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
    }
}
