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

    // API 를 만들 때는 DTO로 만들어서 주고 받아야함 반드시!!
    private final MemberService memberService;

    @GetMapping("/api/v1/members")  // 엔티티에서 필드에 @JsonIgnore를 붙여주면 해당 정보는 api로 불러오지않음
    public List<MemberDTO> membersV1(){ // 하지만 그러면 엔티티에 프레젠테이션 계층을 위한 로직이 추가되어버림. 엔티티로 의존관계가 들어와야하는데 나가버림
        // 엔티티가 바뀌면 API 스펙이 변한다.
        return memberService.findMembers(); // JSON 형태에서는 Array를 이렇게 표현 - [{}, {} ...] 하지만 Array 형태는 다른 값을 추가할 수 없다. -> 에를 들어 count 라는 필드를 추가하고 싶어도 못넣음
        /* [
                "data":[{}..{}..],
                "count": 5
                ..

                객체형태로 {} 안에 감싸져야 안에 추가할 수 있음.
          */
    }

    @GetMapping("/api/v2/members")
    public Result membersV2() {
        List<MemberDTO> findMembers = memberService.findMembers();

        List<MemberDto> collect = findMembers.stream()
                .map(m -> new MemberDto(m.getName())) // API 스펙이 변경되지는 않는다.
                .collect(Collectors.toList()); // API에 노출하는 대상이 DTO 이므로 API 와 1:1로 매핑이된다.

        return new Result(collect.size(), collect); // Result ��� �����⸦ �����ְ� data �ʵ忡 ������ collect ����Ʈ�� ������.
    }

    @Data
    @AllArgsConstructor
    static class Result<T> { // 오브젝트여서 Result 라는 껍데기를 씌워준다. 안그러면 JSON에서 바로 배열타입으로 나가기 때문에 Object 로 만들어줘야함.
        private int count; // count 가 JSON {}안에 들어간다!!!!!
        private T data;
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String name;
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
            @RequestBody @Valid UpdateMemberRequest request){ // id를 request를 날린다. name�� �Ѿ��.

        // 수정할 땐 가급적이면 변경감지 기능을 사용하자.
        // 커맨드와 쿼리를 분리함.
        memberService.update(id, request.getName()); // 커맨드 : 변경
        MemberDTO findMember = memberService.findOne(id); // 쿼리 : 조회
        return new UpdateMemberResponse(findMember.getId(), findMember.getName());
    }

    @Data
    static class UpdateMemberRequest {
        private String name;
    }

    // Entity 에는 Lombok 어노테이션 사용을 자제하지만,
    @Data // DTO 에는 Lombok 을 막씀. 크게 로직이 있는게 아니고 데이터만 오고 가기 때문에 실용적인 관점에서 자주 사용!
    @AllArgsConstructor // 모든 파라미터를 다 넘겨주는 생성자가 필요함!
    static class UpdateMemberResponse {
        private Long id;
        private String name;
    }

    @Data
    static class CreateMemberRequest {
        @NotEmpty // validation
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
