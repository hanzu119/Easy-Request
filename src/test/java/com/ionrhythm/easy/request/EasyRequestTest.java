package com.ionrhythm.easy.request;

import org.junit.Before;

/**
 * @author AGPg
 */
public class EasyRequestTest {
    private EasyTestClient client;
    private static final Long success_id = 0L;
    private static final String Success_name = "zs";
    private static final Long Error_id = 1L;
    private static final String Error_name = "ls";

    @Before
    public void init() {
        client = EasyRequestBuilder.build(EasyTestClient.class);
    }


}
