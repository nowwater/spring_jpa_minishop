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
        // @RequestParam : form에서 submit했을 때 name 속성에 해당하는 값을 가져올 수 있음
        Long orderId = orderService.order(memberId, itemId, count);

        // 핵심 비즈니스 로직은 service에서 @Transactinal 안에서 수행하는게 좋다
        // Controller에서는 식별자만 전달해줘서 수행
        // 영속 컨텍스트도 유지하면서 실행하기 때문에, 특이사항에 대해 값 변경도 가능하다.

        return "redirect:/orders";// + orderId; // 주문상품으로 리다이렉트
    }

    @GetMapping("/orders")//@ModelAttribute("") -> model.addAttribute("orderSearch", ~) 를 생략한 것
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
