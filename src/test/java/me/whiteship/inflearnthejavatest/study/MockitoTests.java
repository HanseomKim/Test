package me.whiteship.inflearnthejavatest.study;

import me.whiteship.inflearnthejavatest.domain.Member;
import me.whiteship.inflearnthejavatest.domain.Study;
import me.whiteship.inflearnthejavatest.member.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MockitoTests {

    @Mock
    MemberService memberService;

    @Mock StudyRepository studyRepository;

    @Test
    void createStudyService() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        assertNotNull(studyService);
    }

    @Test
    void stubbingTest() {
        // member 정의
        Member member = new Member();
        member.setId(1L);
        member.setEmail("hello@mockito.com");

        // 특정한 매개변수 1L을 받은 경우 앞서 정의한 member를 반환합니다.
        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        // 특정한 매개변수 1L을 받은 경우 예외를 던집니다.
        // when(memberService.findById(1L)).thenThrow(RuntimeException.class);

        // 결과를 확인합니다.
        Optional<Member> findMember = memberService.findById(1L);
        assertEquals("hello@mockito.com", findMember.get().getEmail());
    }

    @Test
    void stubbingVoidTest() {
        doThrow(new IllegalArgumentException()).when(memberService).validate(1L);
        assertThrows(IllegalArgumentException.class, () -> {
            memberService.validate(1L);
        });
    }

    @Test
    void stubbingTest2() {
        // member 정의
        Member member = new Member();
        member.setId(1L);
        member.setEmail("hello@mockito.com");

        /** memberService의 findById 메소드가
         * 처음 호출되면 정상적으로 정의한 member를 반환합니다.
         * 두번째 호출되면 RuntimeException이 발생합니다.
         * 세번째 호출되면 빈 값을 반환합니다.
         */
        when(memberService.findById(any()))
                .thenReturn(Optional.of(member))
                .thenThrow(new RuntimeException())
                .thenReturn(Optional.empty());

        // 결과를 확인합니다.
        Optional<Member> findMember = memberService.findById(1L);
        assertEquals("hello@mockito.com", findMember.get().getEmail());

        assertThrows(RuntimeException.class, () -> {
           memberService.findById(10L);
        });

        assertEquals(Optional.empty(), memberService.findById(100L));
    }

    @Test
    void verifyTest() {
        StudyService studyService = new StudyService(memberService, studyRepository);
        // member 정의
        Member member = new Member();
        member.setId(1L);
        member.setEmail("hello@mockito.com");
        // study 정의
        Study study = new Study(1, "Java");

        when(memberService.findById(1L)).thenReturn(Optional.of(member));
        when(studyRepository.save(study)).thenReturn(study);

        studyService.createNewStudy(1L, study);

        // study 매개변수로 1번 호출되었는지 확인합니다.
        verify(memberService, times(1)).notify(study);
        // notify 메소드 호출 이후 아무 일도 하지 않았는지 확인합니다.
        verifyNoMoreInteractions(memberService);

        // study 호출 이후 member로 호출 되었는지 순서를 확인합니다.
/*
        InOrder inOrder = inOrder(memberService);
        inOrder.verify(memberService).notify(study);
        inOrder.verify(memberService).notify(member);
*/
    }
}
