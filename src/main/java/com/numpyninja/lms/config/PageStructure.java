package com.numpyninja.lms.config;

import com.numpyninja.lms.model.PageInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
@ConfigurationProperties(prefix = "layout")
@Getter
@Setter
public class PageStructure {
    private Map<String, PageInfo> pageInfo;
}
