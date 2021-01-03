package jpabook_practice.jpashop_practice.domain.item;

//import jpabook_practice.jpashop_practice.domain.Category;
import jpabook_practice.jpashop_practice.exception.NotEnoughStockException;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "dtype")
// JOINED : 정규화된 스타일
// SINGLE_TABLE : 한 테이블에 다 때려 박음
// TABLE_PER_CLASS : 지금처럼 Book, Album, Movie 로 테이블을 나눠놓은것
@Getter
public abstract class Item {

    @Id @GeneratedValue
    @Column(name = "item_id") // PK 컬럼명
    private Long id;

    private String name;
    private int price;
    private int stockQuantity;
    private String type;

    public void change(String name, int price, int stockQuantity, String type){
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.type = type;
    }
    //@ManyToMany(mappedBy = "items")
    //private List<Category> categories = new ArrayList<>();

    //== 비즈니스 로직 ==//
    // 재고를 늘리고 줄이기 : Domain 주도 설계
    // 엔티티 자체가 해결할 수 있는 것들은 (데이터가 있는) 엔티티안에 비즈니스 로직 넣기 => 응집력
    public void addStock(int quantity){
        this.stockQuantity += quantity;
    }

    public void removeStock(int quantity){
        int restStock = this.stockQuantity - quantity;
        if(restStock < 0) {
            throw new NotEnoughStockException("need more stock");
        }
        this.stockQuantity = restStock;
    }
}