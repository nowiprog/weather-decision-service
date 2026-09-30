package org.iis24.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.util.LinkedList;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

public class RobustShouldIGoToServiceTest {
    RobustShouldIGoToService testingModel;

    @Test
    void shouldRaiseInvalidServiceInstanceExceptionWhenListsContainNullReferences(){
        LinkedList<IMapService> ms = new LinkedList<IMapService>();
        LinkedList<IWeatherService> ws = new LinkedList<IWeatherService>();
        ms.add(0, null);
        assertThrows(InvalidServiceInstanceException.class, () ->
                testingModel = new RobustShouldIGoToService(ms, ws));
    }

    @Test
    void shouldRaiseInvalidMapServiceExecutionExceptionIfMapServiceFails(){
        LinkedList<IMapService> ms = new LinkedList<IMapService>();
        LinkedList<IWeatherService> ws = new LinkedList<IWeatherService>();
        IMapService dummy = mock(IMapService.class);
        when(dummy.getCoordinates("Unexistent")).thenThrow(RuntimeException.class);
        ms.add(0, dummy);
        testingModel = new RobustShouldIGoToService(ms, ws);
        assertThrows(InvalidMapServiceExecutionException.class, () ->
                testingModel.shouldIGoTo("Unexistent", LocalDate.now()));
    }

    @Test
    void shouldInvokeWeatherServiceWithValidCoordinatesIfOneMapServiceWorksWell(){
        LinkedList<IMapService> ms = new LinkedList<>();
        LinkedList<IWeatherService> ws = new LinkedList<>();

        IMapService dummyMap = mock(IMapService.class);
        when(dummyMap.getCoordinates("Existent"))
                .thenReturn(new GPSCoordinates(5.5, 5.5));
        ms.add(dummyMap);

        IWeatherService dummyWeather = mock(IWeatherService.class);
        ws.add(dummyWeather);

        testingModel = new RobustShouldIGoToService(ms, ws);

        LocalDate date = LocalDate.of(2027, 1, 8);

        when(dummyWeather.rainProbability(any(GPSCoordinates.class), eq(date)))
                .thenReturn(0.5);
        when(dummyWeather.totalAmountOfRain(any(GPSCoordinates.class), eq(date)))
                .thenReturn(0.5);

        testingModel.shouldIGoTo("Existent", date);

        verify(dummyWeather, atLeastOnce())
                .rainProbability(any(GPSCoordinates.class), eq(date));
        verify(dummyWeather, atLeastOnce())
                .totalAmountOfRain(any(GPSCoordinates.class), eq(date));
    }

    @Test
    void shouldReturnTrueWhenAverageOfProbabilitiesAndTotalAmountOfRainAreZero(){
        LinkedList<IMapService> ms = new LinkedList<>();
        LinkedList<IWeatherService> ws = new LinkedList<>();

        IMapService dummyMap = mock(IMapService.class);
        when(dummyMap.getCoordinates("Existent"))
                .thenReturn(new GPSCoordinates(5.5, 5.5));
        ms.add(dummyMap);

        IWeatherService dummyWeather = mock(IWeatherService.class);
        ws.add(dummyWeather);

        testingModel = new RobustShouldIGoToService(ms, ws);

        LocalDate date = LocalDate.of(2027, 1, 8);

        when(dummyWeather.rainProbability(any(GPSCoordinates.class), eq(date)))
                .thenReturn(0.0);
        when(dummyWeather.totalAmountOfRain(any(GPSCoordinates.class), eq(date)))
                .thenReturn(0.0);

        assertTrue(testingModel.shouldIGoTo("Existent", date));
    }

    @Test
    void shouldReturnFalseWhenAverageOfProbabilitiesAndTotalAmountOfRainAreEqualToThresholds(){
        LinkedList<IMapService> ms = new LinkedList<>();
        LinkedList<IWeatherService> ws = new LinkedList<>();

        IMapService dummyMap = mock(IMapService.class);
        when(dummyMap.getCoordinates("Existent"))
                .thenReturn(new GPSCoordinates(5.5, 5.5));
        ms.add(dummyMap);

        IWeatherService dummyWeather = mock(IWeatherService.class);
        ws.add(dummyWeather);

        testingModel = new RobustShouldIGoToService(ms, ws);
        testingModel.setRainProbabilityThreshold(5.0);
        testingModel.setRainAmountThreshold(10.0);

        LocalDate date = LocalDate.of(2027, 1, 8);

        when(dummyWeather.rainProbability(any(GPSCoordinates.class), eq(date)))
                .thenReturn(5.0);
        when(dummyWeather.totalAmountOfRain(any(GPSCoordinates.class), eq(date)))
                .thenReturn(10.0);

        assertFalse(testingModel.shouldIGoTo("Existent", date));
    }

    @Test
    void shouldInvokeAllAvailableWeatherServices(){
        LinkedList<IMapService> ms = new LinkedList<>();
        LinkedList<IWeatherService> ws = new LinkedList<>();

        IMapService dummyMap = mock(IMapService.class);
        when(dummyMap.getCoordinates("Existent"))
                .thenReturn(new GPSCoordinates(5.5, 5.5));
        ms.add(dummyMap);

        IWeatherService w1 = mock(IWeatherService.class);
        IWeatherService w2 = mock(IWeatherService.class);
        IWeatherService w3 = mock(IWeatherService.class);
        IWeatherService w4 = mock(IWeatherService.class);
        IWeatherService w5 = mock(IWeatherService.class);
        ws.add(w1);
        ws.add(w2);
        ws.add(w3);
        ws.add(w4);
        ws.add(w5);

        testingModel = new RobustShouldIGoToService(ms, ws);

        LocalDate date = LocalDate.of(2027, 1, 8);

        when(w1.rainProbability(any(), eq(date))).thenReturn(0.5);
        when(w2.rainProbability(any(), eq(date))).thenReturn(0.5);
        when(w3.rainProbability(any(), eq(date))).thenReturn(0.5);
        when(w4.rainProbability(any(), eq(date))).thenReturn(0.5);
        when(w5.rainProbability(any(), eq(date))).thenReturn(0.5);

        when(w1.totalAmountOfRain(any(), eq(date))).thenReturn(10.0);
        when(w2.totalAmountOfRain(any(), eq(date))).thenReturn(10.0);
        when(w3.totalAmountOfRain(any(), eq(date))).thenReturn(10.0);
        when(w4.totalAmountOfRain(any(), eq(date))).thenReturn(10.0);
        when(w5.totalAmountOfRain(any(), eq(date))).thenReturn(10.0);

        testingModel.shouldIGoTo("Existent", date);

        for (IWeatherService w : ws) {
            verify(w, atLeastOnce()).rainProbability(any(), eq(date));
            verify(w, atLeastOnce()).totalAmountOfRain(any(), eq(date));
        }
    }


}
