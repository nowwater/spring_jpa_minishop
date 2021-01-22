package jpabook_practice.jpashop_practice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JpashopPracticeApplication {

	public static void main(String[] args) {

		SpringApplication.run(JpashopPracticeApplication.class, args);
	}
	// groupId : 자신의 프로젝트를 고유하게 식별하게 해주는것. 내가 컨트롤하는 domain name
	// artifactId : 제품의 이름. 버전 정보를 생략한 jar 파일의 이름이다. 프로젝트 이름과 동일하게 설정한다. 소문자만 작성, 특수문자x
	// version : SNAPSHOT - 개발용, RELEASE - 배포용, 숫자와 점을 사용해 버전 형태 표현 
}
