package com.ionrhythm.easy.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author AGPg
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EasyRequestModel extends EasyRequestBaseModel {
    private static final long serialVersionUID = 3135584978802001430L;
    private long id;
    private String name;
}
