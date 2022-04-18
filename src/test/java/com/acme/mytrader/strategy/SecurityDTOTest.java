package com.acme.mytrader.strategy;

public class SecurityDTOTest {

    public static final String IBM_SECURITY = "IBM";
    public static final String APPL_SECURITY = "APPL";
    public static final Double PRICE_THRESHOLD_VALUE = 50.00;
    public static final int VOLUME = 10;

    public static SecurityDTO getSecurityDTOTest(String security, Double thresholdValue, int volumeCount) {
        SecurityDTO securityDTO = new SecurityDTO(security, thresholdValue, volumeCount);
        securityDTO.builder().security(security);
        securityDTO.builder().priceThreshold(thresholdValue);
        securityDTO.builder().volume(volumeCount);

        return securityDTO;
    }

}