package com.debuggeandoideas.airdnd.services;

import com.debuggeandoideas.airdnd.Services.RoomService;
import com.debuggeandoideas.airdnd.repositories.RoomRepository;
import com.debuggeandoideas.airdnd.utils.DataDummy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    //El @Mock es una anotacion de Mockito que nos permite crear un objeto mock
//    @Mock
    private RoomRepository roomRepositoryMock;
    //El @InjectMocks es una anotacion de Mockito que nos permite inyectar el mock en el objeto real
//    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void init() {
        roomRepositoryMock = spy(RoomRepository.class);
        roomService = new RoomService(roomRepositoryMock);
    }

//    This is the same as @Mock
//    @BeforeEach
//    void init() {
//        //Aqui inicializamos una nueva instancia de RoomRepository
//        //roomRepository = new RoomRepository();
//        //Aqui inicializamos una nueva instancia utilizando Mockito
//        //La diferencia es de que ya esto es un objeto fake
//        roomRepositoryMock = mock(RoomRepository.class);
//        roomService = new RoomService(roomRepositoryMock);
//    }

    //The default values of Mockito are as follows:
    // 1.- List, Set, Map, ArrayList, HashMap, etc. the value is: []
    // 2.- Integer, Long, Double, Float, etc. the value is: 0
    // 3.- Boolean the value is: false
    // 4.- String the value is: null
    // 5.- Object the value is: null
    // 6.- Void the value is: null
    @Test
    @DisplayName("Should all rooms be available in room repository")
    void findAllAvailableRooms() {
        when(roomRepositoryMock.findAll())
                .thenReturn(DataDummy.default_rooms);
        var expected = 3;
        var result = roomService.findAllAvailableRooms();

        assertEquals(expected, result.size());
    }



}
