package jpabook_practice.jpashop_practice;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.hibernate.Hibernate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopPracticeApplication {

	public static void main(String[] args) {

		SpringApplication.run(JpashopPracticeApplication.class, args);
	}
	// groupId : 자신의 프로젝트를 고유하게 식별하게 해주는것. 내가 컨트롤하는 domain name
	// artifactId : 제품의 이름. 버전 정보를 생략한 jar 파일의 이름이다. 프로젝트 이름과 동일하게 설정한다. 소문자만 작성, 특수문자x
	// version : SNAPSHOT - 개발용, RELEASE - 배포용, 숫자와 점을 사용해 버전 형태 표현

	// 컨트롤 + 알트 + P : 메소드에서 변수를 파라미터로 추가함
	// 컨트롤 + 쉬프트 + 방향키 위/아래 : 코드블럭을 위/아래로 움직일 수 있음

	// API :: JSON 라이브러리에게 프록시 객체는 JSON 으로 바꾸지 말라고 해줌
	// => Hibernate5Module 을 설치
	// 그리고 빈으로 등록
	// 하지만 어차피 이 방법은 엔티티를 외부로 노출시키는 방법이므로, 어차피 실무에선 사용X
	@Bean
	Hibernate5Module hibernate5Module(){
		// 기본 설정 : LAZY 로딩을 해서 프록시가 정상적으로 초기화 된 데이터만 API가 반환함
		Hibernate5Module hibernate5Module = new Hibernate5Module();
		/*
		hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		하면 강제로 JSON 생성 시점에 프록시도 모두 읽어와버림.
		 */
		return new Hibernate5Module();
	}
}
