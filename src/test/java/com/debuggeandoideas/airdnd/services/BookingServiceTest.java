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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    private PaymentService paymentServiceMock;
    @Mock
    private RoomService roomServiceMock;
    @Mock
    private BookingRepository bookingRepositoryMock;
    @Mock
    private MailHelper mailHelperMock;
    @InjectMocks
    private BookingService bookingService;

    @Test
    @DisplayName("get Available Place Count should works")
    void getAvailablePlaceCount() {
        when(roomServiceMock.findAllAvailableRooms())
                .thenReturn(DataDummy.default_rooms_list);

        assertEquals(14, bookingService.getAvailablePlaceCount());
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
}
