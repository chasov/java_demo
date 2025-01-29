package ru.t1.java.demo.web;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CheckWebClientTest {


    @Mock
    CheckWebClient checkWebClient;

//    @Test
//    void check() {
//
//        when(checkWebClient.check(1L))
//                .thenReturn(Optional.of(CheckResponse.builder()
//                .blocked(false)
//                .build()));
//
//        assertThat(checkWebClient.check(1L).get())
//                .isEqualTo(CheckResponse.builder()
//                        .blocked(false)
//                        .build());
//
//    }


}