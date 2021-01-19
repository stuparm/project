package com.stuparm.project.openapi;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Parameters({
        @Parameter(name = "page", schema = @Schema(type = "integer", format = "int32"), in= ParameterIn.QUERY, description = "Results page you want to retrieve (0..N)."),
        @Parameter(name = "size", schema = @Schema(type = "integer", format = "int32"), in= ParameterIn.QUERY, description = "Number of records per page. Default is 20")//,
        //@Parameter(name = "sort", schema = @Schema(type = "string"), in= ParameterIn.QUERY, description = "Sorting criteria in the format: property(,asc|desc). Example: ?sort=iso,asc")
})
public @interface OpenApiPageable {


}
