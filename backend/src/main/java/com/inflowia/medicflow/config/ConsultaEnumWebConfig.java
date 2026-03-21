package com.inflowia.medicflow.config;

import com.inflowia.medicflow.domain.consulta.MeioPagamento;
import com.inflowia.medicflow.domain.consulta.StatusConsulta;
import com.inflowia.medicflow.domain.consulta.TipoConsulta;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConsultaEnumWebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<String, StatusConsulta>) StatusConsulta::fromValue);
        registry.addConverter((Converter<String, TipoConsulta>) TipoConsulta::fromValue);
        registry.addConverter((Converter<String, MeioPagamento>) MeioPagamento::fromValue);
    }
}
