package com.acme.mytrader.strategy;

import static com.acme.mytrader.strategy.SecurityDTOTest.APPL_SECURITY;
import static com.acme.mytrader.strategy.SecurityDTOTest.IBM_SECURITY;
import static com.acme.mytrader.strategy.SecurityDTOTest.PRICE_THRESHOLD_VALUE;
import static com.acme.mytrader.strategy.SecurityDTOTest.VOLUME;
import static com.acme.mytrader.strategy.SecurityDTOTest.getSecurityDTOTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

import com.acme.mytrader.execution.TradeExecutionService;
import com.acme.mytrader.price.PriceListener;
import com.acme.mytrader.price.PriceSourceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mockito;

public class TradingStrategyTest {

    private final Double THRESHOLD_VALUE = 25.00;

    @InjectMocks
    TradeExecutionService tradeExecutionService;
    @InjectMocks
    PriceSourceImpl priceSource;

    @Before
    public void setUp() {
        tradeExecutionService = Mockito.mock(TradeExecutionService.class);
        priceSource = new MockPriceSource(IBM_SECURITY, THRESHOLD_VALUE);
    }

    @SneakyThrows
    @Test
    public void testAutoBuyForSuccessfulBuy() {
        ArgumentCaptor<String> securityCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> priceCaptor = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<Integer> volumeCaptor = ArgumentCaptor.forClass(Integer.class);
        TradingStrategy tradingStrategy = new TradingStrategy(tradeExecutionService, priceSource);
        List<SecurityDTO> input = Arrays.asList(getSecurityDTOTest(IBM_SECURITY, PRICE_THRESHOLD_VALUE, VOLUME));
        tradingStrategy.autoBuy(input);
        verify(tradeExecutionService, times(1))
                .buy(securityCaptor.capture(), priceCaptor.capture(), volumeCaptor.capture());
        assertThat(securityCaptor.getValue()).isEqualTo(IBM_SECURITY);
        assertThat(priceCaptor.getValue()).isEqualTo(THRESHOLD_VALUE);
        assertThat(volumeCaptor.getValue()).isEqualTo(VOLUME);
    }

    @SneakyThrows
    @Test
    public void testAutoBuyForNotSuccessfulBuy() {
        TradingStrategy tradingStrategy = new TradingStrategy(tradeExecutionService, priceSource);
        List<SecurityDTO> input = Arrays.asList(getSecurityDTOTest(APPL_SECURITY, PRICE_THRESHOLD_VALUE, VOLUME));
        tradingStrategy.autoBuy(input);
        verifyZeroInteractions(tradeExecutionService);
    }

    private class MockPriceSource extends PriceSourceImpl {

        String security;
        double price;

        MockPriceSource(String security, double price) {
            this.security = security;
            this.price = price;
        }

        private final List<PriceListener> priceListeners = new CopyOnWriteArrayList<>();

        @Override
        public void addPriceListener(PriceListener listener) {
            priceListeners.add(listener);
        }

        @Override
        public void removePriceListener(PriceListener listener) {
            priceListeners.remove(listener);
        }

        @Override
        public void run() {
            priceListeners.forEach(priceListener -> priceListener.priceUpdate(security, price));
        }
    }
}