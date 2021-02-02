package jpabook_practice.jpashop_practice.api;

import jpabook_practice.jpashop_practice.controller.MemberDTO;
import jpabook_practice.jpashop_practice.domain.Member;
import jpabook_practice.jpashop_practice.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@RestController // @Controller 와 @ResponseBody(JSON, XML로 데이터 보낼 때 사용) 합쳐놓음
@RequiredArgsConstructor
public class MemberApiController {

    // API ���忡 �´� DTO�� ���� �̸� �Ķ���� �Ǵ� ���信 �������!
    private final MemberService memberService;

    @GetMapping("/api/v1/members")
    public List<MemberDTO> membersV1(){
        return memberService.findMembers(); // JSON ������ ���� [{}, {} ...] ó�� �� ��ü�� 1���� Array�� ����.. -> ������ �������� count ��� ������ �߰��ϰ� �; �Ұ�������
        /* [
                "data":[{}..{}..],
                "count": 5
                ..
                �̷������� ������ �߰��� �� �ֵ��� ��ȯ�ؾ���.
                V2 ó�� ������� ����. �ȱ׷� ����Ʈ�� JSON���� ������ �ٷ� Array ó��( [] �� ���δ� ����) ����
                {} �� �����ֵ��� �������
          */
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        // ���� ���� ���� ���� DTO
        List<MemberDTO> findMembers = memberService.findMembers();

        // ���ǿ��� ���� DTO
        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) // ��ƼƼ�� �ٲ�� ���⼭ ������ ������ ���� �� �ִ�.
                .collect(Collectors.toList());

        return new Result(collect.size(), collect); // Result ��� �����⸦ �����ְ� data �ʵ忡 ������ collect ����Ʈ�� ������.
    }

    @Data
    @AllArgsConstructor
    static class Result<T> {
        private int count; // �̷� ������ count �� JSON ���䰴ü�� �߰��ؼ� ��ȯ�� �� �ִ�!!!!!
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name; // ������ ���븸 �������� �� �ִ�.
    }

    @PostMapping("/api/v1/members")
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member){ // JSON으로 온 body를 member 로 그대로 매핑함
        // @Valid : Javax validation. @NotEmpty
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    // API 개발 시 엔티티를 외부로 노출시키지 말자 -> 반드시 별도의 클래스를 사용  (DTO)
    // API 스펙이 바뀌지 않으며, 컴파일 시점에 에러를 잡아낼 수 있다.
    @PostMapping("/api/v2/members")
    // DTO 스펙을 보고 API 가 어떤 값을 받는지 쉽게 알 수 있으며, 필요한 스펙에 맞게 DTO 내에서 값을 변경할 수 있다. (어노테이션, validation ..)
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request){
        Member member = new Member();
        member.setName(request.getName());

        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    }

    @PutMapping("/api/v2/members/{id}")
    public UpdateMemberResponse updateMemberV2(
            @PathVariable("id") Long id,
            @RequestBody @Valid UpdateMemberRequest request){ // id�� request���� name�� �Ѿ��.

        // ������ �� �������̸� ���� ������ ����ϴ°� ����!!!
        memberService.update(id, request.getName());
        MemberDTO findMember = memberService.findOne(id);
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    @Data // DTO ���� Lombok ����� ����. ���� ������ �Դٰ��� �ϴ°ſ���!
    @AllArgsConstructor // �� ������ ��� �Ķ���͸� �� �Ѱ������
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data // API ���� Ȯ�� ����
    static class CreateMemberRequest {
        @NotEmpty // ���⼭ validation�� �����ϴ�
        private String name;
    }

    @Data
    static class CreateMemberResponse{
        private Long id;

        public CreateMemberResponse(Long id) {
            this.id = id;
        }
    }
}
