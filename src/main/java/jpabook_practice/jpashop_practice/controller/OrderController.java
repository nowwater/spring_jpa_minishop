package jpabook_practice.jpashop_practice.controller;

import jpabook_practice.jpashop_practice.domain.Member;
import jpabook_practice.jpashop_practice.domain.Order;
import jpabook_practice.jpashop_practice.domain.item.Item;
import jpabook_practice.jpashop_practice.repository.OrderSearch;
import jpabook_practice.jpashop_practice.service.ItemService;
import jpabook_practice.jpashop_practice.service.MemberService;
import jpabook_practice.jpashop_practice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final ItemService itemService;

    @GetMapping("/order")
    public String orderForm(Model model){
        List<MemberDTO> members = memberService.findMembers();
        List<Item> items = itemService.findItems();

        model.addAttribute("members", members);
        model.addAttribute("items", items);

        return "order/orderForm";
    }

    @PostMapping(value = "/order")
    public String order(@RequestParam("memberId") Long memberId,
                        @RequestParam("itemId") Long itemId, @RequestParam("count") int count) {
        // @RequestParam : form���� submit���� �� name �Ӽ��� �ش��ϴ� ���� ������ �� ����
        Long orderId = orderService.order(memberId, itemId, count);

        // �ٽ� ����Ͻ� ������ service���� @Transactinal �ȿ��� �����ϴ°� ����
        // Controller������ �ĺ��ڸ� �������༭ ����
        // ���� ���ؽ�Ʈ�� �����ϸ鼭 �����ϱ� ������, Ư�̻��׿� ���� �� ���浵 �����ϴ�.

        return "redirect:/orders";// + orderId; // �ֹ���ǰ���� �����̷�Ʈ
    }

    @GetMapping("/orders")//@ModelAttribute("") -> model.addAttribute("orderSearch", ~) �� ������ ��
    public String orderList(@ModelAttribute("orderSearch") OrderSearch orderSearch, Model model){
        List<Order> orders = orderService.findOrders(orderSearch);
        model.addAttribute("orders", orders);
        return "order/orderList";
    }

    @PostMapping("/orders/{orderId}/cancel")
    public String cancelOrder(@PathVariable("orderId") Long orderId){
        orderService.cancelOrder(orderId);
        return "redirect:/orders";
    }
}
