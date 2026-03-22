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
        registry.addConverter(new Converter<String, StatusConsulta>() {
            @Override
            public StatusConsulta convert(String source) {
                return source == null ? null : StatusConsulta.fromValue(source);
            }
        });

        registry.addConverter(new Converter<String, TipoConsulta>() {
            @Override
            public TipoConsulta convert(String source) {
                return source == null ? null : TipoConsulta.fromValue(source);
            }
        });

        registry.addConverter(new Converter<String, MeioPagamento>() {
            @Override
            public MeioPagamento convert(String source) {
                return source == null ? null : MeioPagamento.fromValue(source);
            }
        });
    }
}