package com.debuggeandoideas.airdnd.services;

import com.debuggeandoideas.airdnd.Services.BookingService;
import com.debuggeandoideas.airdnd.Services.PaymentService;
import com.debuggeandoideas.airdnd.Services.RoomService;
import com.debuggeandoideas.airdnd.dto.BookingDto;
import com.debuggeandoideas.airdnd.helpers.MailHelper;
import com.debuggeandoideas.airdnd.repositories.BookingRepository;
import com.debuggeandoideas.airdnd.utils.DataDummy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private RoomService roomServiceMock;
    //La diferencia entre @Mock y @Spy es que @Mock crea un objeto falso
    // y @Spy crea un objeto real pero con la posibilidad de
    // espiar los metodos de la clase real
    //El @Spy es una anotacion de Mockito que nos permite crear un objeto espia
    //@Spy
    @Mock
    private BookingRepository bookingRepositoryMock;
    @Mock
    private MailHelper mailHelperMock;
    @InjectMocks
    private BookingService bookingService;
    //El @Captor es una anotacion de Mockito que nos permite crear un objeto captor
    //El captor es un objeto que nos permite capturar los argumentos
    //que se pasan a un metodo
    @Captor
    private ArgumentCaptor<String> stringCapture;

    @Test
    @DisplayName("get Available Place Count should works")
    void getAvailablePlaceCount() {
        when(roomServiceMock.findAllAvailableRooms())
                .thenReturn(DataDummy.default_rooms_list)
                .thenReturn(DataDummy.silgle_rooms_list)
                .thenReturn(Collections.emptyList());
                //*.thenThrow(new IllegalStateException("Error"));*/ <-- Tambien se puede agregar excepciones a la cadena de returns
        var expected1 = 14;
        var expected2 = 5;
        var expected3 = 0;
        var result1 = bookingService.getAvailablePlaceCount();
        var result2 = bookingService.getAvailablePlaceCount();
        var result3 = bookingService.getAvailablePlaceCount();

        assertAll(
                () -> assertEquals(expected1, result1),
                () -> assertEquals(expected2, result2),
                () -> assertEquals(expected3, result3)
        );
    }

    @Test
    @DisplayName("booking happy path should works")
    void bookingHappyPath() {
        final var roomId = UUID.randomUUID().toString();
        //Arguments matcher strict
//        when(roomServiceMock.findAvailableRoom(DataDummy.default_booking_req_1))
//                .thenReturn(DataDummy.default_rooms_list.get(0));
//        when(bookingRepositoryMock.save(DataDummy.default_booking_req_1))
//                .thenReturn(roomId);

//        //Arguments matcher any
//        when(roomServiceMock.findAvailableRoom(any(BookingDto.class)))
//                .thenReturn(DataDummy.default_rooms_list.get(0));
//        when(bookingRepositoryMock.save(any(BookingDto.class)))
//                .thenReturn(roomId);

//      Otra forma de hacerlo es:
        doReturn(DataDummy.default_rooms_list.get(0))
                .when(roomServiceMock).findAvailableRoom(DataDummy.default_booking_req_1);
        doReturn(roomId)
                .when(bookingRepositoryMock).save(DataDummy.default_booking_req_1);

        // el doNothing es para que no haga nada y es
        // para los metodos que no devuelven nada (void)
        doNothing()
                .when(roomServiceMock).bookRoom(anyString());

        var result = bookingService.booking(DataDummy.default_booking_req_1);
        assertEquals(roomId, result);

        //Verificamos que se llamo a los metodos
        verify(roomServiceMock,times(1)).findAvailableRoom(any(BookingDto.class));
        verify(bookingRepositoryMock, times(1)).save(any(BookingDto.class));
        verify(roomServiceMock, times(1)).bookRoom(anyString());
    }

    @Test
    @DisplayName("booking unhappy path should works")
    void bookingUnHappyPath() {
        final var roomId = UUID.randomUUID().toString();
        doReturn(DataDummy.default_rooms_list.get(0))
                .when(roomServiceMock).findAvailableRoom(DataDummy.default_booking_req_4);
        doThrow(new IllegalArgumentException("Max 3 guests"))
                .when(paymentServiceMock).pay(eq(DataDummy.default_booking_req_4), eq(320.00));
        //Otra forma de hacerlo es:
        /*when(paymentServiceMock.pay(any(BookingDto.class), anyDouble()))
                .thenThrow(new IllegalArgumentException("Max 3 guests"));*/

        Executable executable = () -> bookingService.booking(DataDummy.default_booking_req_4);
        assertThrows(IllegalArgumentException.class, executable);
    }

    @Test
    @DisplayName("unbook should works")
    void unbook() {
        //given
        var id1 = "id1";
        var id2 = "id2";


        var bookingRes1 = DataDummy.default_booking_req_1;
        bookingRes1.setRoom(DataDummy.default_rooms_list.get(3));

        var bookingRes2 = DataDummy.default_booking_req_2;
        bookingRes2.setRoom(DataDummy.default_rooms_list.get(4));

        //when
        when(this.bookingRepositoryMock.findById(anyString()))
                .thenReturn(bookingRes1)
                .thenReturn(bookingRes2);

        doNothing()
                .when(this.roomServiceMock).unbookRoom(anyString());

        doNothing()
                .when(this.bookingRepositoryMock).deleteById(anyString());

        this.bookingService.unbook(id1);
        this.bookingService.unbook(id2);

        //then
        verify(this.roomServiceMock, times(2)).unbookRoom(anyString());
        verify(this.bookingRepositoryMock, times(2)).deleteById(anyString());
        verify(this.bookingRepositoryMock, times(2)).findById(this.stringCapture.capture());

        System.out.println("captured argument: " + this.stringCapture.getAllValues());

        assertEquals(List.of( "id1",  "id2"), this.stringCapture.getAllValues());
    }
}
