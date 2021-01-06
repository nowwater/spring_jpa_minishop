package jpabook_practice.jpashop_practice.service;

import jpabook_practice.jpashop_practice.controller.MemberDTO;
import jpabook_practice.jpashop_practice.domain.Member;
import jpabook_practice.jpashop_practice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    public void validateDuplicateMember(Member member){
        List<Member> all = memberRepository.findByName(member.getName());
        if(!all.isEmpty())
            throw new IllegalStateException("이미 등록된 회원입니다.");
    }

    public List<MemberDTO> getMemberDTO(){
        List<Member> members = memberRepository.findAll();
        List<MemberDTO> memListDTO = new ArrayList<MemberDTO>();
        for(Member member: members){
            memListDTO.add(new MemberDTO(member.getName(), member.getId(), member.getAddress()));
        }
        return memListDTO;
    }

    public List<MemberDTO> findMembers(){
        List<Member> all = memberRepository.findAll();
        List<MemberDTO> memberDTOS = new ArrayList<>();
        for(Member member : all){
            memberDTOS.add(new MemberDTO(member.getName(), member.getId(), member.getAddress()));
        }
        return memberDTOS;
    }

    public MemberDTO findOne(Long memberId){
        Member member = memberRepository.findOne(memberId);
        MemberDTO memberDTO = new MemberDTO(member.getName(), member.getId(), member.getAddress());
        return memberDTO;
    }
}