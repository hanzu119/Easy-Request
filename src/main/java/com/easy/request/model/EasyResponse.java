package com.easy.request.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author AGPg
 */
@Data
public class EasyResponse<T> implements Serializable {
    private static final long serialVersionUID = -939023015418606076L;
    private Integer code;
    private String reason;
    private String originEntity;
    private T entity;
}
