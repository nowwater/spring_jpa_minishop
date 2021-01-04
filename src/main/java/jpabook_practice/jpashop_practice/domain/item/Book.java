package jpabook_practice.jpashop_practice.domain.item;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@DiscriminatorValue("B")
@Getter
@Setter
public class Book extends jpabook_practice.jpashop_practice.domain.item.Item {
    private String author;
    private String isbn;

    public void createBook(BookDTO book){
        super.change(book.getId(), book.getName(), book.getPrice(), book.getStockQuantity(), "book");
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
    }
}
