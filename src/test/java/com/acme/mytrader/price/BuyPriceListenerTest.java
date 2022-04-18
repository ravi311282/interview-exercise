package com.acme.mytrader.price;

import com.acme.mytrader.execution.ExecutionService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

import static com.acme.mytrader.strategy.SecurityDTOTest.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class BuyPriceListenerTest {

    @InjectMocks
    ExecutionService executionService;
    @InjectMocks
    BuyPriceListener buyPriceListener;

    @Before
    public void setUp() {
        executionService = Mockito.mock(ExecutionService.class);
        buyPriceListener = new BuyPriceListener(IBM_SECURITY, PRICE_THRESHOLD_VALUE, 100, executionService,
                false);
    }

    @Test
    public void testInitializeStateForBuyPriceListener() {

        assertThat(buyPriceListener.getSecurity()).isEqualTo(IBM_SECURITY);
        assertThat(buyPriceListener.getTriggerLevel()).isEqualTo(PRICE_THRESHOLD_VALUE);
        assertThat(buyPriceListener.getQuantityToPurchase()).isEqualTo(100);
        assertThat(buyPriceListener.isTradeExecuted()).isFalse();
    }

    @Test
    public void testBuy_whenThresholdIsMet() {
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> acDouble = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> acInteger = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListener(IBM_SECURITY, PRICE_THRESHOLD_VALUE, 100, executionService,
                false);
        buyPriceListener.priceUpdate(IBM_SECURITY, 25.00);

        verify(executionService, times(1))
                .buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(acString.getValue()).as("Should be IBM ")
                .isEqualTo(IBM_SECURITY);
        assertThat(acDouble.getValue()).as("Should be the value less than 50.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(acInteger.getValue()).as("Should be the volume purchased").isEqualTo(100);
        assertThat(buyPriceListener.isTradeExecuted())
                .as("Should be the trade is successfully executed").isTrue();
    }

    @Test
    public void testShouldNotBuy_whenThresholdIsNotMet() {
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> acDouble = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> acInteger = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener.priceUpdate(IBM_SECURITY, 55.00);

        verify(executionService, times(0))
                .buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(buyPriceListener.isTradeExecuted())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    public void testShouldNotBuy_whenSecurityIsDifferent() {
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> acDouble = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> acInteger = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListener(APPL_SECURITY, PRICE_THRESHOLD_VALUE, 100, executionService,
                false);
        buyPriceListener.priceUpdate(IBM_SECURITY, 55.00);

        verify(executionService, times(0))
                .buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(buyPriceListener.isTradeExecuted())
                .as("Should be the trade is not successfully executed").isFalse();
    }

    @Test
    public void testGivenSeveralPriceUpdates_whenTradeIsAlreadyExecucted_shouldBuyOnlyOnce() {
        ExecutionService executionService = Mockito.mock(ExecutionService.class);
        ArgumentCaptor<String> acString = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> acDouble = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> acInteger = ArgumentCaptor.forClass(Integer.class);

        buyPriceListener = new BuyPriceListener(IBM_SECURITY, PRICE_THRESHOLD_VALUE, 100, executionService,
                false);
        buyPriceListener.priceUpdate(IBM_SECURITY, 25.00);
        buyPriceListener.priceUpdate(IBM_SECURITY, 10.00);
        buyPriceListener.priceUpdate(IBM_SECURITY, 35.00);

        verify(executionService, times(1))
                .buy(acString.capture(), acDouble.capture(), acInteger.capture());
        assertThat(acString.getValue()).as("Should be IBM ")
                .isEqualTo(IBM_SECURITY);
        assertThat(acDouble.getValue()).as("Should be the value less than 50.00, that is 25.00")
                .isEqualTo(25.00);
        assertThat(acInteger.getValue()).as("Should be the volume purchased").isEqualTo(100);
        assertThat(buyPriceListener.isTradeExecuted())
                .as("Should be the trade is successfully executed").isTrue();
    }
}