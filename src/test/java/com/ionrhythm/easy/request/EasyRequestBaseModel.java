package com.ionrhythm.easy.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author AGPg
 */
@Data
public class EasyRequestBaseModel implements Serializable {
    private static final long serialVersionUID = 2382276397113804334L;
    private String test;
}
