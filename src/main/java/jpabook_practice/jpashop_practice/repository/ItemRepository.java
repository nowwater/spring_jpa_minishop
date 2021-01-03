package jpabook_practice.jpashop_practice.repository;

import jpabook_practice.jpashop_practice.domain.item.Book;
import jpabook_practice.jpashop_practice.domain.item.BookDTO;
import jpabook_practice.jpashop_practice.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {
    private final EntityManager em;

    @Transactional
    public void save(Item item){
        if(item.getId() == null)
            em.persist(item);
        else
            em.merge(item);
    }

    public Item findOne(Long id){
        return em.find(Item.class, id);
    }

    public List<Item> findAll(){
        return em.createQuery("select i from Item i")
                .getResultList();
    }
}
