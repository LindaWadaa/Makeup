package com.makeup.entities;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "nommakeup", types = Makeup.class)
public interface MakeupProjection {

    String getNom();
}

//http://localhost:8081/makeup/rest?projection=nommakeup