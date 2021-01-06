package jpabook_practice.jpashop_practice.service;

import jpabook_practice.jpashop_practice.domain.*;
import jpabook_practice.jpashop_practice.domain.item.Item;
import jpabook_practice.jpashop_practice.repository.ItemRepository;
import jpabook_practice.jpashop_practice.repository.MemberRepository;
import jpabook_practice.jpashop_practice.repository.OrderRepository;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;

    // �ֹ�
    @Transactional // ������ ����
    public Long order(Long memberId, Long itemId, int count){
        // ��ƼƼ ��ȸ
        Member member = memberRepository.findOne(memberId);
        Item item = itemRepository.findOne(itemId);

        // ������� ����
        Delivery delivery = new Delivery();
        delivery.setAddress(member.getAddress());

        // �ֹ���ǰ ����
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);
        
        // �ֹ� ����
        Order order = Order.createOrder(member, delivery, orderItem);

        // �ֹ� ����
        orderRepository.save(order);// Ʈ������� Ŀ�ԵǴ� ������ flush�� �߻��ϸ� insert ������ ���ư�.
        return order.getId();
    }

    // ���
    @Transactional
    public void cancelOrder(Long orderId){
        // �ֹ� ��ƼƼ ��ȸ
        Order order = orderRepository.findOne(orderId);
        // �ֹ� ���
        order.cancel();
    }

    // �˻�
    public List<Order> findOrders(OrderSearch orderSearch){
        return orderRepository.findAllByCriteria(orderSearch);
    }

    /*
    ������ �� ����
    ��ƼƼ�� ����Ͻ� ������ ������ ��ü ������ Ư���� ���� Ȱ���ϴ� ��
    ���� ������ �ܼ��� ��ƼƼ�� �ʿ��� ��û�� �����ϴ� ����.

    Ʈ����� ��ũ��Ʈ ����
    ��ƼƼ�� ����Ͻ� ������ ���� ���� ���� �������� ��κ��� ����Ͻ� ������ ó���ϴ� ��
    �Ϲ������� SQL ����� �Ἥ �� �᳻�����鼭 ¥�� ���
     */
}
